package com.personio.Infrastructure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class EmployeeNode(val value: String, val children: List<EmployeeNode> = emptyList()){
    private fun toJson(mapper: ObjectMapper): ObjectNode {
        val node = mapper.createObjectNode()
        children.map { node.set<JsonNode>(it.value, it.toJson(mapper)) }

        return node
    }

    fun toJson(): String {
        val mapper = jacksonObjectMapper()
        val root = mapper.createObjectNode()
        root.set<JsonNode>(value, toJson(mapper))

        return root.toPrettyString()
    }
}

