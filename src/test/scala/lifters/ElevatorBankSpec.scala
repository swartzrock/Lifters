package lifters

import org.scalatest._


class ElevatorBankSpec extends FlatSpec with ShouldMatchers with OptionValues {

  import ElevatorDirection._

  "The Elevator Bank" should "start elevators on the ground floor" in {
    val bank = new ElevatorBank(10, 4)
    bank.statuses.size should equal(4)
    bank.statuses.map(_.currentFloor).distinct should equal(List(0))
  }

  it should "start elevators without a dest floor" in {
    val bank = new ElevatorBank(10, 4)
    bank.statuses.map(_.destFloor).distinct should equal(List(None))
  }

  it should "generate a unique id for every elevator" in {
    val bank = new ElevatorBank(10, 16)
    bank.ids.distinct.size should equal(16)
  }

  it should "send an elevator to pickup a rider" in {
    val bank = new ElevatorBank(2, 1)
    bank.statuses.head.currentFloor should equal(0)

    bank.requestPickup(1, DOWN)
    bank.statuses.head.currentFloor should equal(0) // Elevators shouldn't move w/o manual step() calls
    bank.statuses.head.destFloor should equal(Some(1))

    step(bank, 1)
    bank.statuses.head.currentFloor should equal(1)
  }

  it should "pickup a rider and take them to their floor" in {
    val bank = new ElevatorBank(4, 1)

    bank.requestPickup(2, DOWN)
    step(bank, 2)
    val id = bank.statuses.find(_.currentFloor == 2).value.id

    bank.requestFloor(id, 1)
    step(bank, 1)
    bank.status(id).currentFloor should equal(1)
  }

  it should "ensure 1 elevator can take 2 riders to 2 floors" in {
    val bank = new ElevatorBank(4, 1)

    bank.requestPickup(0, UP)
    step(bank, 1)
    val id = elevatorAt(bank, 0).id

    bank.requestFloor(id, 1)
    bank.requestFloor(id, 3)
    bank.status(id).currentFloor should equal(0)
    bank.status(id).destFloor should equal(Some(1))

    step(bank, 1)
    bank.status(id).currentFloor should equal(1)
    bank.status(id).destFloor should equal(Some(3))

    step(bank, 2)
    bank.status(id).currentFloor should equal(3)
    bank.status(id).destFloor should be(None)
  }

  it should "ensure 1 elevator can take 3 riders to 3 floors in 1 direction in optimal order" in {
    val bank = new ElevatorBank(10, 1)

    bank.requestPickup(0, UP)
    step(bank, 1)
    val id = elevatorAt(bank, 0).id

    bank.requestFloor(id, 4)
    bank.requestFloor(id, 2)
    bank.requestFloor(id, 3)
    bank.status(id).currentFloor should equal(0)

    step(bank, 2)
    bank.status(id).currentFloor should equal(2)

    step(bank, 1)
    bank.status(id).currentFloor should equal(3)

    step(bank, 1)
    bank.status(id).currentFloor should equal(4)
    bank.status(id).destFloor should be(None)
  }


  it should "ensure 1 elevator can allow in-flight floor addition" in {
    val bank = new ElevatorBank(10, 1)

    bank.requestPickup(0, UP)
    step(bank, 1)
    val id = elevatorAt(bank, 0).id

    bank.requestFloor(id, 4)
    bank.status(id).currentFloor should equal(0)

    step(bank, 2)
    bank.status(id).currentFloor should equal(2)
    bank.requestFloor(id, 6)

    step(bank, 2)
    bank.status(id).currentFloor should equal(4)
    bank.status(id).destFloor should equal(Some(6))

    step(bank, 2)
    bank.status(id).currentFloor should equal(6)
    bank.status(id).destFloor should be(None)
  }

  it should "pick the closest elevator for a one-floor non-inclusive trip" in {
    val bank = new ElevatorBank(10, 2)

    bank.requestPickup(6, UP)
    step(bank, 6)
    val nearestId = elevatorAt(bank, 6).id
    val furthestId = elevatorAt(bank, 0).id

    bank.requestPickup(7, UP)
    step(bank, 1)
    bank.status(nearestId).currentFloor should equal(7)
    bank.status(furthestId).currentFloor should equal(0)
  }





  def elevatorAt(bank: ElevatorBank, floor: Int) = bank.statuses.find(_.currentFloor == floor).value

  def step(bank: ElevatorBank, steps: Int) = for (i <- 1 to steps) { bank.step() }
}
