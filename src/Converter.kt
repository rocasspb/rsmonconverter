import java.io.ByteArrayInputStream
import java.io.File
import java.io.PrintWriter

class Converter<T>(val dataReader: InputDataReader<T>, val stream : ByteArrayInputStream, val output: OutputDataWriter<T>) {
    fun readInput(): List<T> = dataReader.readFromBytes(stream)

    fun writeOutput(data: List<T>) {
        output.writeDataToStream(data)
    }


    /*fun enlistDiffs(bytes: ByteArray, seqStart: ByteArray, limit: Int) {
        diffLines(collectLines(bytes, seqStart, limit))
    }

    private fun collectLines(bytes: ByteArray, seqStart: ByteArray, limit: Int) : List<ByteArray> {
        val list = LinkedList<ByteArray>()
        var count = 0

        var index = 0 //findNextSequenceStart(bytes, seqStart, 0)
        if(index == -1)
            return emptyList()

        while(count < limit) {
            val nextIndex = index + 183
            if(nextIndex >= bytes.size)
                break

            list.add(bytes.copyOfRange(index, nextIndex))
            index = nextIndex
            count++
        }
        return list
    }*/



}

fun main(args: Array<String>) {
    val bytes = File("C:/temp/846.run").readBytes()
    val stream = ByteArrayInputStream(bytes)
    val dataReader = RSMonitorReader()
    //dataReader.setDataLogger(DiffedInputDataLogger(false, true))
    val converter = Converter(dataReader, stream, RaceRenderCSVDataWriter(PrintWriter("C:/temp/out.csv")))
    converter.apply {
        writeOutput(readInput())
    }

}