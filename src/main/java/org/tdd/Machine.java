package org.tdd;

/**
 * @desc: The application entry. When this class is initialized, the object engine will also start automatically.
 * @author: Leif
 * @date: 2025/3/20 13:25
 */
public class Machine {
  private final Engine engine;

  public Machine(Engine engine) {
    this.engine = engine;
    this.engine.start();  // Start the engine when the machine is created.
  }

  /**
   * Produces widgets using the engine and calculates the cost.
   * @param quantity Number of widgets to produce.
   * @return The cost for producing the given number of widgets.
   * @throws IllegalStateException if engine is not running.
   * @throws IllegalArgumentException if quantity is less than or equal to zero.
   */
  public double produceWidgets(int quantity) {
    if (!engine.isRunning()) {
      throw new IllegalStateException("Engine is not running");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than 0");
    }
    return engine.calculateCost(quantity);
  }

  /**
   * Stops the engine.
   */
  public void stopEngine() {
    engine.stop();
  }
}
