package mhalma.advent201807

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner::class)
@WebMvcTest
class ExampleControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `calculateStepOrder example`() {
        mockMvc.perform(get("/api/calculateStepOrder/example").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("\$.order").value("CABDFE"))
    }

    @Test
    fun `calculateStepOrder puzzleInput`() {
        mockMvc.perform(get("/api/calculateStepOrder/puzzleInput").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("\$.order").value("BITRAQVSGUWKXYHMZPOCDLJNFE"))
    }

    @Test
    fun `calculateDuration example`() {
        mockMvc.perform(get("/api/calculateDuration/example/0/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("\$.duration").value(15))
    }

    @Test
    fun `calculateDuration puzzleInput`() {
        mockMvc.perform(get("/api/calculateDuration/puzzleInput/60/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("\$.duration").value(869))
    }
}