package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test

class StepTest {

    @Test
    fun `add dependency`() {
        val step = Step('A')
        val dependency = Step('B')
        step.addDependency(dependency)

        assertThat(step.dependencies.size , `is`(1))
        assertThat(step.dependencies.contains(dependency) , `is`(true))
    }

    @Test
    fun `add dependency that already exists`() {
        val dependency = Step('B')
        val step = Step('A', mutableSetOf(dependency))
        step.addDependency(dependency)

        assertThat(step.dependencies.size , `is`(1))
        assertThat(step.dependencies.contains(dependency) , `is`(true))
    }

    @Test
    fun `equals`() {
        assertThat(Step('A') == (Step('A')), `is`(true))
    }

    @Test
    fun `equals different dependencies`() {
        assertThat(Step('A', mutableSetOf(Step('B'))) == (Step('A')), `is`(true))
    }

    @Test
    fun `not equals`() {
        assertThat(Step('A'), `is`(not(Step('B'))))
    }

    @Test
    fun `not equal null`() {
        assertThat(Step('A') == null, `is`(false))
    }

    @Test
    fun `not equal random object`() {
        assertThat(Step('A').equals(Worker(1)), `is`(false))
    }

    @Test
    fun `equals and hashcode in collection`() {
        val step1 = Step('1')
        val step2 = Step('2')
        val step1a = Step('1')
        step1.addDependency(step2)
        assertThat(setOf(step1).contains(step1a), `is`(true))
    }

    @Test
    fun `equals and hashcode not in collection`() {
        val step1 = Step('1')
        val step2 = Step('2')
        assertThat(setOf(step1).contains(step2), `is`(false))
    }

    @Test
    fun `toString no dependencies`() {
        assertThat(Step('A').toString(), `is`("Step A, dependencies"))
    }

    @Test
    fun `toString with dependencies`() {
        assertThat(Step('A', mutableSetOf(Step('B'), Step('C'))).toString(), `is`("Step A, dependencies BC"))
    }

    @Test
    fun `get step time`() {
        Assert.assertThat(Step('A').getStepTime(0), `is`(1))
        Assert.assertThat(Step('B').getStepTime(1), `is`(3))
        Assert.assertThat(Step('Z').getStepTime(26), `is`(52))
    }
}