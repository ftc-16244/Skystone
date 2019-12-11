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
 * This file illustrates the concept of driving a arced path based on encoder counts
 * and a speed differential for the left and right wheels.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 * *
 */
// Code to move the foundation into the building zone during Autonomous Mode

@Autonomous(name="Arc_Example", group="Pushbot")
//@Disabled
public class Arc_Example extends LinearOpMode {

    /* Declare OpMode members. */
    HardwarePushbot2        robot   = new HardwarePushbot2();   // Use a Pushbot's hardware
    private ElapsedTime     runtime = new ElapsedTime();

    private static final double     COUNTS_PER_MOTOR_REV    =   1120 ;         // REV HD HEX 40:1 motors
    private static final double     DRIVE_GEAR_REDUCTION    =   0.5 ;         // This is < 1.0 if geared UP 20 teeth drive 10 teeth driven
    private static final double     WHEEL_DIAMETER_INCHES   =   3.54 ;   // 90mm wheels. For figuring circumference its a 90 millimeter wheel
    private static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    public static final double      DRIVE_SPEED             =   0.5;

    public static final double      ARM_SPEED               =   0.8;
    private static final double     TRACK_WIDTH             =   15;

    @Override
    public void runOpMode() {

         /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
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


        sleep(1000);     // pause for servos to move

        // Wait for the game to start (driver presses PLAY)
        waitForStart(); //once press start, everything below will happen

        leftArcDrive(DRIVE_SPEED,25,90,4);


       sleep(1000);



        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a variable radius arc by varying wheel sppeds and using encoders at
     *  the same tiem.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
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
            newRightTarget = robot.rightFront.getCurrentPosition() + (int)(leftDist * COUNTS_PER_INCH);
            centerlineDist =  sweptAngle/360*3.14159*(radius*2); // distance center needs to go
            leftFrac = leftDist/ centerlineDist;
            rightFrac= rightDist/centerlineDist;
            leftSpeed = DRIVE_SPEED  * leftFrac;
            rightSpeed =  DRIVE_SPEED  * rightFrac;


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



}