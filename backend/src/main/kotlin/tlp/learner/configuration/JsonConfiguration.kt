package tlp.learner.configuration

import com.beust.klaxon.Klaxon
import io.ktor.application.ApplicationCall
import io.ktor.content.TextContent
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.core.IoBuffer
import kotlinx.io.core.readText
import java.io.StringReader

class JsonConfiguration {
    companion object {
        val converter = object : ContentConverter {
            override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
                val request = context.subject
                val channel = request.value as? ByteReadChannel ?: return null

                val empty = IoBuffer.Empty;
                channel.readAvailable(empty)
                return Klaxon().parseJsonObject(StringReader(empty.readText()))
            }

            override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
                return TextContent(Klaxon().toJsonString(value), contentType)
            }
        }
    }

}