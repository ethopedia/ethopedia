package org.ethopedia.apps.api

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.toSchema


class Query {
    fun ping(): String = "pong!"
}

val schema = toSchema(
    config = SchemaGeneratorConfig(
        listOf(
            "org.ethopedia.apps.api"
        )
    ),
    queries = listOf(TopLevelObject(Query())),
    mutations = listOf()
)
