package org.firstinspires.ftc.teamcode.Subsystems;



import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Arm {

    // Define hardware objects
    public DcMotor armLeft = null;
    public DcMotor armRight = null;
    public Telemetry telemetry = null;


    private static final int ARM_STONE_LOAD = 50; // Left side reference
    private static final int ARM_STONE_CARRY = 200;// Left side reference
    private static final double ARM_SPEED = .5;
    private static final double ARM_RESET_POWER = .5;

    private ElapsedTime runtime = new ElapsedTime();

    // Contructor for Arm
    public Arm() {

    }

    public void init(HardwareMap hwMap) {


        //hwMap = ahwMap;
        armLeft = hwMap.get(DcMotor.class, "Arm");
        armRight = hwMap.get(DcMotor.class, "Arm_2");


        armLeft.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if needed
        armRight.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if needed

        armLeft.setPower(0);
        armRight.setPower(0);

        // Set all motors to run with encoders.

        armLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void moveToPickupStone() {
        armLeft.setTargetPosition(ARM_STONE_LOAD);
        armRight.setTargetPosition(ARM_STONE_LOAD);
        armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armLeft.setPower(Math.abs(ARM_SPEED));
        armRight.setPower(Math.abs(ARM_SPEED));


    }

    public void moveToCarryStone() {
        armLeft.setTargetPosition(ARM_STONE_CARRY);
        armRight.setTargetPosition(ARM_STONE_CARRY);
        armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armLeft.setPower(Math.abs(ARM_SPEED));
        armRight.setPower(Math.abs(ARM_SPEED));


    }

    public void resetArmPosn(Telemetry telemetry) {
        this.telemetry = telemetry;
        runtime.reset();

        armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armLeft.setPower(-.3);
        armRight.setPower(-.3);
        while (runtime.seconds() < 3.0) {
            telemetry.addData("Arm Resetting", "Leg 1: %2.5f S Elapsed", runtime.seconds());

        }

        armLeft.setPower(0);
        armRight.setPower(0);
        armLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void moveByJoystick(double joystick_Y){
        armLeft.setPower(joystick_Y);
        armRight.setPower(joystick_Y);
    };


}