package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Test

class WorkTest {

    @Test
    fun `stepsInProgress none`() {
        assertThat(Work(listOf()).stepsInProgress().size, `is`(0))
    }

    @Test
    fun `stepsInProgress single step`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        assertThat(Work(listOf(worker)).stepsInProgress().size, `is`(1))
        assertThat(Work(listOf(worker)).stepsInProgress(), containsInAnyOrder(Step('A')))
    }

    @Test
    fun `stepsInProgress worker finished`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        while (worker.secondsLeft > 0) {
            worker.performWork()
        }
        assertThat(Work(listOf(worker)).stepsInProgress().size, `is`(0))
    }

    @Test
    fun `stepsInProgress one worker not finished`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        while (worker.secondsLeft > 0) {
            worker.performWork()
        }
        val worker2 = Worker(2)
        worker2.assignStep(Step('B'), 0)
        assertThat(Work(listOf(worker, worker2)).stepsInProgress().size, `is`(1))
        assertThat(Work(listOf(worker, worker2)).stepsInProgress(), containsInAnyOrder(Step('B')))
    }

    @Test
    fun `stepsInProgress multiple workers not finished`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        val worker2 = Worker(2)
        worker2.assignStep(Step('B'), 0)
        assertThat(Work(listOf(worker, worker2)).stepsInProgress().size, `is`(2))
        assertThat(Work(listOf(worker, worker2)).stepsInProgress(), containsInAnyOrder(Step('A'), Step('B')))
    }

}