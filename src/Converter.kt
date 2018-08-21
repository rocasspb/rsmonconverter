import input.InputDataReader
import input.RSMonitorReader
import output.OutputDataWriter
import output.RaceRenderCSVDataWriter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.PrintWriter

class Converter<T>(val dataReader: InputDataReader<T>, val stream : ByteArrayInputStream, val output: OutputDataWriter<T>) {
    fun readInput(): List<T> = dataReader.readFromBytes(stream)

    fun writeOutput(data: List<T>) {
        output.writeDataToStream(data)
    }
}

fun main(args: Array<String>) {
    if(args.size < 2)
        throw IllegalArgumentException("Please provide input and output filenames")
    val bytes = File(args[0]).readBytes()
    val stream = ByteArrayInputStream(bytes)
    val dataReader = RSMonitorReader()
    //dataReader.setDataLogger(logging.DiffedInputDataLogger(true, true))
    val converter = Converter(dataReader, stream, RaceRenderCSVDataWriter(PrintWriter(args[1])))
    converter.apply {
        writeOutput(readInput())
    }

}