package analytics.vwap;

import java.util.EnumMap;
import java.util.Map;

public class CalculatorImpl implements Calculator {

    // for each instrument the latest updates for all markets are stored
    // the weighted average will be calculated for the latest value provided for all markets
    // an optimizatio as follows:
    // we keep a total value and total volume for each instrument
    // on an update we reduce with the old values for the updated market and increase with the new
    // marketstate is tracked by counting the number of indicative values, non-zero -> indicative

    // Class to keep intermediate results,
    private static class InstrumentState {
        double bidValue = 0.0;
        double bidVolume = 0.0;
        double offerValue = 0.0;
        double offerVolume = 0.0;
        int indicativeCount = 0;

        public void reduceBy(InstrumentState other) {
            bidValue -= other.bidValue;
            bidVolume -= other.bidVolume;
            offerValue -= other.offerValue;
            offerVolume -= other.offerVolume;
            indicativeCount -= other.indicativeCount;
        }

        public void increaseBy(InstrumentState other) {
            bidValue += other.bidValue;
            bidVolume += other.bidVolume;
            offerValue += other.offerValue;
            offerVolume += other.offerVolume;
            indicativeCount += other.indicativeCount;
        }

        // allow update in place to save some memory
        private void populateFrom(TwoWayPrice input) {
            bidVolume = input.getBidAmount();
            bidValue = input.getBidPrice() * bidVolume;
            offerVolume = input.getOfferAmount();
            offerValue = input.getOfferPrice() * offerVolume;
            indicativeCount = input.getState() == State.INDICATIVE ? 1 : 0;
        }
    }

    // map to keep the intermediate results for each market for each instrument
    Map<Instrument, Map<Market, InstrumentState>> lastValues;
    // map to keep the current intermediate results fror each instrument
    Map<Instrument, InstrumentState> currentValues;

    // Constructor: pre-allocate all the maps
    public CalculatorImpl() {
        lastValues = new EnumMap<>(Instrument.class);
        currentValues = new EnumMap<>(Instrument.class);
        for(Instrument instrument : Instrument.values()) {
            lastValues.put(instrument, generateMarketMap());
            currentValues.put(instrument, new InstrumentState());
        }
    }

    // break out method to improve readablility
    private Map<Market, InstrumentState> generateMarketMap() {
        Map<Market, InstrumentState>  marketMap = new EnumMap<>(Market.class);
        for(Market market: Market.values()) {
            marketMap.put(market, new InstrumentState());
        }
        return marketMap;
    }

    public void validate(MarketUpdate mu) {
        if (mu == null) {
            throw new IllegalArgumentException("Market update not specified.");
        }
        if (mu.getMarket() == null) {
            throw new IllegalArgumentException("Market not specified.");
        }
        TwoWayPrice tp = mu.getTwoWayPrice();
        if (tp == null) {
            throw new IllegalArgumentException("Two-Way price not specified.");
        }
        if (tp.getInstrument() == null) {
            throw new IllegalArgumentException("Instrument not specified.");
        }
        if (tp.getState() == null) {
            throw new IllegalArgumentException("State not specified.");
        }
        if (!Double.isFinite(tp.getBidPrice())) {
            throw new IllegalArgumentException("Bid price is invalid.");
        }
        if (!Double.isFinite(tp.getBidAmount())) {
            throw new IllegalArgumentException("Bid amount is invalid.");
        }
        if (!Double.isFinite(tp.getOfferPrice())) {
            throw new IllegalArgumentException("Offer price is invalid.");
        }
        if (!Double.isFinite(tp.getOfferAmount())) {
            throw new IllegalArgumentException("Offer amount is invalid.");
        }
    }

    // return an updated VWAP based on the new input
    // the calculator will fail for any bad input
    @Override
    public TwoWayPrice applyMarketUpdate(MarketUpdate twoWayMarketPrice)  {
        validate(twoWayMarketPrice);
        TwoWayPrice input = twoWayMarketPrice.getTwoWayPrice();
        Instrument instrument = input.getInstrument();
        Map<Market, InstrumentState> lastMap = lastValues.get(instrument);
        InstrumentState state = lastMap.get(twoWayMarketPrice.getMarket());
        InstrumentState current = currentValues.get(instrument);
        current.reduceBy(state);   // reduce by previous update for the market
        state.populateFrom(input);
        current.increaseBy(state);  // update with latest value
        return new TwoWayPriceImpl(instrument,
                current.indicativeCount > 0 ? State.INDICATIVE : State.FIRM,
                current.bidValue == 0.0 ? 0.0 : current.bidValue / current.bidVolume,
                current.bidVolume,
                current.offerValue == 0.0 ? 0.0 : current.offerValue / current.offerVolume,
                current.offerVolume);
    }
}
