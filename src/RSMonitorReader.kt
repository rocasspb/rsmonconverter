import java.io.ByteArrayInputStream
import java.util.*

class RSMonitorReader : InputDataReader<DataLine> {
    private val lineLength = 139



    override fun readFromBytes(bytes: ByteArrayInputStream): List<DataLine> {
        val lineBytes = ByteArray(lineLength)
        val resList = LinkedList<DataLine>()
        while (bytes.available() > lineLength) {
            bytes.read(lineBytes)

            val line = DataLine()

            //todo - move logic to the reader
            line.readFromBytes(lineBytes)
            resList.add(line)
        }

        return resList
    }
}