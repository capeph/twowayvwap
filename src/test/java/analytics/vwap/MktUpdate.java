package analytics.vwap;

class MktUpdate implements MarketUpdate {

    private final Market mkt;
    private final TwoWayPrice price;

    MktUpdate(Market mkt, TwoWayPrice price) {
        this.mkt = mkt;
        this.price = price;
    }

    @Override
    public Market getMarket() {
        return mkt;
    }

    @Override
    public TwoWayPrice getTwoWayPrice() {
        return price;
    }
}
