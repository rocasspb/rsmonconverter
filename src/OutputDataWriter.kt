import java.io.ByteArrayOutputStream

interface OutputDataWriter<D> {
    fun writeDataToStream(dataList: List<D>)
}

class NullOutputDataWriter<T> : OutputDataWriter<T> {
    override fun writeDataToStream(dataList: List<T>) {
        //no op
    }
}