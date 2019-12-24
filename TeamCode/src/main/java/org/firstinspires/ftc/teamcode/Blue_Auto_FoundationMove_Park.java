/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */
// Code to move the foundation into the building zone during Autonomous Mode

@Autonomous(name="#6A AutoFoundation Move w/ Park", group="Pushbot")
//@Disabled
public class Blue_Auto_FoundationMove_Park extends LinearOpMode {

    /* Declare OpMode members. */
    HardwarePushbot2        robot   = new HardwarePushbot2();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();

    private static final double     COUNTS_PER_MOTOR_REV    = 1120 ;         // REV HD HEX 40:1 motors
    private static final double     DRIVE_GEAR_REDUCTION    = 0.5 ;         // This is < 1.0 if geared UP 20 teeth drive 10 teeth driven
    private static final double     WHEEL_DIAMETER_INCHES   = 3.54 ;   // 90mm wheels. For figuring circumference its a 90 millimeter wheel
    private static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    public static final double     DRIVE_SPEED             = 0.4;
    private static final double     TURN_SPEED              = 0.5;
    private static final double     TRACK_WIDTH             =   15;
    private static final double     COUNTS_PER_ARM_MOTOR_REV    = 280 ;         // REV HD HEX 40:1 motors
    public static final double     ARM_SPEED             = 0.8;
    private static final double     ARM_GEAR_REDUCTION    = 4.0 ;   // This should be 1.0 or more for an arm. Count teeth and calculate
    private static final double     Ticks_Per_Degree        = COUNTS_PER_ARM_MOTOR_REV * ARM_GEAR_REDUCTION/360;

    private static final double     FOUNDATION_UP =0.4;
    private static final double     FOUNDATION_DOWN  =0.65;
    @Override
    public void runOpMode() {

        //Local variables//



        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.arm2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                          robot.leftFront.getCurrentPosition(),
                          robot.rightFront.getCurrentPosition());
        telemetry.update();
        robot.foundationleft.setPosition(FOUNDATION_UP);
        robot.foundationright.setPosition(1-FOUNDATION_UP);

        sleep(250);     // pause for servos to move

        // Wait for the game to start (driver presses PLAY)
        waitForStart(); //once press start, everything below will happen
        //armDrive(ARM_SPEED,  2, 1.);  // S1: 180 degrees counterclockwise
        encoderDrive(DRIVE_SPEED, -12, -12, 4);
        encoderDrive(DRIVE_SPEED, -6, 6, 3); //first turn
        encoderDrive(DRIVE_SPEED, -12, -12, 3);
        encoderDrive(DRIVE_SPEED, 6, -6, 3);
        encoderDrive(DRIVE_SPEED, -13, -13, 4);
        // grab foundation
        robot.foundationleft.setPosition(FOUNDATION_DOWN); //lift them so they don't get destroyed
        robot.foundationright.setPosition(1-FOUNDATION_DOWN);
        sleep(500);     // pause for servos to move

        // Return in an arc to be able to drive to centerline
        leftArcDrive(DRIVE_SPEED,22,115,5);
        encoderDrive(DRIVE_SPEED, -23, -23, 10);
        robot.foundationleft.setPosition(FOUNDATION_UP); //lift them so they don't get destroyed
        robot.foundationright.setPosition(1-FOUNDATION_UP);

        sleep(1000);
        encoderDrive(DRIVE_SPEED, 40, 40, 4);


        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightFront.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            robot.leftFront.setTargetPosition(newLeftTarget);
            robot.rightFront.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftFront.setPower(Math.abs(speed));
            robot.rightFront.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (robot.leftFront.isBusy() && robot.rightFront.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Target",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Actual_Position",  "Running at %7d :%7d",robot.leftFront.getCurrentPosition(),robot.rightFront.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;

            robot.leftFront.setPower(0);
            robot.rightFront.setPower(0);


            // Turn off RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(250);   // optional pause after each move
        }



    }

    public void armDrive(double speed,
                         double armDegrees, double timeoutS) {

        int newArmTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newArmTarget = robot.arm.getCurrentPosition() + (int) (armDegrees * Ticks_Per_Degree);
            // set target position before "run to position"
            robot.arm.setTargetPosition(newArmTarget);
            robot.arm2.setTargetPosition(newArmTarget);
            // Turn On RUN_TO_POSITION
            robot.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.arm2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // reset the timeout time and start motion.
            runtime.reset();
            robot.arm.setPower(Math.abs(speed));
            robot.arm2.setPower(Math.abs(speed));
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.arm.isBusy() )) {

                // Display it for the driver.
                telemetry.addData("Arm Target",  "Running to %7d", newArmTarget);
                telemetry.addData("Arm Position",  "Running at %7d",robot.arm.getCurrentPosition());
                telemetry.update();
            }
            // Stop all motion;
            robot.arm.setPower(0);
            robot.arm2.setPower(0);
            // Turn off RUN_TO_POSITION
            robot.arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.arm2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            sleep(250);   // optional pause after each move

        }

    }
    public void leftArcDrive(double speed,
                             double radius, double sweptAngle,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;
        double leftSpeed;
        double rightSpeed;
        double leftDist;
        double rightDist;
        double centerlineDist;
        double leftFrac;
        double rightFrac;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            leftDist = sweptAngle/360*3.14159*(radius-(TRACK_WIDTH /2))*2; // distance leftwheel needs to go
            rightDist = sweptAngle/360*3.14159*(radius+(TRACK_WIDTH /2))*2; // distance right wheel needs to go
            newLeftTarget = robot.leftFront.getCurrentPosition() + (int)(leftDist * COUNTS_PER_INCH);
            newRightTarget = robot.rightFront.getCurrentPosition() + (int)(rightDist * COUNTS_PER_INCH);
            centerlineDist =  sweptAngle/360*3.14159*(radius*2); // distance center needs to go
            leftFrac = leftDist/ centerlineDist;
            rightFrac= rightDist/centerlineDist;
            leftSpeed = speed  * leftFrac;
            rightSpeed =  speed  * rightFrac;

            // set target positions
            robot.leftFront.setTargetPosition(newLeftTarget);
            robot.rightFront.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftFront.setPower(Math.abs(leftSpeed));
            robot.rightFront.setPower(Math.abs(rightSpeed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftFront.isBusy() || robot.rightFront.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Target",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Actual_Position",  "Running at %7d :%7d",robot.leftFront.getCurrentPosition(),robot.rightFront.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;

            robot.leftFront.setPower(0);
            robot.rightFront.setPower(0);


            // Turn off RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(250);   // optional pause after each move
        }



    }

}