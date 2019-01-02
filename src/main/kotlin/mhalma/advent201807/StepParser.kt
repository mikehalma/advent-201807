package mhalma.advent201807

data class Step(val id: Char, val dependencies: List<Char>)

fun parseStep(description: String): Step {
    val regex = """.*([A-Z]).*([A-Z])""".toRegex()
    val result = regex.find(description)
    val (id, dependency) = result!!.destructured
    return Step(id.toCharArray()[0], listOf(dependency.toCharArray()[0]))
}