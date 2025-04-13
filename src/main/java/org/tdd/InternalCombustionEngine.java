package org.tdd;

/**
 * @desc:
 * @author: Leif
 * @date: 2025/3/20 13:17
 */
public final class InternalCombustionEngine implements Engine {

  private final FuelType fuelType;
  private boolean running;
  private static final int BATCH_SIZE = 8;

  public InternalCombustionEngine(FuelType fuelType) {
    if (fuelType != FuelType.PETROL && fuelType != FuelType.DIESEL) {
      throw new IllegalArgumentException("Internal combustion engine only supports PETROL or DIESEL");
    }
    this.fuelType = fuelType;
  }

  @Override
  public void start() {
    running = true;
  }

  @Override
  public void stop() {
    running = false;
  }

  @Override
  public double calculateCost(int quantity) {
    if (!running) {
      throw new IllegalStateException("Engine must be started before calculating cost");
    }
    int batches = (int) Math.ceil(quantity / (double) BATCH_SIZE); // 8 widgets per batch
    return batches * fuelType.getCostPerBatch();
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public int getBatchSize() {
    return BATCH_SIZE;
  }
}
