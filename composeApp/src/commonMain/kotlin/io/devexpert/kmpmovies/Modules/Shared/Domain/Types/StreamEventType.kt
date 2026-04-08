package io.devexpert.kmpmovies.Modules.Shared.Domain.Types

data class StreamEventType(
    val streamName: String,
    val eventData: Map<String, Any?>
)
