package io.devexpert.kmpmovies.Modules.Shared.Domain.Events

data class GenericEvent(
    val eventId: String,
    val eventSource: String,
    val eventName: String,
    val occurredOn: Long,
    val correlationId: String? = null,
    val causationId: String? = null,
    val aggregateId: Int,
    val body: Map<String, Any?>
)
