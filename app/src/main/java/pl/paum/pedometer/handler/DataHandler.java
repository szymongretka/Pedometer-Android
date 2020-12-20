package pl.paum.pedometer.handler;

public interface DataHandler {
    void saveToMemory(int numOfSteps);
    void exportDataToCsv();
}
