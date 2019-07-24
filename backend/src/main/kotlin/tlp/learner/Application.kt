package tlp.learner

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.controller.mainModule
import tlp.learner.entity.Item
import tlp.learner.entity.ItemCollection
import tlp.learner.entity.ItemCollections
import tlp.learner.entity.Items


fun main(args: Array<String>) {
    embeddedServer(
            Netty,
            watchPaths = listOf("learner"),
            port = 8080,
            module = Application::mainModule
    ).apply {

        databaseSetup()
        start(wait = true)
    }
}

fun databaseSetup() {
    Database.connect("jdbc:mysql://localhost:3306/tl", user = "root", password = "1234", driver = "com.mysql.cj.jdbc.Driver")

    transaction {
        if (Items.exists()) {
            Items.deleteAll()
        } else {
            SchemaUtils.create(Items)
        }
        if (ItemCollections.exists()) {
            ItemCollections.deleteAll()
        } else {
            SchemaUtils.create(ItemCollections)
        }

        (0..20).toList()
                .forEach { collectionIndex ->
                    val collection = ItemCollection.new {
                        name = "Collection name $collectionIndex"
                    }
                    (0..99).toList()
                            .map { itemSubIndex -> "$collectionIndex$itemSubIndex".toInt() }
                            .forEach { itemIndex ->
                                Item.new {
                                    this.name = "Item name $itemIndex"
                                    this.value = "Value $itemIndex"
                                    this.collection = collection
                                }
                            }
                }
    }
}