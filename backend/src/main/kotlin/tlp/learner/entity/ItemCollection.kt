package tlp.learner.entity

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import java.util.*

object ItemCollections : UUIDTable() {
    val name = varchar("name", length = 500)
}

class ItemCollection(id: EntityID<UUID>): UUIDEntity(id){
    companion object : UUIDEntityClass<ItemCollection>(ItemCollections)

    var name by ItemCollections.name
}

data class ItemCollectionDTO (
        val id: String,
        val name: String,
        val items: List<ItemDTO>
)