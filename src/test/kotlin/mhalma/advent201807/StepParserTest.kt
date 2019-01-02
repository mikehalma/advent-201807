package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class StepParserTest {

    @Test
    fun `parse a single step`() {
        val stepDescription = "Step C must be finished before step A can begin."
        assertThat(parseStep(stepDescription), `is`(Step('C', listOf('A'))))
    }
}