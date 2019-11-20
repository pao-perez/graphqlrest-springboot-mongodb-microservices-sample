package com.paoperez.graphql;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.springframework.stereotype.Component;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
class GraphQLProvider {
    private final GraphQLDataFetchers graphQLDataFetchers;
    private GraphQL graphQL;

    GraphQLProvider(final GraphQLDataFetchers graphQLDataFetchers) {
        this.graphQLDataFetchers = graphQLDataFetchers;
    }

    GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        this.graphQL = GraphQL.newGraphQL(buildSchema(sdl)).build();
    }

    private GraphQLSchema buildSchema(final String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        return new SchemaGenerator().makeExecutableSchema(typeRegistry, buildWiring());
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query").dataFetcher("contents", graphQLDataFetchers.getAllContentsDataFetcher()))
                .type(newTypeWiring("Query").dataFetcher("content", graphQLDataFetchers.getContentDataFetcher()))
                .type(newTypeWiring("Content").dataFetcher("image", graphQLDataFetchers.getContentImageDataFetcher())
                        .dataFetcher("category", graphQLDataFetchers.getCategoryDataFetcher())
                        .dataFetcher("avatar", graphQLDataFetchers.getAvatarDataFetcher()))
                .type(newTypeWiring("Avatar").dataFetcher("image", graphQLDataFetchers.getAvatarImageDataFetcher()))
                .build();
    }

}
