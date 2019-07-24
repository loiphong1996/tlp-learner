package tlp.learner.controller

import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import tlp.learner.configuration.GraphqlConfiguration
import java.io.StringReader


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







