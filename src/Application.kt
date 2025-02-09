package com.personio

import com.fasterxml.jackson.core.JsonParseException
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.personio.Application.AdjacencyListBuilder
import com.personio.Application.HierarchyTreeBuilder
import com.personio.Application.HierarchyTreeValidator
import com.personio.Domain.*
import com.personio.Infrastructure.SqliteEmployeeRepository
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.utils.io.errors.IOException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
        basic {
            realm = "myrealm"
            validate { if (it.name == "user" && it.password == "password") UserIdPrincipal("user") else null }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val client = HttpClient() {
    }

    fun initDB() {
        Database.connect("jdbc:sqlite:my.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(com.personio.Infrastructure.Employee)
        }
    }

    initDB()
    routing {
        val repository = SqliteEmployeeRepository()

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Application.Json)
        }

        authenticate {
            post("/newHierarchy") {
                repository.empty();
                val post = call.receiveText()

                if (post == "") {
                    call.respond(HttpStatusCode.NoContent)
                }

                try {
                    val mapper = jacksonObjectMapper();

                    val employeeHierarchy: List<Pair<String, String>>? = mapper
                        .readTree(post)
                        .deepCopy<ObjectNode>()
                        .fields()
                        .asSequence()
                        .map { Pair(it.key, it.value.asText()!!) }
                        .toList()


                    if (null == employeeHierarchy) {
                        call.respondText("Json error", contentType = ContentType.Application.Json)
                    }

                    val mapToAdjacencyList = AdjacencyListBuilder();

                    val adjacencyList =
                        mapToAdjacencyList.toAdjacencyList(employeeHierarchy!!).map { it.first to it.second }

                    try {
                        val validator = HierarchyTreeValidator()
                        validator.validate(adjacencyList)
                    } catch (e: TwoCeoException) {
                        call.respondText(
                            "There is two CEO in the hierarchy",
                            contentType = ContentType.Application.Json
                        )
                    } catch (e: ManagedTwiceException) {
                        call.respondText("An employee is managed twice", contentType = ContentType.Application.Json)
                    }

                    val employees =
                        employeeHierarchy.flatMap { listOf(it.first, it.second) }.toSet().map { it to Employee(it) }

                    val adjacencyListAsMap = adjacencyList.toMap()
                    val employeesAsMap = employees.toMap()

                    employees.forEach { employee ->
                        adjacencyListAsMap[employee.first]!!.forEach { managed ->
                            employee.second.supervise(
                                employeesAsMap[managed]!!
                            )
                        }
                    }

                    employees.forEach {
                        repository.save(it.second)
                    }

                    val getTreeHierarchy = HierarchyTreeBuilder(repository)
                    val json = getTreeHierarchy.invoke().toJson()
                    call.respond(json)
                } catch (e: CycleException) {
                    call.respondText(
                        "a cycle have been detected in the hierarchy",
                        contentType = ContentType.Application.Json
                    )
                } catch (jpe: JsonParseException) {
                    call.respondText("Json is invalid", contentType = ContentType.Application.Json)
                } catch (e: IOException) {
                    call.respondText("Json is invalid", contentType = ContentType.Application.Json)
                }
            }

            get("/employeeHierarchy") {
                if (0 == repository.all().count()) {
                    call.respondText("No tree in database", contentType = ContentType.Application.Json)
                } else {
                    val getTreeHierarchy = HierarchyTreeBuilder(repository)
                    val employeeNode = getTreeHierarchy.invoke()

                    call.respondText(employeeNode.toJson(), contentType = ContentType.Application.Json)
                }
            }

            get("/employeeSupervisors/{name}") {
                val name = call.parameters["name"]

                if (0 == repository.all().count()) {
                    call.respondText("No tree in database", contentType = ContentType.Application.Json)
                }

                val getTreeHierarchy = HierarchyTreeBuilder(repository)
                try {
                    val actual = getTreeHierarchy.getSupervisors(name!!)
                    call.respondText(actual.toJson(), contentType = ContentType.Application.Json)
                } catch (e: NoEmployeeFoundException) {
                    call.respondText(e.message.toString(), contentType = ContentType.Application.Json)
                }
            }
        }
    }
}
