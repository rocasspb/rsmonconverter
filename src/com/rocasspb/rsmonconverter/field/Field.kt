package com.rocasspb.rsmonconverter.field

import javax.sound.sampled.DataLine

sealed class Field<out T>(val value: T) {
    fun accept(visitor: FieldVisitor<Any>) = visitor.visit(this)
}

class DoubleField(value: Double) : Field<Double>(value)
class LatLonField(value: Double) : Field<Double>(value)
class IntField(value: Int) : Field<Int>(value)
class ByteField(value: Byte) : Field<Byte>(value)
class BooleanField(value: Boolean) : Field<Boolean>(value)

interface FieldVisitor<out O> {
    fun <T> visit(field: Field<T>) : O
}

