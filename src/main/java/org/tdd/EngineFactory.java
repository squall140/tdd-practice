package org.tdd;

import java.util.Objects;

/**
 * @desc:
 * @author: Leif
 * @date: 2025/3/20 12:41
 */
public class EngineFactory {
  public Engine createEngine(String fuelType) {
    Objects.requireNonNull(fuelType, "Fuel type cannot be null");

    Engine engine = switch (fuelType) {
      case "WOOD" -> new SteamEngine(FuelType.WOOD);
      case "COAL" -> new SteamEngine(FuelType.COAL);
      case "PETROL" -> new InternalCombustionEngine(FuelType.PETROL);
      case "DIESEL" -> new InternalCombustionEngine(FuelType.DIESEL);
      default -> throw new IllegalArgumentException("Invalid fuel type: " + fuelType);
    };

    return engine;
  }
}
