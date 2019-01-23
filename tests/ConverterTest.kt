import com.rocasspb.rsmonconverter.formatOutputName
import org.junit.Test
import kotlin.test.assertEquals

class ConverterTest {
    @Test
    fun testOutFileName() {
        assertEquals( "abc.csv", formatOutputName("abc.de"))
        assertEquals( "abd.csv", formatOutputName("abd."))
        assertEquals( "2017_08_03_08_57_56_50.06676_04.29639_log.csv",
                formatOutputName("2017_08_03_08_57_56_50.06676_04.29639_log.run"))

    }
}