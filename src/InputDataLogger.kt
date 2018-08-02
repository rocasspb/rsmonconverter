interface InputDataLogger {
    fun logData(bytesLine: ByteArray)
}

class NullDataLogger : InputDataLogger {
    override fun logData(bytesLine: ByteArray) {
        //no op
    }

}