package graphqlscope.graphql;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphqlscope.graphql.fetchers.MyDataFetcher;

@Component
public class GraphQLProvider {

    private final GraphQLDataFetchers graphQLDataFetchers;

    private GraphQL graphQL;

    public GraphQLProvider(GraphQLDataFetchers graphQLDataFetchers) {
        this.graphQLDataFetchers = graphQLDataFetchers;
    }

    @Autowired
    private List<MyDataFetcher> fetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("train", graphQLDataFetchers.trainFetcher())
                );

        for (MyDataFetcher fetcher : this.fetchers) {
            builder = builder.type(newTypeWiring(fetcher.getTypeName())
                    .dataFetcher(fetcher.getFieldName(), fetcher.createFetcher()));
        }

        return builder
                .build();
    }


}
