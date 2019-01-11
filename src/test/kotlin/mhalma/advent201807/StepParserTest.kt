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
        assertThat(parseSteps(stepDescriptions), containsInAnyOrder(*Steps(mutableSetOf(stepA, stepC, stepF)).toTypedArray()))
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
        assertThat(calculateDuration(parseSteps(getStepsFromFile("example.txt")), 0, 2), `is`(15))
    }

    @Test
    fun `calculate duration of steps for part 2`() {
        assertThat(calculateDuration(parseSteps(getStepsFromFile("puzzleInput.txt")), 60, 5), `is`(869))
    }

    /**
     * TODO
     *  -* Put the steps we are working on into their own class and refer to that class
     *  - split up StepParser so classes with content have their own file, and write missing unit tests
     *  -- finish tests for Steps, Worker and Work
     *  -- split parts1 and 2 from StepParser
     *  - create a rest endpoint for each part of the puzzle
     *  - think about thread safety and try to break it - or is immutability working ok
     *
     */
}