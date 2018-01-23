package statistics.core;

import java.util.concurrent.TimeUnit;

/**
 * This implementation helps to fake the system timer in tests to ensure reproducibility.
 */
public class MockTimeProvider implements ITimeProvider {

    public long secondsNow;

    @Override
    public long now() {
        return TimeUnit.SECONDS.toMillis(secondsNow);
    }

}
