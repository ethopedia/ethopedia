package org.ethopedia.apps.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionInput
import graphql.GraphQL


class DefaultHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    companion object {
        private val graphQLEngine = GraphQL.newGraphQL(schema).build()
        private val mapper = jacksonObjectMapper()
    }

    override fun handleRequest(
        input: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayProxyResponseEvent {
        return if (input.httpMethod == "POST") {
            val data = mapper.readValue<Map<String, Any>>(input.body)

            val query = data["query"]
            val operationName = data["operationName"] as? String
            val variables = (data["variables"] as? Map<String, Any?>) ?: emptyMap()

            val gqlInput = ExecutionInput.newExecutionInput().apply {
                if (operationName != null) {
                    operationName(operationName)
                }

                query(query.toString())
                variables(variables)
            }.build()

            val result = graphQLEngine.execute(gqlInput)


            return if (result.isDataPresent) {
                APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(
                        mapper.writeValueAsString(
                            buildMap {
                                putAll(result.toSpecification())
                            }
                        ))
                    .withHeaders(
                        mapOf(
                            "Content-Type" to "application/json"
                        )
                    )
            } else {
                APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(
                        mapper.writeValueAsString(
                            buildMap {
                                putAll(result.toSpecification())
                            }
                        )
                    )
                    .withHeaders(
                        mapOf(
                            "Content-Type" to "application/json"
                        )
                    )
            }
        } else {
            APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(
                    DefaultHandler::class.java.classLoader
                        .getResourceAsStream("graphql-graphiql.html")?.bufferedReader()?.use { reader ->
                        reader.readText()
                            .replace("\${graphQLEndpoint}", "graphql")
                    } ?: throw IllegalStateException("Unable to load GraphiQL")
                )
                .withHeaders(
                    mapOf(
                        "Content-Type" to "text/html"
                    )
                )
        }
    }
}
