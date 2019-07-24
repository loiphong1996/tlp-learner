package tlp.learner.graphql

import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.entity.Item
import tlp.learner.entity.ItemCollectionDTO
import tlp.learner.entity.ItemDTO
import java.util.*

val itemById = fun(environment: DataFetchingEnvironment): ItemDTO? {
    val id = environment.arguments["id"] as? String
    return id?.let {
        transaction {
            Item.findById(UUID.fromString(id))?.let { item ->
                ItemDTO(item.id.toString(), item.name, item.value, null)
            }
        }
    }
}

val itemByName = fun(environment: DataFetchingEnvironment): List<ItemDTO> {
    val name = environment.arguments["name"] as? String
    val result = name?.let {
        transaction {
            Item.all().filter { item -> item.name.contains(name, ignoreCase = true) }
        }.map { item ->
            ItemDTO(item.id.toString(), item.name, item.value, null)
        }
    }
    return result ?: emptyList()
}

val itemByCollection = fun(environment: DataFetchingEnvironment): List<ItemDTO> {
    val collectionDTOParam = environment.getSource<ItemCollectionDTO>()

    val items = collectionDTOParam?.let { collectionDTO ->
        transaction {
            Item.all().filter { item ->
                item.collection.id.toString() == collectionDTO.id
            }
        }.map { item ->
            ItemDTO(item.id.toString(), item.name, item.value, null)
        }
    }
    return items ?: emptyList()
}
