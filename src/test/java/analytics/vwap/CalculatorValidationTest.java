package analytics.vwap;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static analytics.vwap.Instrument.INSTRUMENT0;
import static analytics.vwap.Market.*;
import static analytics.vwap.State.FIRM;
import static analytics.vwap.State.INDICATIVE;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorValidationTest {


    private void checkErr(String expected, Market mkt, Instrument inst, State state, double bidPx, double bidAmt, double offPx, double offAmt) {
        Calculator c = new CalculatorImpl();
        MarketUpdate mu = new MktUpdate(mkt, new TwoWayPriceImpl(inst, state, bidPx, bidAmt, offPx, offAmt));
        try  {
            c.applyMarketUpdate(mu);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().toLowerCase().contains(expected.toLowerCase()));
        }
    }

    @Test
    public void testBrokenMarket() {
        checkErr("market", null, INSTRUMENT0, FIRM, 0, 0, 0, 0);
    }

    @Test
    public void testBrokenInstrument() {
        checkErr("instrument", MARKET0, null, FIRM, 0, 0, 0, 0);
    }

    @Test
    public void testBrokenState() {
        checkErr("state", MARKET0, INSTRUMENT0, null, 0, 0, 0, 0);
    }

    @Test
    public void testBrokenBidPx() {
        checkErr("bid price", MARKET0, INSTRUMENT0, FIRM, Double.NaN, 0, 0, 0);
    }

    @Test
    public void testBrokenBidAmount() {
        checkErr("bid amount", MARKET0, INSTRUMENT0, FIRM, 0, Double.NaN, 0, 0);
    }

    @Test
    public void testBrokenOfferPx() {
        checkErr("offer price", MARKET0, INSTRUMENT0, FIRM, 0, 0, Double.NaN, 0);
    }

    @Test
    public void testBrokenOfferAmount() {
        checkErr("offer amount", MARKET0, INSTRUMENT0, FIRM, 0, 0, 0, Double.NaN);
    }

}