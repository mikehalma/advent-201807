package mhalma.advent201807

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Test

class StepsTest {

    @Test
    fun `empty`() {
        assertThat(Steps().isEmpty(), `is`(true))
        assertThat(Steps().isNotEmpty(), `is`(false))
    }

    @Test
    fun `not empty`() {
        assertThat(Steps(mutableSetOf(Step('A'))).isEmpty(), `is`(false))
        assertThat(Steps(mutableSetOf(Step('A'))).isNotEmpty(), `is`(true))
    }

    @Test
    fun `getAvailableSteps empty`() {
        assertThat(Steps().getAvailableSteps().size, `is`(0))
    }

    @Test
    fun `getAvailableSteps one dependency`() {
        assertThat(Steps(mutableSetOf(Step('A', mutableSetOf(Step('B'))))).getAvailableSteps().size, `is`(0))
    }

    @Test
    fun `getAvailableSteps no dependencies`() {
        assertThat(Steps(mutableSetOf(Step('A'))).getAvailableSteps().size, `is`(1))
        assertThat(Steps(mutableSetOf(Step('A'))).getAvailableSteps(), containsInAnyOrder(*mutableSetOf(Step('A')).toTypedArray()))
    }

    @Test
    fun `getNextAvailableStep only step`() {
        assertThat(Steps(mutableSetOf(Step('A'))).getNextAvailableStep(), `is`(Step('A')))
    }

    @Test
    fun `getNextAvailableStep two steps`() {
        assertThat(Steps(mutableSetOf(Step('A'), Step('B'))).getNextAvailableStep(), `is`(Step('A')))
        assertThat(Steps(mutableSetOf(Step('A'), Step('B'))).getNextAvailableStep(), `is`(Step('A')))
    }

    @Test
    fun `remove single step`() {
        val steps = Steps(mutableSetOf(Step('A')))
        steps.remove(Step('A'))
        assertThat(steps.size, `is`(0))
    }

    @Test
    fun `remove not present`() {
        val steps = Steps(mutableSetOf(Step('A')))
        steps.remove(Step('B'))
        assertThat(steps.size, `is`(1))
        assertThat(steps, contains(Step('A')))
    }

    @Test
    fun `remove dependency`() {
        val steps = Steps(mutableSetOf(Step('A', mutableSetOf(Step('B')))))
        steps.remove(Step('B'))
        assertThat(steps.size, `is`(1))
        assertThat(steps, contains(Step('A')))
    }

    @Test
    fun `remove step and dependency`() {
        val steps = Steps(mutableSetOf(Step('A', mutableSetOf(Step('B'))), Step('B')))
        steps.remove(Step('B'))
        assertThat(steps.size, `is`(1))
        assertThat(steps, contains(Step('A')))
    }

    @Test
    fun `removeAll removes all`() {
        val steps = Steps(mutableSetOf(Step('A', mutableSetOf(Step('B'))), Step('B')))
        steps.removeAll(listOf(Step('B'), Step('A')))
        assertThat(steps.isEmpty(), `is`(true))
    }

    @Test
    fun `removeAll leaves remaining`() {
        val steps = Steps(mutableSetOf(Step('A', mutableSetOf(Step('B'))), Step('B'), Step('C')))
        steps.removeAll(listOf(Step('B'), Step('A')))
        assertThat(steps.size, `is`(1))
        assertThat(steps, contains(Step('C')))
    }

    @Test
    fun `removeAll nothing to remove`() {
        val steps = Steps(mutableSetOf(Step('A', mutableSetOf(Step('B'))), Step('B'), Step('C')))
        steps.removeAll(listOf(Step('D'), Step('E')))
        assertThat(steps.size, `is`(3))
    }

}