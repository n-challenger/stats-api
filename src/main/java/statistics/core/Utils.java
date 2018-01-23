package statistics.core;

interface ITimeProvider {
    long now();
}

class RealTimeProvider implements ITimeProvider{

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}