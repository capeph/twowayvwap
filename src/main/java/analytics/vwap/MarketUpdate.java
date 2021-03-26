package analytics.vwap;

public interface MarketUpdate {
    Market getMarket();
    TwoWayPrice getTwoWayPrice();
}
