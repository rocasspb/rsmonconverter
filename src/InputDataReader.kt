import java.io.ByteArrayInputStream

interface InputDataReader<T> {
    fun readFromBytes(bytes: ByteArrayInputStream) : List<T>
    fun setDataLogger(logger: InputDataLogger)
}