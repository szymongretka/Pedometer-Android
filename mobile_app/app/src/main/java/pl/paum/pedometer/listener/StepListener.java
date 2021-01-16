package pl.paum.pedometer.listener;

/**
 * Interface listening to alerts about steps being detected.
 */
public interface StepListener {
    void step(long timeNs);
}
