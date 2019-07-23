package tlp.learner.controller

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.ktor.application.*
import io.ktor.content.TextContent
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.jackson.jackson
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.request.receiveText
import io.ktor.response.defaultTextContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.readAvailable
import kotlinx.io.core.IoBuffer
import kotlinx.io.core.readText

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tlp.learner.configuration.GraphqlConfiguration
import tlp.learner.configuration.JsonConfiguration
import tlp.learner.entity.Items
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import javax.naming.spi.ObjectFactory
import javax.naming.spi.ObjectFactoryBuilder


fun Application.mainModule() {

    routing {
        route("/api") {
            post("/graphql") {
                val rawQuery = call.receiveText()
                val jsonObj = Klaxon().parseJsonObject(reader = StringReader(rawQuery))
                val query = jsonObj["query"] as? String ?: ""
                val variables = (jsonObj["variables"]as? String?)?.let {
                    Klaxon().parse<Map<String, Any>>(it)
                } ?: emptyMap();
                val operationName = jsonObj["operationName"] as? String ?: ""
                if (!query.isBlank()) {
                    call.respondText(contentType = ContentType.Application.Json) {
                        val response = GraphqlConfiguration.execute(query, operationName, variables)
                        val objectMapper = ObjectMapper()
                        objectMapper.writeValueAsString(mapOf("data" to response))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "")
                }
            }
        }


    }
}







