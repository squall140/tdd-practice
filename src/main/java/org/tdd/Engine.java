package org.tdd;

/**
 * @desc: Define main methods for Engine
 * @author: Leif
 * @date: 2025/3/20 12:15
 */
public sealed interface Engine permits InternalCombustionEngine, SteamEngine {
  /**
   * Start the engine.
   */
  void start();

  /**
   * Stop the engine.
   */
  void stop();

  /**
   * Calculate the cost for producing widgets.
   * @param quantity Number of widgets to produce.
   * @return Cost of production.
   */
  double calculateCost(int quantity);

  /**
   * Check if the engine is currently running.
   * @return true if engine is running, false otherwise.
   */
  boolean isRunning();

  /**
   * Dynamically get the production batch size for current engine.
   * @return batch size of current engine
   */
  int getBatchSize();
}
