package fi.digitraffic.graphql.rail.config.graphql;

import static graphql.language.NodeChildrenContainer.newNodeChildrenContainer;
import static graphql.language.ObjectTypeDefinition.CHILD_DIRECTIVES;
import static graphql.language.ObjectTypeDefinition.CHILD_FIELD_DEFINITIONS;
import static graphql.language.ObjectTypeDefinition.CHILD_IMPLEMENTZ;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import fi.digitraffic.graphql.rail.links.base.BaseLink;
import fi.digitraffic.graphql.rail.queries.BaseQuery;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeDefinition;
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

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .instrumentation(new ChainedInstrumentation(Arrays.asList(
//                new ExecutionTimeInstrumentation()
                        new NoCircularQueriesInstrumentation()
                ))).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);

        removeBlacklistedFields(typeRegistry);

        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private void removeBlacklistedFields(TypeDefinitionRegistry typeRegistry) {
        for (Map.Entry<String, TypeDefinition> entry : typeRegistry.types().entrySet()) {
            if (entry.getValue() instanceof ObjectTypeDefinition) {
                ObjectTypeDefinition objectTypeDefinition = (ObjectTypeDefinition) entry.getValue();
                List<FieldDefinition> newDefinitions = objectTypeDefinition.getFieldDefinitions();
                Set<FieldDefinition> toBeRemoved = new HashSet<>();
                for (FieldDefinition fieldDefinition : newDefinitions) {
                    String fieldKey = entry.getKey() + "." + fieldDefinition.getName();
                    if (digitrafficConfig.getHiddenFields().contains(fieldKey)) {
                        toBeRemoved.add(fieldDefinition);
                    }
                }
                if (!toBeRemoved.isEmpty()) {
                    newDefinitions.removeAll(toBeRemoved);
                }

                ObjectTypeDefinition newObjectTypeDefiniton = objectTypeDefinition.withNewChildren(newNodeChildrenContainer()
                        .children(CHILD_IMPLEMENTZ, objectTypeDefinition.getImplements())
                        .children(CHILD_DIRECTIVES, objectTypeDefinition.getDirectives())
                        .children(CHILD_FIELD_DEFINITIONS, newDefinitions)
                        .build());

                typeRegistry.remove(entry.getValue());
                typeRegistry.add(newObjectTypeDefiniton);
            }
        }
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime);

        TypeRuntimeWiring.Builder query = newTypeWiring("Query");
        for (BaseQuery fetcher : rootFetchers) {
            query.dataFetcher(fetcher.getQueryName(), fetcher.createFetcher());
        }
        builder = builder.type(query);

        for (BaseLink fetcher : this.fetchers) {
            builder = builder.type(newTypeWiring(fetcher.getTypeName())
                    .dataFetcher(fetcher.getFieldName(), fetcher.createFetcher()));
        }

        return builder
                .build();
    }


}
