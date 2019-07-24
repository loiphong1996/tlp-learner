package tlp.learner.entity

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import java.util.*

object Items : UUIDTable() {
    val name = varchar("name", length = 500)
    val value = varchar("value", length = 500)
    val collection = reference("collection", ItemCollections)
}

class Item(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Item>(Items)

    var name by Items.name
    var value by Items.value
    var collection by ItemCollection referencedOn Items.collection
}

data class ItemDTO(
        val id: String,
        val name: String,
        val value: String,
        val collection: ItemCollectionDTO?
)