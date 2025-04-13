package org.tdd;

/**
 * @desc: Enumerating all the fuel types for the steam engine and internal combustion engine.
 * @author: Leif
 * @date: 2025/3/20 12:30
 */
public record FuelType(String name, double costPerBatch) {
  public static final FuelType WOOD = new FuelType("Wood", 4.35);
  public static final FuelType COAL = new FuelType("Coal", 5.65);
  public static final FuelType PETROL = new FuelType("Petrol", 6.75);
  public static final FuelType DIESEL = new FuelType("Diesel", 7.25);

  /**
   * Getter for the cost per batch.
   * @return the cost of the fuel type per batch.
   */
  public double getCostPerBatch() {
    return costPerBatch;
  }

}
