package tlp.learner.configuration

import graphql.GraphQL
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaParser
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.entity.Item
import tlp.learner.graphql.ItemResolvers
import java.util.*


object GraphqlConfiguration {

    fun execute(query: String, operationName: String = "", variables: Map<String, Any?> = emptyMap()): Map<String, Any?> {
        val schema = this.javaClass.getResource("/schema.graphql").readText()

        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = schemaParser.parse(schema)

        val runtimeWiring = newRuntimeWiring()
                .type("QueryType") { typeWiring ->
                    typeWiring.dataFetcher("itemById", ItemResolvers.itemById)
                    typeWiring.dataFetcher("itemByName", ItemResolvers.itemByName)
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
//        val executionResult = build.execute("{hello}")

        return executionResult.getData() ?: emptyMap()
    }
}
