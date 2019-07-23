package tlp.learner.graphql

import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.entity.Item
import java.util.*

object ItemResolvers {
    val itemById = fun (environment: DataFetchingEnvironment): Item? {
        val id = environment.arguments["id"] as? String
        return id?.let {
            transaction {
                Item.findById(UUID.fromString(id))
            }
        }
    }

    val itemByName = fun (environment: DataFetchingEnvironment): List<Item> {
        val name = environment.arguments["name"] as? String
        val result = name?.let {
            transaction {
                Item.all().filter { item -> item.name.contains(name, ignoreCase = true) }
            }
        }
        return result?: emptyList()
    }
}