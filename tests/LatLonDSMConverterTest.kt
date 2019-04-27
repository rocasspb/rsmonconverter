import com.rocasspb.rsmonconverter.output.vbo.LatLonDSMConverter
import org.junit.Test
import kotlin.test.assertEquals

class LatLonDSMConverterTest {
    @Test fun testConvert(){
        assertEquals(LatLonDSMConverter().latLonToDSM(50.44361111), "+3026.37")
    }
}