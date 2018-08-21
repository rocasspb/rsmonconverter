package com.rocasspb.rsmonconverter.input

import com.rocasspb.rsmonconverter.logging.InputDataLogger
import java.io.ByteArrayInputStream

interface InputDataReader<T> {
    fun readFromBytes(bytes: ByteArrayInputStream) : List<T>
    fun setDataLogger(logger: InputDataLogger<T>)
}