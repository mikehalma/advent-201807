package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.Test
import org.mockito.ArgumentMatchers.isNull

class WorkerTest {

    @Test
    fun `assignStep is assigned`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        assertThat(worker.currentStep, `is`(Step('A')))
    }

    @Test
    fun `assignStep calculates seconds left`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        assertThat(worker.secondsLeft, greaterThan(0))
    }

    @Test
    fun `performWork deducts secondsLeft`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        val secondsLeft = worker.secondsLeft
        worker.performWork()
        assertThat(worker.secondsLeft, `is`(secondsLeft - 1))
    }

    @Test
    fun `performWork keeps step in progress`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 10)
        worker.performWork()
        assertThat(worker.currentStep, `is`(Step('A')))
    }

    @Test
    fun `performWork keeps step in progress returns null`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 10)
        val step = worker.performWork()
        assertThat(step == null, `is`(true))
    }

    @Test
    fun `performWork removes step when finished`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        while (worker.secondsLeft > 0) {
            worker.performWork()
        }
        assertThat(worker.currentStep, `is`(Step.EMPTY_STEP))
    }

    @Test
    fun `notWorking is working`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        assertThat(worker.notWorking(), `is`(false))
    }

    @Test
    fun `notWorking is not working`() {
        val worker = Worker(1)
        worker.assignStep(Step('A'), 0)
        while (worker.secondsLeft > 0) {
            worker.performWork()
        }
        assertThat(worker.notWorking(), `is`(true))
    }

}