package lifters


import scala.collection.mutable

/**
 * Represents an elevator in a building with mutable state
 *
 */
class Elevator() {

  var currentFloor: Int = 0
  val destinations: mutable.ListBuffer[Int] = mutable.ListBuffer[Int]()


  def requestFloor(floor: Int): Unit = {
    destinations += floor
  }


  def step(): Unit = {
    currentFloor = (currentFloor, destinations.headOption) match {
      case (c, Some(d)) if c < d => c + 1
      case (c, Some(d)) if c > d => c - 1
      case (c, _) => c
    }

    // Remove the current floor from the list of destinations
    destinations -= currentFloor
  }

}



object ElevatorDirection extends Enumeration {
  type ElevatorDirection = Value
  val UP, DOWN = Value
}

