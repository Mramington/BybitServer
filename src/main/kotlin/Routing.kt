package com.example

import com.example.model.SmaDcaStrategy
import com.example.model.SmaDcaStrategyRepository
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repository: SmaDcaStrategyRepository) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/sma-dca-strategy") {
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")
                val all = repository.allSmaDcaStrategyByUserId(userId)
                if (all.isEmpty())
                    call.respond(HttpStatusCode.NotFound, "Strategy not found")
                call.respond(HttpStatusCode.OK, all)
            }

            post("/add") {
                val request = call.receive<SmaDcaStrategy>()
                repository.addSmaDcaStrategy(request)
                call.respond(HttpStatusCode.Created, "Strategy added")
            }

            delete("/{userId}") {
                val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing userId")
                val removed = repository.removeSmaDcaStrategy(userId)
                if (removed) {
                    call.respond(HttpStatusCode.OK, "Strategy removed")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Strategy not found")
                }
            }

            post("/update-last-order") {
                data class UpdateRequest(val userId: String, val lastOrder: String)

                val updateRequest = call.receive<UpdateRequest>()
                val strategy = repository.smaDcaStrategyByUserId(updateRequest.userId)
                if (strategy == null) {
                    call.respond(HttpStatusCode.NotFound, "Strategy not found")
                } else {
                    repository.updateSmaDcaStrategy(strategy, updateRequest.lastOrder)
                    call.respond(HttpStatusCode.OK, "Last order updated")
                }
            }
        }
    }
}
