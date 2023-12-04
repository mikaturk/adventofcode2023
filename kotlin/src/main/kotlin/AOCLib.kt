import java.nio.file.Path
import kotlin.io.path.Path

val inputs = Path("/Users/mika/code/aoc2023/inputs")

class AOCLib(private val day: Int) {
    fun getInputPath(): Path =
        inputs.resolve("%02d.txt".format(day))
    fun getExampleInputPath(part: Int = 1): Path =
        inputs.resolve("%02d_example%d.txt".format(day, part))
}