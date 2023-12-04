import kotlin.io.path.readLines

private fun main() {
    val lib = AOCLib(4)
    lib.printDay()

    val cardsExample1 = lib.getExampleInputPath(1).readLines()
        .map { Day04.parseCard(it) }
    val cards = lib.getInputPath().readLines()
        .map { Day04.parseCard(it) }
    println("-- Part 1 --")

    print("Example = ")
    println(Day04.part1(cardsExample1))

    print("Full = ")
    println(Day04.part1(cards))

    println("-- Part 2 --")

    print("Example = ")
    println(Day04.part2(cardsExample1))

    print("Full = ")
    println(Day04.part2(cards))
}

object Day04 {

    fun part1(input: List<Card>): Int =
        input.map { it.countWinningNumbers() }
            .filter { it > 0 }
            .sumOf { 1 shl (it - 1) }

    fun part2(input: List<Card>): Int {
        val winAmounts = input.map { it.countWinningNumbers() }
        val cardAmounts = MutableList(winAmounts.size) { 1 }

        for ((idx, winAmount) in winAmounts.withIndex()) {
            for (i in 1..winAmount) {
                cardAmounts[idx + i] += cardAmounts[idx]
            }
        }

        return cardAmounts.sum()
    }


    data class Card(val id: Int, val winning: HashSet<Int>, val have: List<Int>) {
        fun countWinningNumbers(): Int =
            have.count { it in winning }
    }

    private val cardReg = """Card\s+(\d+): """.toRegex()
    private val oneOrMoreWhiteSpace = """\s+""".toRegex()

    private fun parseNumList(numList: String): List<Int> =
        numList.split(oneOrMoreWhiteSpace).filter { it.isNotEmpty() }.map { it.toInt() }

    fun parseCard(line: String): Card {
        val cardMR = cardReg.find(line)!!
        val id = cardMR.groupValues[1].toIntOrNull()!!

        val numberStrings = line.substring(cardMR.range.last + 1).split(" | ")

        val winning = parseNumList(numberStrings[0]).toHashSet()
        val have = parseNumList(numberStrings[1])

        return Card(id, winning, have)
    }
}