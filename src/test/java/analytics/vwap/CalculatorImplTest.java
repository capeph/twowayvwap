package analytics.vwap;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static analytics.vwap.Instrument.INSTRUMENT0;
import static analytics.vwap.Market.*;
import static analytics.vwap.State.FIRM;
import static analytics.vwap.State.INDICATIVE;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorImplTest {


    public TwoWayPrice addValue(Calculator c, Instrument i, Market m, State s,
                         double bidpx, double bidvol, double offerpx, double offervol) {

        TwoWayPrice tp = new TwoWayPriceImpl(i, s, bidpx,  bidvol, offerpx, offervol);
        return c.applyMarketUpdate(new MktUpdate(m, tp));
    }

    public TwoWayPrice bvwap(Calculator c, double[] prices, double[] volumes) {
        assertEquals(prices.length, volumes.length);
        TwoWayPrice result = null;
        for (int i = 0; i < prices.length; i++) {
            result = addValue(c, INSTRUMENT0, Market.values()[i], FIRM, prices[i], volumes[i], 0, 0);
        }
        return result;
    }

    public TwoWayPrice ovwap(Calculator c, double[] prices, double[] volumes) {
        assertEquals(prices.length, volumes.length);
        TwoWayPrice result = null;
        for (int i = 0; i < prices.length; i++) {
            result = addValue(c, INSTRUMENT0, Market.values()[i], FIRM, 0, 0, prices[i], volumes[i]);
        }
        return result;
    }

    @Test
    public void testBrokenMarket() {
        Calculator c = new CalculatorImpl();
        MarketUpdate mu = new MktUpdate(null, new TwoWayPriceImpl(INSTRUMENT0, FIRM, 0, 0, 0, 0));
        try  {
            c.applyMarketUpdate(mu);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("market"));
        }
    }

    @Test
    public void testBrokenInstrument() {
        Calculator c = new CalculatorImpl();
        MarketUpdate mu = new MktUpdate(MARKET0, new TwoWayPriceImpl(null, FIRM, 0, 0, 0, 0));
        try  {
            c.applyMarketUpdate(mu);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("instrument"));
        }
    }



    @Test
    public void testSingleBid() {
        double[] prices = {100.0};
        double[] volumes = {100.0};
        TwoWayPrice result = bvwap(new CalculatorImpl(), prices, volumes);
        assertEquals(100, result.getBidPrice());
        assertEquals(0, result.getOfferPrice());
    }

    @Test
    public void testMultiBid() {
        double[] prices = {100.0, 110.0, 120.0, 110.0, 120.0, 110};
        double[] volumes = {100.0, 50.0, 80.0, 120.0, 70.0, 80};
        TwoWayPrice result = bvwap(new CalculatorImpl(), prices, volumes);
        assertEquals(111, result.getBidPrice());
        assertEquals(500, result.getBidAmount());
        assertEquals(0, result.getOfferPrice());
    }

    @Test
    public void testUseNewValuesOnlyBid() {
        double[] aprices = {54, 12.0, 78.0, 16.0, 34.0, 40};
        double[] avolumes = {10.0, 20.0, 83.0, 12.0, 7.0, 13};
        double[] bprices = {100.0, 110.0, 120.0, 110.0, 120.0, 110};
        double[] bvolumes = {100.0, 50.0, 80.0, 120.0, 70.0, 80};
        Calculator calc = new CalculatorImpl();
        TwoWayPrice aresult = bvwap(calc, aprices, avolumes);
        TwoWayPrice bresult = bvwap(calc, bprices, bvolumes);
        assertEquals(111, bresult.getBidPrice());
        assertEquals(500, bresult.getBidAmount());
        assertEquals(0, bresult.getOfferPrice());
    }



    @Test
    public void testSingleOffer() {
        double[] prices = {100.0};
        double[] volumes = {100.0};
        TwoWayPrice result = ovwap(new CalculatorImpl(), prices, volumes);
        assertEquals(100, result.getOfferPrice());
        assertEquals(0, result.getBidPrice());
    }

    @Test
    public void testMultiOffer() {
        double[] prices = {100.0, 110.0, 120.0, 110.0, 120.0, 110};
        double[] volumes = {100.0, 50.0, 80.0, 120.0, 70.0, 80};
        TwoWayPrice result = ovwap(new CalculatorImpl(), prices, volumes);
        assertEquals(111, result.getOfferPrice());
        assertEquals(500, result.getOfferAmount());
        assertEquals(0, result.getBidPrice());
    }

    @Test
    public void testUseNewValuesOnlyOffer() {
        double[] aprices = {54, 12.0, 78.0, 16.0, 34.0, 40};
        double[] avolumes = {10.0, 20.0, 83.0, 12.0, 7.0, 13};
        double[] bprices = {100.0, 110.0, 120.0, 110.0, 120.0, 110};
        double[] bvolumes = {100.0, 50.0, 80.0, 120.0, 70.0, 80};
        Calculator calc = new CalculatorImpl();
        TwoWayPrice aresult = ovwap(calc, aprices, avolumes);
        TwoWayPrice bresult = ovwap(calc, bprices, bvolumes);
        assertEquals(111, bresult.getOfferPrice());
        assertEquals(500, bresult.getOfferAmount());
        assertEquals(0, bresult.getBidPrice());
    }

    @Test
    public void testIndicative() {
        Calculator c = new CalculatorImpl();
        TwoWayPrice result = addValue(c, INSTRUMENT0, MARKET0, FIRM, 10, 10, 0, 0);
        assertEquals(FIRM, result.getState());
        result = addValue(c, INSTRUMENT0, MARKET1, INDICATIVE, 10, 10, 0, 0);
        assertEquals(INDICATIVE, result.getState());
        result = addValue(c, INSTRUMENT0, MARKET3, INDICATIVE, 10, 10, 0, 0);
        assertEquals(INDICATIVE, result.getState());
        result = addValue(c, INSTRUMENT0, MARKET1, FIRM, 10, 10, 0, 0);
        assertEquals(INDICATIVE, result.getState());
        result = addValue(c, INSTRUMENT0, MARKET2, FIRM, 10, 10, 0, 0);
        assertEquals(INDICATIVE, result.getState());
        result = addValue(c, INSTRUMENT0, MARKET3, FIRM, 10, 10, 0, 0);
        assertEquals(FIRM, result.getState());
    }




}