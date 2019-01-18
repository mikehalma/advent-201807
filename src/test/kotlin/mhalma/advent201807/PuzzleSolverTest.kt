package mhalma.advent201807

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class PuzzleSolverTest {

    @Test
    fun `calculate order of steps`() {
        Assert.assertThat(calculateStepOrder(getStepsFromFile("example.txt")), CoreMatchers.`is`("CABDFE"))
    }

    @Test
    fun `complete part 1`() {
        Assert.assertThat(calculateStepOrder(getStepsFromFile("puzzleInput.txt")), CoreMatchers.`is`("BITRAQVSGUWKXYHMZPOCDLJNFE"))
    }

    @Test
    fun `calculate duration of steps for example`() {
        Assert.assertThat(calculateDuration(parseSteps(getStepsFromFile("example.txt")), 0, 2), CoreMatchers.`is`(15))
    }

    @Test
    fun `calculate duration of steps for part 2`() {
        Assert.assertThat(calculateDuration(parseSteps(getStepsFromFile("puzzleInput.txt")), 60, 5), CoreMatchers.`is`(869))
    }

}