package org.tdd;

/**
 * @desc:
 * @author: Leif
 * @date: 2025/3/20 12:56
 */
public final class SteamEngine implements Engine {

  private final FuelType fuelType;
  private boolean running;
  private static final int BATCH_SIZE = 2;

  public SteamEngine(FuelType fuelType) {
    if (fuelType != FuelType.WOOD && fuelType != FuelType.COAL) {
      throw new IllegalArgumentException("Steam engine only supports WOOD or COAL");
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
    int batches = (int) Math.ceil(quantity / (double) BATCH_SIZE); // 2 widgets per batch
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
