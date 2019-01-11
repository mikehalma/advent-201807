package mhalma.advent201807

data class Step(val id: Char, val dependencies: MutableSet<Step> = mutableSetOf()) {

    private val STEP_VALUES = 'A'.rangeTo('Z')

    companion object {
        val EMPTY_STEP: Step = Step('0')
    }

    fun addDependency(dependency: Step) {
        dependencies.add(dependency)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Step) {
            return false
        }
        return id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun toString(): String {
        return "Step ${this.id}, dependencies ${this.dependencies.map {it.id}.joinToString("")}".trim()
    }

    fun getStepTime(minDuration: Int): Int {
        return STEP_VALUES.indexOf(this.id) + minDuration + 1
    }
}