package analytics.vwap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class MicroBenchTest {


    private final Market[] markets = Market.values();
    private final Instrument[] instruments= Instrument.values();
    private final Random random = new Random();

    // todo: update in place...
    private MarketUpdate generateUpdate() {
        TwoWayPrice tp = new TwoWayPriceImpl(
                instruments[random.nextInt(20)],
                random.nextInt(500) < 2 ? State.INDICATIVE : State.FIRM,
                random.nextDouble() * 10 + 10,
                random.nextDouble() * 10 + 10,
                random.nextDouble() * 10 + 10,
                random.nextDouble() * 10 + 10
                );
        return new MktUpdate(markets[random.nextInt(50)], tp);
    }

    public TwoWayPrice addUpdates(Calculator c, int count, MarketUpdate[] updates) {
        TwoWayPrice result = null;
        int available = updates.length;
        for(int i = 0; i < count; i++) {
            result = c.applyMarketUpdate(updates[i % available]);
        }
        return result;
    }

    public MarketUpdate[] generateUpdates(int count) {
        MarketUpdate[] updates = new MarketUpdate[count];
        for (int i = 0; i < count; i++) {
            updates[i] = generateUpdate();
        }
        return updates;
    }

    @Test
    public void runMicroBench() {
        int count = 1000000;
        MarketUpdate[] updates = generateUpdates(1000000);
        Calculator c = new CalculatorImpl();
        // do bench;
        double[] times = new double[10];
        for(int i = 0; i < 10; i++) {
            long begin = System.nanoTime();
            TwoWayPrice result = addUpdates(c, count, updates);
            long end = System.nanoTime();
            times[i] = end - begin;
            Assertions.assertNotNull(result);   // force use of result, to avoid optimizations
        }
        StringBuilder sb = new StringBuilder("Times: ");
        for(double t : times) {
            sb.append(String.format("%.2f, ", t/count));
        }
        System.out.println(sb.toString());
    }

    // This is just to give a rough estimate of the time to do a single update (in ns)
    // sample output (on MacBook Air) notice some warmup effect and jitter
    // Times: 42.15, 31.58, 25.97, 22.43, 25.72, 26.38, 27.55, 24.13, 23.03, 20.65,

}
