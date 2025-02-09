package com.personio

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.client.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Test
    fun testNoEmployeeHierarchy() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{}")
            }
            handleRequest(HttpMethod.Get, "/employeeHierarchy") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("No tree in database", response.content)
            }
        }
    }

    @Test
    fun testEmployeeHierarchy() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }
            handleRequest(HttpMethod.Get, "/employeeHierarchy") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\n" +
                        "  \"Jonas\" : {\n" +
                        "    \"Sophie\" : {\n" +
                        "      \"Nick\" : {\n" +
                        "        \"Pete\" : { },\n" +
                        "        \"Barbara\" : { }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", response.content)
            }
        }
    }

    @Test
    fun testEmployeeHierarchyPost() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun testEmployeeSupervisors() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }
            handleRequest(HttpMethod.Get, "/employeeSupervisors/Pete") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\n" +
                        "  \"Sophie\" : {\n" +
                        "    \"Nick\" : {\n" +
                        "      \"Pete\" : { }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}", response.content)
            }
        }
    }

    @Test
    fun testEmployeeSupervisorsCaseOnlyOneSupervisor() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }
            handleRequest(HttpMethod.Get, "/employeeSupervisors/Sophie") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\n" +
                        "  \"Jonas\" : {\n" +
                        "    \"Sophie\" : { }\n" +
                        "  }\n" +
                        "}", response.content)
            }
        }
    }

    @Test
    fun testEmployeeSupervisorsCaseNoSupervisor() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }
            handleRequest(HttpMethod.Get, "/employeeSupervisors/Jonas") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\n" +
                        "  \"Jonas\" : { }\n" +
                        "}", response.content)
            }
        }
    }

    @Test
    fun testHierarchyLoop() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Nick\": \"Jonas\",\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Jonas\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("a cycle have been detected in the hierarchy", response.content)
            }
        }
    }

    @Test
    fun testInvalidJson() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\" \"Jonas\"\n" +
                        "}"
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Json is invalid", response.content)
            }
        }
    }

    @Test
    fun testNotFoundEmployee() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/newHierarchy") {
                addHeader("content-type", "application/x-www-form-urlencoded")
                addHeader("Accept", "application/json")
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                setBody("{\n" +
                        "  \"Pete\": \"Nick\",\n" +
                        "  \"Barbara\": \"Nick\",\n" +
                        "  \"Nick\": \"Sophie\",\n" +
                        "  \"Sophie\": \"Jonas\"\n" +
                        "}"
                )
            }
            handleRequest(HttpMethod.Get, "/employeeSupervisors/Michel") {
                addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("No employee named: \"Michel\" found !", response.content)
            }
        }
    }
}
