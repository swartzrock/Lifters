# Lifters

A basic elevator bank simulation, written in Scala.


## Building And Testing

Lifters is built with [sbt](https://github.com/sbt/sbt).

To compile and test the simulation, run the following:

  $ sbt test
  

To compile without testing, run the following:

  $ sbt compile


## Using Lifters

The core element is an `ElevatorControlSystem` instance. The one implementation is `ElevatorBank` which takes the number of floors in the bank (assumes all elevators visit the same floors) and the number of elevators.


### API

There are three types of operations in `ElevatorControlSystem`:

1. Reading information about the elevators
2. Pushing an "up" or "down" button on a floor or a specific floor button in an elevator
3. Manually stepping the elevator bank simulation to the next transition for every elevator.

Here are the public methods for the elevator system.


    /**
     * Return a list of the elevator id's in this system
     */
    def ids: List[Int]

    /**
     * Return a list of the elevator status's in this system
     */
    def status: List[ElevatorStatus]

    /**
     * Return the status of a specific elevator, by id
     */
    def status(id: Int): ElevatorStatus

    /**
     * Request that a specific elevator visit a specific floor.
     * TL;DR - a rider pressed a floor button inside an elevator
     */
    def requestFloor(id: Int, floor: Int)

    /**
     * Requests that an elevator visit the floor to pick up a rider
     * and take them in the requested direction
     */
    def pickup(floor: Int, direction: ElevatorDirection)

    /**
     * Step through the next transition in this system
     */
    def step(): Unit



### Scheduling Algorithm

The algorithm is not first-come, first-served, but calculates the shortest path from all elevators. It does this in a rather brute-force way, however, due to time constraints.

Here is the algorithm:

1. Pick a floor one away from the requested floor in the requested direction
2. For all elevators, copy the elevator, add the floor to the destination, and count how many steps it takes until the elevator arrives there
3. Pick the shortest path and add the requested floor to that elevator's destination

Problems:

* Feels brute-force-y - there's probably a good way to predict this without manual steps



### Design Notes

* The product spec mentioned the feature "receiving an update about the status of an elevator".
However the wording here was unclear, as it could mean there must be a way to read the status of an elevator
versus writing the status of the elevator. As the former is covered by an API call, and the latter
breaks the model of a simulation, this feature may not have been implemented as requested.

* The elevator id is an integer, which works for elevator banks (you probably won't have > 2**32 elevators in a building)
but could be replaced by a non-sequential type, eg a UUID.

* The "floor" value is zero-based, which may be confusing. Ie 1st floor may be represented as floor zero. A better type could address this.

* A future version should support floor differentiation between elevators. For example,
one elevator may serve floors 1 through 10 while another serves floors 1 and 10 - 20.








