import kotlin.io.path.readLines

private fun main() {
    val lib = AOCLib(3)
    lib.printDay()

    val gridExample1 = Day03.parseGrid(lib.getExampleInputPath(1).readLines())
    val grid = Day03.parseGrid(lib.getInputPath().readLines())
    println("-- Part 1 --")

    print("Example = ")
    println(Day03.part1(gridExample1))

    print("Full = ")
    println(Day03.part1(grid))

    println("-- Part 2 --")

    print("Example = ")
    println(Day03.part2(gridExample1))

    print("Full = ")
    println(Day03.part2(grid))
}


object Day03 {
    data class Position(val y: Int, val x: Int) {
        operator fun plus(other: Position) =
            Position(y + other.y, x + other.x)
    }
    data class NumEntry(val pos: Position, val num: Int, val len: Int)

    data class Grid(val height: Int, val width: Int, val numbers: List<NumEntry>,
                    val symbols: HashMap<Position, Char>, val gearRatios: HashMap<Position, HashSet<NumEntry>>) {
        private fun posInGrid(pos: Position) =
            pos.y in 0..<height && pos.x in 0..<width

        fun numHasSurroundingSymbols(numEntry: NumEntry): Boolean {
            val len = numEntry.len

            for (yOffset in -1..1)
                for (xOffset in -1..len) {
                    val newPos = numEntry.pos + Position(yOffset, xOffset)
                    if (!(yOffset == 0 && xOffset in 0..<len)
                        && posInGrid(newPos) && symbols[newPos] != null) {
                        return true
                    }
                }

            return false
        }

        private fun fillGearRatioAtSymPos(numEntry: NumEntry, yOffset: Int, xOffset: Int) {
            val newPos = numEntry.pos + Position(yOffset, xOffset)
            if (
                !(yOffset == 0 && xOffset in 0..<numEntry.len)
                && posInGrid(newPos)
                && symbols[newPos] == '*') {
                gearRatios[newPos]?.add(numEntry)
            }
        }

        fun fillGearRatio(numEntry: NumEntry) {
            for (yOffset in -1..1)
                for (xOffset in -1..numEntry.len)
                    fillGearRatioAtSymPos(numEntry, yOffset, xOffset)
        }
    }

    fun part1(grid: Grid): Int =
        grid.numbers
            .filter { grid.numHasSurroundingSymbols(it) }
            .sumOf{ it.num }

    fun part2(grid: Grid): Int {
        grid.numbers.forEach { grid.fillGearRatio(it) }

        return grid.gearRatios.values
            .filter { it.size == 2 }
            .sumOf { set -> set.fold<NumEntry, Int>(1) { acc, numEntry -> acc * numEntry.num }}
    }


    private val numberGridReg = """\d+""".toRegex()
    private val symbolGridReg = """[^\d.]""".trimMargin().toRegex()

    fun parseGrid(input: List<String>): Grid {
        val height = input.size
        val width = input[0].length

        val numbers = mutableListOf<NumEntry>()
        val symbols = HashMap<Position, Char>()
        val gearRatios = HashMap<Position, HashSet<NumEntry>>()

        input.forEachIndexed { yPos, line ->
            val matchResNum = numberGridReg.findAll(line)
            for (match in matchResNum) {
                val value = match.value.toInt()
                val len = match.value.length
                val xPos = match.range.first

                numbers.add(NumEntry(Position(yPos, xPos), value, len))
            }

            val matchResSym = symbolGridReg.findAll(line)
            for (match in matchResSym) {
                val xPos = match.range.first

                val sym = match.value[0]
                val pos = Position(yPos, xPos)
                symbols[pos] = sym
                if (sym == '*')
                    gearRatios[pos] = HashSet()
            }
        }

        return Grid(height, width, numbers, symbols, gearRatios)
    }
}