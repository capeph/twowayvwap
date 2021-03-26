package analytics.vwap;

import org.junit.jupiter.api.Test;

import static analytics.vwap.Instrument.INSTRUMENT0;
import static analytics.vwap.State.INDICATIVE;
import static org.junit.jupiter.api.Assertions.*;

class TwoWayPriceImplTest {

    @Test
    public void testTwoWayPrice() {
        TwoWayPrice p = new TwoWayPriceImpl(INSTRUMENT0, INDICATIVE, 2, 3, 4, 5);
        assertEquals(INSTRUMENT0, p.getInstrument());
        assertEquals(INDICATIVE, p.getState());
        assertEquals(2.0, p.getBidPrice());
        assertEquals(3.0, p.getBidAmount());
        assertEquals(4.0, p.getOfferPrice());
        assertEquals(5.0, p.getOfferAmount());
    }

}