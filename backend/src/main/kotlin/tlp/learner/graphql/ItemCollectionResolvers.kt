package tlp.learner.graphql

import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.entity.Item
import tlp.learner.entity.ItemCollection
import tlp.learner.entity.ItemCollectionDTO
import tlp.learner.entity.ItemDTO
import java.util.*


val collectionById = fun(environment: DataFetchingEnvironment): ItemCollectionDTO? {
    val idParam = environment.arguments["id"] as? String

    return idParam?.let { id ->
        transaction {
            ItemCollection.findById(UUID.fromString(id))
        }?.let { itemCollection ->
            ItemCollectionDTO(itemCollection.id.toString(), itemCollection.name, emptyList())
        }
    }
}

val collectionByItem = fun(environment: DataFetchingEnvironment): ItemCollectionDTO {
    val item = environment.getSource<ItemDTO>()
    return transaction {
        Item.findById(UUID.fromString(item.id))!!.collection.let { itemCollection ->
            ItemCollectionDTO(itemCollection.id.toString(), itemCollection.name, emptyList())
        }
    }
}
