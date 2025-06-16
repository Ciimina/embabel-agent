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
package com.embabel.agent.prompt.element

import com.embabel.common.ai.prompt.PromptContributor

/**
 * PromptContributor to control tool usage.
 */
data class ToolCallControl(
    val toolCalls: Int = 5,
) : PromptContributor {

    override fun contribution(): String =
        """
        You are allowed to make up to $toolCalls tool calls to complete the task.
        Use them wisely.
        If you reach this limit, you must stop and return your best answer.
        """.trimIndent()
}

data class FocusedToolCallControl(
    val toolName: String,
    val toolCalls: Int = 5,
) : PromptContributor {

    override fun contribution(): String =
        """
        You are allowed to make up to $toolCalls calls to the $toolName tool to complete the task.
        Use them wisely.
        Do not exceed this limit.
        """.trimIndent()
}
