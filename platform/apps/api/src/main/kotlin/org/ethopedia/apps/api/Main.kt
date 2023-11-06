package org.ethopedia.apps.api

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(Routing)

    val dummyContext = object : Context {
        override fun getAwsRequestId(): String = ""
        override fun getLogGroupName(): String = ""
        override fun getLogStreamName(): String = ""
        override fun getFunctionName(): String = ""
        override fun getFunctionVersion(): String = ""
        override fun getInvokedFunctionArn(): String = ""
        override fun getIdentity(): CognitoIdentity = error("Running locally!")
        override fun getClientContext(): ClientContext = error("Running locally!")
        override fun getRemainingTimeInMillis(): Int = 0
        override fun getMemoryLimitInMB(): Int = 0
        override fun getLogger(): LambdaLogger = error("Running locally!")
    }

    routing {
        get("*") {
            val request = APIGatewayProxyRequestEvent()
                .withBody(call.receiveText())
                .withHeaders(call.request.headers.toMap().mapValues { it.value.firstOrNull() })
                .withHttpMethod("GET")


            val resp = DefaultHandler().handleRequest(request, dummyContext)

            resp.headers.forEach { call.response.header(it.key, it.value) }

            call.respond(HttpStatusCode.fromValue(resp.statusCode), resp.body)
        }

        post("*") {
            val request = APIGatewayProxyRequestEvent()
                .withBody(call.receiveText())
                .withHeaders(call.request.headers.toMap().mapValues { it.value.firstOrNull() })
                .withHttpMethod("POST")


            val resp = DefaultHandler().handleRequest(request, dummyContext)

            resp.headers.forEach { call.response.header(it.key, it.value) }

            call.respond(HttpStatusCode.fromValue(resp.statusCode), resp.body)
        }
    }
}
