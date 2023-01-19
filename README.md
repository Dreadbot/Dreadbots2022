# Dreadbots2022

The 3656 Dreadbots 2022 Season repository for FIRST Robotics Competition: RAPID REACT

## FIM District Belleville Competition

- 7th Alliance Quarterfinalist
- FIRST Innovation in Control Award
- 1189 Gearheads Best Pin Design 2022 Award
- 6344 Jiggawattz Best Support Award
- 2834 Bionic Black Hawks Helpful Award

## FIM District Jackson Competition

- 6th Alliance Quarterfinalist
- FIRST Creativity Award

## Robot Description
 - Fram is 24"x36" rectangle (front/back are wider than left/right)
 - Mechanum Wheels for a drivetrain
 - Pickup/Intake mechanism is an open side (front) with a bar across the top of the opening which spins inward to pick up the balls as the robot drives into them
 - Shooting mechanism is a flywheel mounted in a 340 degree turret (20 degree dead zone alligns with the intake)
 - We used computer vision to find/track the goal and the turret tracked the goal as we moved around the field so we would always be ready to shoot.
 - Also used computer vision to compute the distance to the goal and we calculate the speed of the flywheel and the angle to shoot based on that distance.
 - Our climb was a combination of 2 arms, the "power arm" would extend, hook onto the medium bar and lift the robot.  2nd arm would hook on to medium bar once the power arm was fully retracted.  The Power arm would then let go of the medium bar and extend to hook the high bar.  And the process repeats until robot is hanging from traversal bar.
