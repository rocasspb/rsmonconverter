class ExperimentUtils {
    fun listSequences(bytes: ByteArray, length: Int, distance: Int) {
        for (index in 0..bytes.size - distance - length) {
            val sequence = bytes.copyOfRange(index, index + length + 1)
            findSequence(bytes, sequence, index, distance)
        }

    }

    fun findSequence(bytes: ByteArray, sequence: ByteArray, index: Int, distance : Int) {
        var numberOfMatches = 0
        var currentIndex = index
        while(bytes.size > currentIndex + distance + sequence.size) {
            var i = 0
            for (byte in sequence) {
                if (byte != bytes[currentIndex + i++]) {
                    reportSequence(sequence, index, numberOfMatches)
                    return
                }
            }
            numberOfMatches++
            currentIndex += distance
        }

        reportSequence(sequence, index, numberOfMatches)
    }

    private fun reportSequence(sequence: ByteArray, firstOccurence: Int, nuberOfMatches: Int) {
        if(nuberOfMatches < 3000) //threshold
            return
        val builder = StringBuilder()
        for (byte in sequence) {
            builder.append(printByte(byte))
        }

        println("Found sequence: ${builder.toString()}\n first occurence: $firstOccurence\n Repeats: $nuberOfMatches")
    }

    private fun findNextSequenceStart(bytes: ByteArray, seqStart: ByteArray, index: Int) : Int {
        var currentIndex = index
        while(bytes.size > currentIndex + seqStart.size) {
            if (isMatchingSequence(bytes, seqStart, currentIndex))
                return currentIndex
            currentIndex++
        }
        return -1
    }

    private fun isMatchingSequence(bytes: ByteArray, seqStart: ByteArray, index: Int) : Boolean {
        for ((i, byte) in seqStart.withIndex()) {
            if (byte != bytes[index + i]) {
                return false
            }
        }
        return true
    }

    private fun printByte(byte: Byte) = String.format("%02X ", byte)
}