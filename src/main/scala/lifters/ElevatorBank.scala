package lifters

import lifters.ElevatorDirection.ElevatorDirection

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



