import kotlin.io.path.readText

private fun main() {
    val lib = AOCLib(6)
    lib.printDay()

    val example1 = lib.getExampleInputPath(1).readText()
    val real = lib.getInputPath().readText()

    val parsedExample1 = Day06.parseRacesPart1(example1)
    val parsedReal1 = Day06.parseRacesPart1(real)
    val parsedExample2 = Day06.parseRacePart2(example1)
    val parsedReal2 = Day06.parseRacePart2(real)

    println(parsedExample2)

    println("-- Part 1 --")

    print("Example = ")
    println(Day06.part1(parsedExample1))

    print("Full = ")
    println(Day06.part1(parsedReal1))

    println("-- Part 2 --")

    print("Example = ")
    println(Day06.part2(parsedExample2))

    print("Full = ")
    println(Day06.part2(parsedReal2))
}

object Day06 {
    fun part1(races: List<Race>): Long {
        val records = races.map { part2(it) }

        return records.reduce { a, b -> a * b }
    }

    fun part2(race: Race): Long {
        return (1..<race.time).count { waited ->
            (waited * (race.time - waited)) > race.distance
        }.toLong()
    }

    data class Race(val time: Long, val distance: Long)

    private val oneOrMoreWhiteSpace = """\s+""".toRegex()

    fun parseRacesPart1(fileContents: String): List<Race> {
        val parts = fileContents.split("\n")
        val times = parts[0].substring(11)
            .split(oneOrMoreWhiteSpace)
            .filter { it.isNotEmpty() }
            .map { it.toLong() }
        val distances = parts[1].substring(11)
            .split(oneOrMoreWhiteSpace)
            .filter { it.isNotEmpty() }
            .map { it.toLong() }

        return times.zip(distances).map { (time, distance) -> Race(time, distance)}
    }

    fun parseRacePart2(fileContents: String): Race {
        val parts = fileContents.split("\n")
        val time = parts[0].substring(11)
            .replace(oneOrMoreWhiteSpace, "")
            .toLong()
        val distance = parts[1].substring(11)
            .replace(oneOrMoreWhiteSpace, "")
            .toLong()

        return Race(time, distance)
    }
}