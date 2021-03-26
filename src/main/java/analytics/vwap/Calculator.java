package analytics.vwap;

public interface Calculator {
    TwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice);
}