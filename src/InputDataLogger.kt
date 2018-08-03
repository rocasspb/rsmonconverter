interface InputDataLogger<T> {
    fun logData(bytesLine: ByteArray)
    fun logResult(result: T)
}

class NullDataLogger<T> : InputDataLogger<T> {
    override fun logResult(result: T) {
        //no op
    }

    override fun logData(bytesLine: ByteArray) {
        //no op
    }

}