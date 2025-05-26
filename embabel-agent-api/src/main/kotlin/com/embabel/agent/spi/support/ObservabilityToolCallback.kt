/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.agent.spi.support

import com.embabel.common.util.loggerFor
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.definition.ToolDefinition

class ObservabilityToolCallback(
    private val delegate: ToolCallback,
    private val observationRegistry: ObservationRegistry? = null,
) : ToolCallback {

    override fun getToolDefinition(): ToolDefinition = delegate.toolDefinition

    override fun call(toolInput: String): String {
        if (observationRegistry == null) {
            return delegate.call(toolInput)
        }
        val currentObservation = observationRegistry.currentObservation
        loggerFor<ObservabilityToolCallback>().info(
            "Observability decorator for tool call {} with input: {}, current observation: {}",
            delegate.toolDefinition.name(),
            toolInput,
            currentObservation?.context?.name ?: "None"
        )
        val observation = Observation.createNotStarted("tool call", observationRegistry)
            .lowCardinalityKeyValue("toolName", delegate.toolDefinition.name())
            .highCardinalityKeyValue("payload", toolInput)
            .parentObservation(currentObservation)
            .start()
        return try {
            val result = delegate.call(toolInput)
            observation.lowCardinalityKeyValue("status", "success")
            observation.highCardinalityKeyValue("result", result)
            result
        } catch (ex: Exception) {
            observation.lowCardinalityKeyValue("status", "error")
            observation.highCardinalityKeyValue("error_type", ex::class.simpleName ?: "Unknown")
            observation.highCardinalityKeyValue("error_message", ex.message ?: "No message")
            observation.error(ex) // This also records the exception
            throw ex
        } finally {
            observation.stop()
        }
    }


    override fun toString(): String {
        return "ObservabilityToolCallback(delegate=${delegate.toolDefinition.name()})"
    }
}
