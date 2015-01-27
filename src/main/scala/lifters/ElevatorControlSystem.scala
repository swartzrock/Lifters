package lifters

import lifters.ElevatorDirection.ElevatorDirection


/**
 * Contains information about a single elevator
 *
 * TODO - the "id" field probably doesn't belong in a "status" class
 * TODO - should "floor" be an external reference, or a type other than int? It isn't a quantity.
 *
 * @param id the id of the elevator in the system
 * @param currentFloor the current floor that this elevator is on
 * @param destFloor the destination floor that this elevator will visit next
 */
case class ElevatorStatus(id: Int, currentFloor: Int, destFloor: Option[Int])



trait ElevatorControlSystem {

  /**
   * Return a list of the elevator id's in this system
   */
  def ids: List[Int]

  /**
   * Return a list of the elevator status's in this system
   */
  def statuses: List[ElevatorStatus]

  /**
   * Return the status of a specific elevator, by id
   *
   * @param id the elevator id
   */
  def status(id: Int): ElevatorStatus

  /**
   * Requests that an elevator visit the floor to pick up a rider
   * and take them in the requested direction.
   *
   * Should result in one elevator adding the requested floor as a destination.
   *
   * @param floor the zero-based floor number
   * @param direction the direction in which the rider is requesting to travel
   */
  def requestPickup(floor: Int, direction: ElevatorDirection): Unit

  /**
   * Request that a specific elevator visit a specific floor.
   * TL;DR - a rider pressed a floor button inside an elevator.
   *
   * Should result in the specified elevator adding the requested floor as a destination.
   *
   * @param id the elevator id
   * @param floor the zero-based floor number
   */
  def requestFloor(id: Int, floor: Int): Unit

  /**
   * Step through the next transition in this system.
   * Moves all elevators zero or one floor away.
   */
  def step(): Unit
}














