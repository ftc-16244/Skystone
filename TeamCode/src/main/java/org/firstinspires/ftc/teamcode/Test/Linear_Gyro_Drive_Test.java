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

package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Arm;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain_Encoder;
import org.firstinspires.ftc.teamcode.Subsystems.FoundationMover;
import org.firstinspires.ftc.teamcode.Subsystems.Gripper;

import static org.firstinspires.ftc.teamcode.Subsystems.Drivetrain.COUNTS_PER_INCH;
import static org.firstinspires.ftc.teamcode.Subsystems.Drivetrain.DRIVE_SPEED;


@TeleOp(name="Linear Gyro Drive Test", group="Linear Opmode")
//@Disabled
public class Linear_Gyro_Drive_Test extends LinearOpMode {

    // Declare OpMode members.
    ElapsedTime     runtime          =    new ElapsedTime();


    @Override
    public void runOpMode() {


        FoundationMover     foundationMover  =    new FoundationMover();
        Arm                 arm              =    new Arm();
        Gripper             gripper          =    new Gripper();
        Drivetrain  drivetrain       =    new Drivetrain(false);

        // Define the IMU parameters and then Calibrate IMU
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;



        telemetry.addData("Status", "Initialized");
        telemetry.update();

        foundationMover. init(hardwareMap);
        arm.init(hardwareMap);
        gripper.init(hardwareMap);
        drivetrain.init(hardwareMap); // this includes the imu too
        telemetry.addData("Hardware is Initiaized ", "Complete ");
        //position robot into start position - for example the 18x18x18 inch dimensions
        gripper.moveToStartPsn();
        sleep(500);

        arm.armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.armLeft.setPower(-.3);
        arm.armRight.setPower(-.3);
        runtime.reset();


        // wait for calibration


        // now reset the arm by powering it downward for 3 seconds at low power
        arm.resetArmPosn(telemetry);

        // need to initailize the IMU first in the drivetrain init method befre calibration
        while  (runtime.seconds() < 3.0) {
            telemetry.addData("Arm Resetting", "Leg 1: %2.5f S Elapsed", runtime.seconds());

        }

        arm.armLeft.setPower(0);
        arm.armRight.setPower(0);

        // Wait for START to be Pressed

        waitForStart();

        // Opmode Starts Here
        runtime.reset();

        encoderDrive(DRIVE_SPEED, 40, 40, 4);

        //foundationMover.moveToStore(); //lift them so they don't get destroyed
        sleep(500);
        // run until the end of the match (driver presses STOP)

    }


    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;
        // Drive train is used by the encodedrive method, not the Foundation move method.
        // Don't forget to initialize after the drivertain instance is created.
        Drivetrain      drivetrain       =    new Drivetrain(false);
        drivetrain.init(hardwareMap);

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = drivetrain.leftFront.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = drivetrain.rightFront.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            drivetrain.leftFront.setTargetPosition(newLeftTarget);
            drivetrain.rightFront.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            drivetrain.leftFront.setPower(Math.abs(speed));
            drivetrain.rightFront.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (drivetrain.leftFront.isBusy() && drivetrain.rightFront.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Target", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Actual_Position", "Running at %7d :%7d", drivetrain.leftFront.getCurrentPosition(), drivetrain.rightFront.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;

            drivetrain.leftFront.setPower(0);
            drivetrain.rightFront.setPower(0);


            // Turn off RUN_TO_POSITION
            drivetrain.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            drivetrain.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(250);   // optional pause after each move
        }

    }

}
