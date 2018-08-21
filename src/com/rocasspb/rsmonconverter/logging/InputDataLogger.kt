package com.rocasspb.rsmonconverter.logging

interface InputDataLogger<T> {
    var shouldLogData:Boolean
    var shouldLogResult:Boolean

    fun logData(bytesLine: ByteArray)
    fun logResult(result: T)
}

class NullDataLogger<T> : InputDataLogger<T> {
    override var shouldLogData: Boolean
        get() = false
        set(value) {}
    override var shouldLogResult: Boolean
        get() = false
        set(value) {}

    override fun logResult(result: T) {
        //no op
    }

    override fun logData(bytesLine: ByteArray) {
        //no op
    }

}