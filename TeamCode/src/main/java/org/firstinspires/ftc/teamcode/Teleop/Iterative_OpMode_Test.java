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

package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.Arm;
import org.firstinspires.ftc.teamcode.Subsystems.FoundationMover;
import org.firstinspires.ftc.teamcode.Subsystems.Gripper;




@TeleOp(name="Iterative OpMode Test ", group="Teleop")
//@Disabled
public class Iterative_OpMode_Test extends OpMode{


    //set up states to change how the arm operates. Pre-sets or variable.

    FoundationMover foundationMover = new FoundationMover();
    Arm arm = new Arm();
    /* Declare OpMode members. */

    //private Gripper gripper                 = new Gripper(hardwareMap);
    //private Arm arm                         = new Arm(hardwareMap);

       /*
     * Code to run ONCE when the driver hits INIT
     */
    // Constructor


    @Override
    public void init() {
        //ElapsedTime runtime             = new ElapsedTime();

        //Arm arm = new Arm(hardwareMap);
        Gripper gripper = new Gripper(hardwareMap);
        //
       foundationMover. initFdnMoverServo(hardwareMap);
       arm.initArmMotors(hardwareMap);

        gripper.moveToStartPsn();
        telemetry.addData("Fdn Movere Init ", "Complete ");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        foundationMover.moveToStore();
        arm.moveToCarryStone();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        //========================================
        // GAME PAD 1
        //========================================
        // left joystick is assigned to drive speed


        // foundation moving servo assignment to drivers gamepad
        if (gamepad1.a) {
            foundationMover.moveToStore();
        }

        if (gamepad1.b) {
            //foundationMover.moveToGrab();
        }


        //========================================
        // GAME PAD 2
        //========================================

        // gripper assignment to X and Y buttons on implement gamepad
        /*
        if (gamepad2.x) {
            gripper.moveToClose();
        }

        if (gamepad2.y) {
            gripper.moveToOpen();
        }

        if (gamepad2.a) {
            arm.moveToPickupStone();
        }

        if (gamepad2.b) {
            arm.moveToCarryStone();
        }
        */

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }


}