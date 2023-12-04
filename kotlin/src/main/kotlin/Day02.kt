import java.lang.Exception
import kotlin.io.path.readLines
import kotlin.math.max

private fun main() {
    val lib = AOCLib(2)

    println("-- Day 02 --")
    println("-- Part 1 --")

    println("Example = ")
    println(Day02.part1(lib.getExampleInputPath(1).readLines()))

    print("Full = ")
    println(Day02.part1(lib.getInputPath().readLines()))

    println("-- Part 2 --")

    print("Example = ")
    println(Day02.part2(lib.getExampleInputPath(1).readLines()))

    print("Full = ")
    println(Day02.part2(lib.getInputPath().readLines()))
}

object Day02 {
    private val max = Revealed(12, 13, 14)

    fun part1(input: List<String>): Int =
        input.map { parseGame(it) }
            .filter { game -> game.reveals.all { it.allLessEqualThan(max) }}
            .sumOf { game -> game.id }

    fun part2(input: List<String>): Int =
        input.sumOf { line ->
            parseGame(line)
                .reveals
                .reduce { g1, g2 -> g1.maximum(g2)}
                .product()
        }

    data class Revealed(val red: Int, val green: Int, val blue: Int) {
        fun allLessEqualThan(other: Revealed): Boolean =
            red <= other.red && green <= other.green && blue <= other.blue

        fun maximum(other: Revealed): Revealed =
            Revealed(max(red, other.red), max(green, other.green), max(blue, other.blue))

        fun product(): Int = red * green * blue
    }

    data class Game(val id: Int, val reveals: List<Revealed>)

    private val gameReg = """Game (\d+): """.toRegex()
    private val revealReg = """(\d+) (red|green|blue)""".toRegex()

    private fun parseGame(line: String): Game {
        val gameMR = gameReg.find(line)!!
        val id = gameMR.groupValues[1].toIntOrNull()!!

        val revealStrings = line.substring(gameMR.range.last + 1).split("; ")

        val reveals = revealStrings.map { parseRevealed(it) }

        return Game(id, reveals)
    }

    private fun parseRevealed(revealString: String): Revealed {
        val matchResults = revealReg.findAll(revealString)

        var red = 0
        var green = 0
        var blue = 0

        for (mr in matchResults) {
            val amount = mr.groupValues.getOrNull(1)?.toIntOrNull()!!

            when (mr.groupValues.getOrNull(2)!!) {
                "red" -> red = amount
                "green" -> green = amount
                "blue" -> blue = amount
                else -> throw Exception("The regex is broken")
            }
        }

        return Revealed(red, green, blue)
    }
}