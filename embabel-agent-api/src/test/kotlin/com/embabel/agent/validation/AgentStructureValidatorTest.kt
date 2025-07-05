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
package com.embabel.agent.validation

import com.embabel.agent.api.dsl.evenMoreEvilWizard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.support.GenericApplicationContext

class AgentStructureValidatorTest {

    @Nested
    inner class Valid {

        @Test
        fun `no agents`() {
            val applicationContext = GenericApplicationContext()
            applicationContext.refresh()
            val validator = AgentStructureValidator(applicationContext)
            validator.afterPropertiesSet()
        }

        @Test
        fun `evil wizard`() {
            val applicationContext = GenericApplicationContext()
            applicationContext.refresh()
            val validator = AgentStructureValidator(applicationContext)
            val result = validator.validate(evenMoreEvilWizard())
            assertEquals(0, result.errors.size, "Expected no validation errors for evenMoreEvilWizard")
        }
    }

}
