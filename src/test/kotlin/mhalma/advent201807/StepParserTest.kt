package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.util.ResourceUtils

class StepParserTest {

    @Test
    fun `parse a single step`() {
        val stepDescription = "Step C must be finished before step A can begin."
        assertThat(parseStep(stepDescription), `is`(Pair('A', 'C')))
    }

    @Test
    fun `parse two steps for the same id`() {
        val stepDescriptions = listOf(
                "Step A must be finished before step C can begin.",
                "Step F must be finished before step C can begin."
        )
        val stepA = Step('A')
        val stepF = Step('F')
        val stepC = Step('C', mutableSetOf(stepA, stepF))
        assertThat(parseSteps(stepDescriptions), `is`(setOf(stepA, stepC, stepF)))
    }

    @Test
    fun `load steps from file`() {
        val expected = listOf(
                "Step C must be finished before step A can begin.",
                "Step C must be finished before step F can begin.",
                "Step A must be finished before step B can begin.",
                "Step A must be finished before step D can begin.",
                "Step B must be finished before step E can begin.",
                "Step D must be finished before step E can begin.",
                "Step F must be finished before step E can begin."
        )
        assertThat(getStepsFromFile("example.txt"), `is`(expected))
    }

    private fun getStepsFromFile(fileName: String) = ResourceUtils.getFile("classpath:$fileName").readLines()

    @Test
    fun `parse example from file`() {
        val stepC = Step('C')
        val stepA = Step('A', mutableSetOf(stepC))
        val stepB = Step('B', mutableSetOf(stepA))
        val stepD = Step('D', mutableSetOf(stepA))
        val stepF = Step('F', mutableSetOf(stepC))
        val stepE = Step('E', mutableSetOf(stepB, stepD, stepF))
        val expected = setOf(stepE, stepB, stepD, stepF, stepA, stepC)

        val actual = parseSteps(getStepsFromFile("example.txt"))
        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
        expected.forEach{step ->
            println("checking step ${step.id}")
            assertThat(actual.find {it == step}?.dependencies, containsInAnyOrder(*step.dependencies.toTypedArray()))
        }
    }

    @Test
    fun `calculate order of steps`() {
        assertThat(calculateStepOrder(getStepsFromFile("example.txt")), `is`("CABDFE"))
    }

    @Test
    fun `complete part 1`() {
        assertThat(calculateStepOrder(getStepsFromFile("puzzleInput.txt")), `is`("BITRAQVSGUWKXYHMZPOCDLJNFE"))
    }

    @Test
    fun `calculate duration of steps for example`() {
        assertThat(calculateDuration(parseSteps(getStepsFromFile("example.txt")).toMutableSet(), 0, 2), `is`(15))
    }

    @Test
    fun `get step time`() {
        assertThat(getStepTime(Step('A'), 0), `is`(1))
        assertThat(getStepTime(Step('B'), 1), `is`(3))
        assertThat(getStepTime(Step('Z'), 26), `is`(52))
    }
}