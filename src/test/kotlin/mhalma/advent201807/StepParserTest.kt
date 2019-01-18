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

}