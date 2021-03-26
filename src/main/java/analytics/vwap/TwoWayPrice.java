package analytics.vwap;

public interface TwoWayPrice {
    Instrument getInstrument();
    State getState();
    double getBidPrice();
    double getOfferAmount();
    double getOfferPrice();
    double getBidAmount();
}