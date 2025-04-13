package org.tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.tdd.FuelType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @desc: Unit tests for main scenarios
 * @author: Leif
 * @date: 2025/3/20 14:41
 */
class MachineTest {
  private Machine machine;

  @BeforeEach
  void setUp() {
    machine = null;
  }

  // 1.Happy path tests
  @ParameterizedTest
  @DisplayName("Test valid production scenarios")
  @CsvSource({
    "WOOD, 2, 4.35", // 2 widgets = 1 batch
    "COAL, 4, 11.30", // 4 widgets = 2 batches
    "PETROL, 8, 6.75", // 8 widgets = 1 batch
    "DIESEL, 16, 14.5" // 16 widgets = 2 batches
  })
  void testProduceWidgets(String fuel, int quantity, double expectedCost) {
    // Mapping the fuel type from the string to the actual FuelType record
    FuelType fuelType;
    switch (fuel) {
      case "WOOD" -> fuelType = FuelType.WOOD;
      case "COAL" -> fuelType = FuelType.COAL;
      case "PETROL" -> fuelType = FuelType.PETROL;
      case "DIESEL" -> fuelType = FuelType.DIESEL;
      default -> throw new IllegalArgumentException("Unknown fuel type: " + fuel);
    }

    // Creating the engine based on the fuel type
    Engine engine = switch (fuelType.name()) {  // 使用 name() 方法来获取 FuelType 的名称字符串
      case "Wood", "Coal" -> new SteamEngine(fuelType);
      case "Petrol", "Diesel" -> new InternalCombustionEngine(fuelType);
      default -> throw new IllegalArgumentException("Unsupported fuel type: " + fuelType);
    };

    // Create a Machine with the engine
    Machine machine = new Machine(engine);

    // Calculate the production cost
    double cost = machine.produceWidgets(quantity);

    // Assert that the calculated cost matches the expected cost, within a tolerance
    assertEquals(expectedCost, cost, 0.01);
  }

  // 2.Test zero widgets (Invalid Case)
  @Test
  @DisplayName("Test producing zero widgets throws exception")
  void testZeroWidgetsThrowsException() {
    Engine engine = new SteamEngine(FuelType.WOOD);
    machine = new Machine(engine);
    assertThrows(IllegalArgumentException.class, () -> machine.produceWidgets(0));
  }

  // 3.Test unsupported fuel type
  @Test
  @DisplayName("Test unsupported fuel type throws exception")
  void testInvalidFuelTypeThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new SteamEngine(PETROL));
  }

  // 4.Test engine state before starting
  @Test
  @DisplayName("Test producing widgets without starting engine")
  void testProducingWithoutStartingEngineThrowsException() {
    Engine engine = new SteamEngine(FuelType.WOOD);
    assertThrows(IllegalStateException.class, () -> engine.calculateCost(5));
  }

  // 5.Test engine reuse after stopping
  @Test
  @DisplayName("Test engine can be reused after restarting")
  void testEngineCanBeRestarted() {
    Engine engine = new SteamEngine(FuelType.WOOD);
    machine = new Machine(engine);
    machine.produceWidgets(4); // Success
    machine.stopEngine();

    engine.start(); // Restart engine
    double cost = machine.produceWidgets(4);
    assertEquals(8.70, cost, 0.01);
  }

  // 6.Large Batch Test
  @Test
  @DisplayName("Test large quantity production")
  void testLargeQuantityProduction() {
    Engine engine = new InternalCombustionEngine(PETROL);
    machine = new Machine(engine);
    double cost = machine.produceWidgets(10000);
    assertEquals(8437.5, cost, 0.01);
  }

  // 7.Performance Test
  @ParameterizedTest
  @MethodSource("fuelTypesProvider")
  @DisplayName("Test large quantity production under concurrent load with different engines and fuels")
  void testConcurrentProduction(FuelType fuelType) throws Exception {
    int threadCount = 100; // Number of threads to create
    int batchSize = 100; // Number of widgets produced per thread

    Engine engine = new InternalCombustionEngine(fuelType);
    Machine machine = new Machine(engine);

    // Create a fixed thread pool with 100 threads
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    // Create tasks for each thread using CompletableFuture
    CompletableFuture<Double>[] futures = IntStream.range(0, threadCount)
      .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
        try {
          // Each thread produces 'batchSize' widgets
          double res = machine.produceWidgets(batchSize);
          System.out.println("Thread " + Thread.currentThread().getName() + " finished producing " + batchSize + " widgets.");
          return res;

        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }, executor))
      .toArray(CompletableFuture[]::new);

    // Wait for all threads to finish
    CompletableFuture.allOf(futures).join();

    double totalCost = 0.00;
    for (CompletableFuture<Double> future : futures) {
      totalCost += future.get(); // Get the result of each task
    }

    // Calculate expected cost using dynamic batch size
    int totalBatchesPerThread = (int) Math.ceil(batchSize / (double) engine.getBatchSize());
    double expectedCost = threadCount * totalBatchesPerThread * fuelType.getCostPerBatch();

    assertEquals(expectedCost, totalCost, 0.01);

    // Shut down the thread pool
    executor.shutdown();
  }

  // Method to provide FuelType instances for parameterized tests
  private static Stream<FuelType> fuelTypesProvider() {
    return Stream.of(FuelType.PETROL, FuelType.DIESEL);
  }
}
