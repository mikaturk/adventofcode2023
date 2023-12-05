import kotlin.io.path.readText

private fun main() {
    val lib = AOCLib(5)
    lib.printDay()

    val example1 = lib.getExampleInputPath(1).readText()
    val real = lib.getInputPath().readText()

    val parsedExample1 = Day05.parseAlmanac(example1)
    val parsedReal = Day05.parseAlmanac(real)

    println("-- Part 1 --")

    print("Example = ")
    println(Day05.part1(parsedExample1))

    print("Full = ")
    println(Day05.part1(parsedReal))

    println("-- Part 2 --")

    val p2example = Day05.part2(parsedExample1)
    print("Example = ")
    println(p2example)

    print("Full = ")
    println(Day05.part2(parsedReal))
}

object Day05 {
    // Solutions assume no overlap in the conversions from the file
    fun part1(input: Almanac): Long {
        var min = Long.MAX_VALUE

        for (seed in input.seeds) {
            val location = input.conversions.fold(seed) { acc, conversion -> lookup(conversion, acc) }

            if (location < min)
                min = location
        }

        return min
    }

    fun part2(input: Almanac): Long {
        var ranges = input.seeds.chunked(2).map {
            Range(it[0], it[0]+it[1])
        }

        for (conversion in input.conversions) {
            val unmergedRanges = ranges.flatMap { range -> convertRange(range, conversion) }

            ranges = mergeRanges(unmergedRanges)
        }

        return ranges.minOf { it.start }
    }


    data class Almanac(val seeds: List<Long>, val conversions: List<List<Mapping>>) {}
    data class Mapping(val destinationStart: Long, val sourceStart: Long, val length: Long) {
        val sourceEnd get() = sourceStart + length
        val destinationEnd get() = destinationStart + length
    }
    data class Range(val start: Long, val end: Long)

    // Function assumes the maps are correctly ordered
    fun parseAlmanac(fileContents: String): Almanac {
        val parts = fileContents.trim().split("\n\n")

        val seeds = parts[0].substring(7).split(" ").map { it.toLong() }

        val conversions = parts.subList(1, parts.size).map conv@{ part ->
            val lines = part.split("\n")

            return@conv lines.subList(1, lines.size).map line@{ line ->
                val nums = line.split(" ").map { it.toLong() }
                return@line Mapping(nums[0], nums[1], nums[2])
            }
        }

        return Almanac(seeds, conversions)
    }

    private fun lookup(conversion: List<Mapping>, number: Long): Long {
        for (range in conversion) {
            val end = range.sourceStart + range.length
            if (number in range.sourceStart..<end) {
                return range.destinationStart + (number - range.sourceStart)
            }
        }

        return number
    }

    private fun clampMapping(mapping: Mapping, range: Range): Mapping? {
        var dstStart = mapping.destinationStart
        var srcStart = mapping.sourceStart
        var length = mapping.length

        // End before range start OR start after range = no overlap
        if (srcStart + length <= range.start || srcStart >= range.end )
            return null

        // Mapping starts before range
        if (srcStart < range.start) {
            val diff = range.start - srcStart
            dstStart += diff
            srcStart = range.start
            length -= diff
        }

        // Mapping ends after range
        if (srcStart + length > range.end) {
            val diff = srcStart + length - range.end
            length -= diff
        }

        return Mapping(dstStart, srcStart, length)
    }

    fun convertRange(sourceRange: Range, conversions: List<Mapping>): List<Range> {
        val outputRanges = mutableListOf<Range>()
        var range = sourceRange

        val sortedConversions = conversions.sortedBy { it.sourceStart }

        for (unclamped in sortedConversions) {
            // Not null = we have overlap
            val conv = clampMapping(unclamped, range)
                ?: continue

            if (range.start < conv.sourceStart) {
                // Non-overlap before overlap
                outputRanges.add(Range(range.start, conv.sourceStart))
                range = Range(conv.sourceStart, range.end)
            }

            // Add the (potentially clamped) range
            outputRanges.add(Range(conv.destinationStart, conv.destinationEnd))

            if (range.end == conv.sourceEnd) {
                // There is nothing left to process after this
                return outputRanges
            }

            range = Range(conv.sourceEnd, range.end)
        }

        outputRanges.add(range)

        return outputRanges
    }

    private fun mergeRanges(ranges: List<Range>): List<Range> {
        val sorted = ranges.sortedBy { it.start }

        val merged = mutableListOf<Range>()

        var start = sorted[0].start
        var end = sorted[0].start

        for (range in sorted) {
            if (range.start <= end && range.end > end)
                end = range.end
            else {
                merged.add(Range(start, end))
                start = range.start
                end = range.end
            }
        }

        merged.add(Range(start, end))

        return merged
    }
}