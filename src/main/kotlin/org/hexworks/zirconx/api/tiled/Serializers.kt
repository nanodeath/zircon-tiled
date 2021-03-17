package org.hexworks.zirconx.api.tiled

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

private val json: ObjectMapper = jacksonObjectMapper()

internal fun deserializeJson(file: File): Map<String, Any> = json.readValue(file)