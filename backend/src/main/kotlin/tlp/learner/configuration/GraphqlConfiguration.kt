package tlp.learner.configuration

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import tlp.learner.graphql.collectionByItem
import tlp.learner.graphql.itemByCollection
import tlp.learner.graphql.itemById
import tlp.learner.graphql.itemByName


object GraphqlConfiguration {

    fun execute(query: String, operationName: String = "", variables: Map<String, Any?> = emptyMap()): Map<String, Any?> {
        val schema = this.javaClass.getResource("/schema.graphql").readText()

        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = schemaParser.parse(schema)

        val runtimeWiring = newRuntimeWiring()
                .type("QueryType") { typeWiring ->
                    typeWiring.dataFetcher("itemById", itemById)
                    typeWiring.dataFetcher("itemByName", itemByName)
                }
                .type("Collection"){typeWiring ->
                    typeWiring.dataFetcher("items", itemByCollection)
                }
                .type("Item"){ typeWiring ->
                    typeWiring.dataFetcher("collection", collectionByItem)
                }
                .build()

        val schemaGenerator = SchemaGenerator()
        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

        val build = GraphQL.newGraphQL(graphQLSchema).build()
        val executionResult = build.execute {
            it.query(query)
            it.operationName(operationName)
            it.variables(variables)
        }
        return executionResult.getData() ?: emptyMap()
    }
}
