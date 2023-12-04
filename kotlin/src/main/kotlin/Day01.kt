import kotlin.io.path.readLines

val lib = AOCLib(1)

fun main() {
    println("-- Part 1 --")

    print("Example = ")
    println(part1(lib.getExampleInputPath(1).readLines()))

    print("Full = ")
    println(part1(lib.getInputPath().readLines()))

    println("-- Part 2 --")

    print("Example = ")
    println(part2(lib.getExampleInputPath(2).readLines()))

    print("Full = ")
    println(part2(lib.getInputPath().readLines()))
}

fun part1(input: List<String>): Int =
    input.sumOf { retrieveDigits(it) }

fun retrieveDigits(line: String): Int {
    // We are guaranteed to have numbers on each line
    val first = line.find(Char::isDigit)!!.digitToInt()
    val second = line.reversed().find(Char::isDigit)!!.digitToInt()

    return first * 10 + second
}

val numbersRegex = """(one|two|three|four|five|six|seven|eight|nine|\d)""".toRegex()

fun part2(input: List<String>): Int =
    input.sumOf { retrieveNumbers(it) }

fun findNumbers(line: String): List<String> {
    var index = 0
    val numbers = mutableListOf<String>()

    while (index < line.length) {
        val matchResult = numbersRegex.find(line, index) ?: break
        val matchedText = matchResult.value

        numbers.add(matchedText)

        // If we matched a written number, start the next iteration from the last char of the current one,
        // to account for overlap, else just start from the next char in the string
        index = if (matchedText.length > 1) {
            matchResult.range.last
        } else {
            matchResult.range.last + 1
        }
    }

    return numbers
}

fun retrieveNumbers(line: String): Int {
    val numbers = findNumbers(line)

    val first = mapTextToNum(numbers.first())
    val second = mapTextToNum(numbers.last())
    val res = first!! * 10 + second!!

//    println("($first, $second) \"$line\" = $res [${numbers.joinToString(", ")}]")

    // We are guaranteed to have numbers on each line
    return res
}

fun mapTextToNum(numText: String): Int? =
    when (numText) {
        "one" -> 1
        "two" -> 2
        "three" -> 3
        "four" -> 4
        "five" -> 5
        "six" -> 6
        "seven" -> 7
        "eight" -> 8
        "nine" -> 9
        else -> numText.toIntOrNull()
    }