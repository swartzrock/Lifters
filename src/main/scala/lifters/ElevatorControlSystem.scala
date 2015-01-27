package lifters

import lifters.ElevatorDirection.ElevatorDirection

import scala.collection.mutable


object ElevatorDirection extends Enumeration {
  type ElevatorDirection = Value
  val UP, DOWN = Value
}


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


/**
 * An elevator bank manages one or more elevators in a building.
 *
 * TODO - Better constructor validations (eg numFloors set to -1 should raise an error)
 * TODO - Better method validations (eg requestFloor(-1, -1) should raise errors)
 */
class ElevatorBank(val numFloors: Int, val numElevators: Int) extends ElevatorControlSystem {

  val ids: List[Int] = (100 to (100 + numElevators - 1)).toList

  private val elevatorMap: Map[Int, Elevator] = ids.map(_ -> new Elevator()).toMap
  private def elevators: List[Elevator] = elevatorMap.values.toList


  override def statuses: List[ElevatorStatus] = elevatorMap.toList map { case (i,e) => toStatus(i,e) }
  override def status(id: Int): ElevatorStatus = toStatus(id, elevatorMap(id))
  override def step(): Unit = elevators foreach { e => e.step() }


  override def requestFloor(id: Int, floor: Int): Unit = {
    elevatorMap(id).requestFloor(floor)
  }

  
  override def requestPickup(floor: Int, direction: ElevatorDirection): Unit = {
    val id = shortestDistance(floor)
    elevatorMap(id).requestFloor(floor)
  }


  /**
   * Returns the elevator that will approach the requested floor first
   */
  private def shortestDistance(floor: Int): Int = {
    val distances = elevatorMap.toList map { case (id, e) =>
      val ev = new Elevator()
      ev.currentFloor = e.currentFloor
      ev.destinations ++= e.destinations
      id -> elevatorDistance(ev, floor)
    }

    distances.sortBy(_._2).head._1
  }


  /**
   * Returns the number of steps until an elevator arrives at a destination
   */
  private def elevatorDistance(elevator: Elevator, floor: Int): Int = {
    var steps = 0
    elevator.requestFloor(floor)
    while (elevator.currentFloor != floor) {
      steps += 1
      elevator.step()
    }
    steps
  }



  private def toStatus(i: Int, e: Elevator) = ElevatorStatus(i, e.currentFloor, e.destinations.headOption)

}


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


//  def includes(floor: Int, direction: ElevatorDirection): Boolean = {
//
//
//
//    import ElevatorDirection._
//
//    (currentFloor, destinations.headOption, direction) match {
//
//      case (c, Some(d), UP) => pathIncludes(srcFloor, )
//      case (c, Some(d), UP) if c <= floor && floor <= d => true
//      case (c, Some(d), DOWN) if c >= floor && floor >= d => true
//      case _ => false
//    }
//  }
//
//  def pathIncludes(srcFloor: Int, testFloor: Int, destFloor: Int, direction: ElevatorDirection): Boolean = {
//    import ElevatorDirection._
//
//    (srcFloor, testFloor, destFloor, direction) match {
//      case (a, b, c, UP) if a <= b && b <= c => true
//      case (a, b, c, DOWN) if a >= b && b >= c => true
//      case _ => false
//    }
//  }


//  def direction: ElevatorDirection = {
//
//    import ElevatorDirection._
//
//    (currentFloor, destFloor) match {
//      case (c, None) if c > 0     => DOWN
//      case (c, None)              => UP
//      case (c, Some(d)) if c < d  => UP
//      case (c, Some(d)) if c > d  => DOWN
//      case (c, Some(d)) if c == d =>
//        println("Error - an elevator has the same current floor and destination floor.")
//        UP
//    }
//  }


}















