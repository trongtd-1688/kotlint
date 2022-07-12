
package com.example.controller

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class HealthCheckControllerTests : BaseControllerTest() {
    @Test
    fun test_health_return_string_value() {
        val mvcRequest = MockMvcRequestBuilders.get("/v1/health")

        mockMvc.perform(mvcRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("API Alive"))
    }
}
