public static void main(String [] args)
{
      UrRobot Karel = new UrRobot(1, 1, East, 0);
            // Deliver the robot to the origin (1,1),
            // facing East, with no beepers.
      Karel.move();
      Karel.move();
      Karel.move();
      Karel.pickBeeper();
      Karel.turnOff();
}
