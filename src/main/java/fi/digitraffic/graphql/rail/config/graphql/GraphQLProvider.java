package fi.digitraffic.graphql.rail.config.graphql;

import static graphql.language.NodeChildrenContainer.newNodeChildrenContainer;
import static graphql.language.ObjectTypeDefinition.CHILD_DIRECTIVES;
import static graphql.language.ObjectTypeDefinition.CHILD_FIELD_DEFINITIONS;
import static graphql.language.ObjectTypeDefinition.CHILD_IMPLEMENTZ;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import fi.digitraffic.graphql.rail.config.DigitrafficConfig;
import fi.digitraffic.graphql.rail.links.base.BaseLink;
import fi.digitraffic.graphql.rail.queries.BaseQuery;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NodeChildrenContainer;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

@Component
public class GraphQLProvider {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private GraphQL graphQL;

    @Autowired
    private DigitrafficConfig digitrafficConfig;

    @Autowired
    private List<BaseLink> fetchers;

    @Autowired
    private List<BaseQuery> rootFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    private Set<String> PRIMITIVE_TYPES = Set.of("Boolean", "String", "Date", "DateTime", "Int");
    private Map<String, String> fieldNameOrderByOverrides = Map.of("trainType", "TrainTypeOrderBy");
    private Map<String, String> fieldNameWhereOverrides = Map.of("trainType", "TrainTypeWhere");

    @PostConstruct
    public void init() throws IOException {
        final URL url = Resources.getResource("schema.graphqls");
        final String sdl = Resources.toString(url, Charsets.UTF_8);
        final GraphQLSchema graphQLSchema = buildSchema(sdl);

        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(new ChainedInstrumentation(Arrays.asList(
                        new ExecutionTimeInstrumentation(),
                        new NoCircularQueriesInstrumentation(digitrafficConfig)
                ))).build();
    }

    private GraphQLSchema buildSchema(final String sdl) {
        final TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);

        removeBlacklistedFields(typeRegistry);
        addGenericArgumentsToQueries(typeRegistry);
        addGenericArgumentsToCollections(typeRegistry);
        generateOrderByTypes(typeRegistry);
        generateWhereTypes(typeRegistry);
        generateCollectionWhereTypes(typeRegistry);

        final RuntimeWiring runtimeWiring = buildWiring();
        final SchemaGenerator schemaGenerator = new SchemaGenerator();

        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private void addGenericArgumentsToCollections(TypeDefinitionRegistry typeRegistry) {
        for (Map.Entry<String, TypeDefinition> typeEntry : typeRegistry.types().entrySet()) {
            TypeDefinition value = typeEntry.getValue();
            if (value instanceof ObjectTypeDefinition && !value.getName().equals("Query")) {
                ObjectTypeDefinition objectTypeDefinition = (ObjectTypeDefinition) value;

                List<FieldDefinition> newFieldDefinitions = new ArrayList<>();
                for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
                    if (fieldDefinition.getType() instanceof ListType) {
                        ListType listType = (ListType) fieldDefinition.getType();
                        TypeName childType = (TypeName) listType.getType();
                        if (childType.getName().equals("Float")) {
                            newFieldDefinitions.add(fieldDefinition);
                        } else {
                            List<InputValueDefinition> inputValueDefinitions = new ArrayList<>(fieldDefinition.getInputValueDefinitions());
                            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("where").type(TypeName.newTypeName(childType.getName() + "Where").build()).build());
                            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("skip").type(TypeName.newTypeName("Int").build()).build());
                            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("take").type(TypeName.newTypeName("Int").build()).build());
                            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("orderBy").type(ListType.newListType(TypeName.newTypeName(childType.getName() + "OrderBy").build()).build()).build());

                            newFieldDefinitions.add(
                                    FieldDefinition.newFieldDefinition()
                                            .name(fieldDefinition.getName())
                                            .type(fieldDefinition.getType())
                                            .inputValueDefinitions(inputValueDefinitions)
                                            .build()
                            );
                        }
                    } else {
                        newFieldDefinitions.add(fieldDefinition);
                    }
                }

                NodeChildrenContainer children = newNodeChildrenContainer()
                        .children(CHILD_FIELD_DEFINITIONS, newFieldDefinitions)
                        .build();
                typeRegistry.remove(objectTypeDefinition);
                typeRegistry.add(objectTypeDefinition.withNewChildren(children));
            }
        }
    }

    private void addGenericArgumentsToQueries(TypeDefinitionRegistry typeRegistry) {
        ObjectTypeDefinition oldQuery = null;
        ObjectTypeDefinition newQuery = null;
        for (Map.Entry<String, TypeDefinition> typeEntry : typeRegistry.types().entrySet()) {
            if (typeEntry.getKey().equals("Query")) {
                ObjectTypeDefinition queryObjectTypeDefinition = (ObjectTypeDefinition) typeEntry.getValue();
                oldQuery = queryObjectTypeDefinition;

                List<FieldDefinition> newFieldDefinitions = new ArrayList<>();
                for (FieldDefinition fieldDefinition : queryObjectTypeDefinition.getFieldDefinitions()) {
                    if (fieldDefinition.getType() instanceof ListType) {
                        ListType listType = (ListType) fieldDefinition.getType();
                        TypeName namedType = (TypeName) listType.getType();

                        List<InputValueDefinition> newInputValueDefinitions = new ArrayList<>(fieldDefinition.getInputValueDefinitions());
                        newInputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("where").type(TypeName.newTypeName(namedType.getName() + "Where").build()).build());
                        newInputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("skip").type(TypeName.newTypeName("Int").build()).build());
                        newInputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("take").type(TypeName.newTypeName("Int").build()).build());
                        newInputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("orderBy").type(ListType.newListType(TypeName.newTypeName(namedType.getName() + "OrderBy").build()).build()).build());

                        newFieldDefinitions.add(FieldDefinition.newFieldDefinition()
                                .type(fieldDefinition.getType())
                                .name(fieldDefinition.getName())
                                .inputValueDefinitions(newInputValueDefinitions)
                                .build());
                    } else {
                        newFieldDefinitions.add(fieldDefinition);
                    }
                }
                NodeChildrenContainer children = newNodeChildrenContainer()
                        .children(CHILD_FIELD_DEFINITIONS, newFieldDefinitions)
                        .build();
                newQuery = queryObjectTypeDefinition.withNewChildren(children);
            }
        }
        typeRegistry.remove(oldQuery);
        typeRegistry.add(newQuery);
    }

    private void generateWhereTypes(TypeDefinitionRegistry typeRegistry) {
        List<ObjectTypeDefinition> userTypes = new ArrayList<>();
        for (Map.Entry<String, TypeDefinition> typeEntry : typeRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition && !typeEntry.getValue().getName().equals("Query")) {
                userTypes.add((ObjectTypeDefinition) typeEntry.getValue());
            }
        }

        for (ObjectTypeDefinition userType : userTypes) {
            List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
            for (FieldDefinition fieldDefinition : userType.getFieldDefinitions()) {
                if (fieldDefinition.getType() instanceof ListType) {
                    String name = getTypeName(((ListType) fieldDefinition.getType()).getType()).get();
                    if (!name.equals("Float")) {
                        String fieldName = fieldDefinition.getName();
                        Type type = TypeName.newTypeName(name + "CollectionWhere").build();

                        InputValueDefinition inputValueDefinition = InputValueDefinition.newInputValueDefinition()
                                .type(type)
                                .name(fieldName)
                                .build();
                        inputValueDefinitions.add(inputValueDefinition);
                    }
                } else {
                    Optional<String> typeName = getTypeName(fieldDefinition.getType());
                    if (typeName.isPresent()) {
                        String name = typeName.get();
                        String fieldName = fieldDefinition.getName();

                        Type type;
                        if (fieldNameWhereOverrides.containsKey(fieldName)) {
                            type = TypeName.newTypeName(fieldNameWhereOverrides.get(fieldName)).build();
                        } else if (PRIMITIVE_TYPES.contains(name)) {
                            ;
                            type = TypeName.newTypeName(typeName.get() + "Where").build();
                        } else if (name.endsWith("Type")) {
                            type = TypeName.newTypeName("EnumWhere").build();
                        } else {
                            type = TypeName.newTypeName(name + "Where").build();
                        }

                        InputValueDefinition inputValueDefinition = InputValueDefinition.newInputValueDefinition()
                                .type(type)
                                .name(fieldName)
                                .build();
                        inputValueDefinitions.add(inputValueDefinition);
                    }
                }
            }

            TypeName typeName = TypeName.newTypeName(userType.getName() + "Where").build();
            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("and").type(ListType.newListType().type(typeName).build()).build());
            inputValueDefinitions.add(InputValueDefinition.newInputValueDefinition().name("or").type(ListType.newListType().type(typeName).build()).build());

            InputObjectTypeDefinition whereType = InputObjectTypeDefinition.newInputObjectDefinition()
                    .name(userType.getName() + "Where")
                    .inputValueDefinitions(inputValueDefinitions).build();
            typeRegistry.add(whereType);
        }
    }

    private void generateCollectionWhereTypes(TypeDefinitionRegistry typeRegistry) {
        List<ObjectTypeDefinition> userTypes = new ArrayList<>();
        for (Map.Entry<String, TypeDefinition> typeEntry : typeRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition && !typeEntry.getValue().getName().equals("Query")) {
                userTypes.add((ObjectTypeDefinition) typeEntry.getValue());
            }
        }

        for (ObjectTypeDefinition userType : userTypes) {
            InputValueDefinition inputValueDefinition = InputValueDefinition.newInputValueDefinition()
                    .type(TypeName.newTypeName(userType.getName() + "Where").build())
                    .name("contains")
                    .build();

            InputObjectTypeDefinition whereType = InputObjectTypeDefinition.newInputObjectDefinition()
                    .name(userType.getName() + "CollectionWhere")
                    .inputValueDefinition(inputValueDefinition).build();
            typeRegistry.add(whereType);
        }
    }

    private void generateOrderByTypes(TypeDefinitionRegistry typeRegistry) {
        List<ObjectTypeDefinition> userTypes = new ArrayList<>();
        for (Map.Entry<String, TypeDefinition> typeEntry : typeRegistry.types().entrySet()) {
            if (typeEntry.getValue() instanceof ObjectTypeDefinition && !typeEntry.getValue().getName().equals("Query")) {
                userTypes.add((ObjectTypeDefinition) typeEntry.getValue());
            }
        }

        TypeName orderDirectionType = TypeName.newTypeName("OrderDirection").build();
        for (ObjectTypeDefinition userType : userTypes) {
            List<InputValueDefinition> inputValueDefinitions = new ArrayList<>();
            for (FieldDefinition fieldDefinition : userType.getFieldDefinitions()) {
                Optional<String> optionalName = getTypeName(fieldDefinition.getType());
                if (optionalName.isPresent()) {
                    String name = optionalName.get();
                    String fieldName = fieldDefinition.getName();

                    Type type;
                    if (fieldNameOrderByOverrides.containsKey(fieldName)) {
                        type = TypeName.newTypeName(fieldNameOrderByOverrides.get(fieldName)).build();
                    } else if (PRIMITIVE_TYPES.contains(name) || name.endsWith("Type")) {
                        type = orderDirectionType;

                    } else {
                        type = TypeName.newTypeName(name + "OrderBy").build();
                    }

                    InputValueDefinition inputValueDefinition = InputValueDefinition.newInputValueDefinition()
                            .type(type)
                            .name(fieldName)
                            .build();
                    inputValueDefinitions.add(inputValueDefinition);
                }
            }

            InputObjectTypeDefinition whereType = InputObjectTypeDefinition.newInputObjectDefinition()
                    .name(userType.getName() + "OrderBy")
                    .inputValueDefinitions(inputValueDefinitions).build();
            typeRegistry.add(whereType);
        }
    }

    private Optional<String> getTypeName(Type type) {
        if (type instanceof NonNullType) {
            Type nestedType = ((NonNullType) type).getType();
            if (nestedType instanceof TypeName) {
                return Optional.of(((TypeName) nestedType).getName());
            }
        } else if (type instanceof TypeName) {
            return Optional.of(((TypeName) type).getName());
        }
        return Optional.empty();
    }

    private void removeBlacklistedFields(final TypeDefinitionRegistry typeRegistry) {
        for (final Map.Entry<String, TypeDefinition> entry : typeRegistry.types().entrySet()) {
            if (entry.getValue() instanceof ObjectTypeDefinition) {
                final ObjectTypeDefinition objectTypeDefinition = (ObjectTypeDefinition) entry.getValue();
                final List<FieldDefinition> newDefinitions = objectTypeDefinition.getFieldDefinitions();
                final Set<FieldDefinition> toBeRemoved = new HashSet<>();

                for (final FieldDefinition fieldDefinition : newDefinitions) {
                    final String fieldKey = entry.getKey() + "." + fieldDefinition.getName();
                    if (digitrafficConfig.getHiddenFields().contains(fieldKey)) {
                        toBeRemoved.add(fieldDefinition);
                    }
                }

                final List<FieldDefinition> filteredDefinitions = newDefinitions.stream().filter(d -> !toBeRemoved.contains(d)).collect(Collectors.toList());

                final ObjectTypeDefinition newObjectTypeDefiniton = objectTypeDefinition.withNewChildren(newNodeChildrenContainer()
                        .children(CHILD_IMPLEMENTZ, objectTypeDefinition.getImplements())
                        .children(CHILD_DIRECTIVES, objectTypeDefinition.getDirectives())
                        .children(CHILD_FIELD_DEFINITIONS, filteredDefinitions)
                        .build());

                typeRegistry.remove(entry.getValue());
                typeRegistry.add(newObjectTypeDefiniton);
            }
        }
    }

    private RuntimeWiring buildWiring() {
        final TypeRuntimeWiring.Builder query = newTypeWiring("Query");
        final RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime);

        for (final BaseQuery fetcher : rootFetchers) {
            query.dataFetcher(fetcher.getQueryName(), fetcher.createFetcher());
        }
        builder.type(query);

        for (final BaseLink fetcher : this.fetchers) {
            builder.type(newTypeWiring(fetcher.getTypeName())
                    .dataFetcher(fetcher.getFieldName(), fetcher.createFetcher()));
        }

        return builder.build();
    }
}
