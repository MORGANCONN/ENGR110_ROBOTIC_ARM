//Might be irrelevent now

/** important values

positions at 1500, 1500

x/y of pen = 330, 278

x/y of left joint = 146, 315

x/y of right joint = 497, 344

x/y of left motor 257, 476

x/y if right motor 370, 477

motor angles change = 0.1 degree per unit of motor movement
1 degree = 10 units

**/

//Actually relevent
/*
import ecs100.*;
import java.util.*;

public class Arm {

    private double xt = 330; //x of pen
    private double yt = 278; //y pf pen

    private int motor1 = 1500;
    private int motor2 = 1500;
    
    double xm1 = 257; //x of motor 1
    double ym1 = 476; //y of motor 1

    double xm2 = 370; //x of motor 2
    double ym2 = 477; //x of motor 2

    int R = 188; //Length of arm sections;

    private double theta1 = 125; //Angle of motor 1
    private double theta2 = 45; //Angle of motor 2

    private double rightd = Math.sqrt(Math.pow(xt - xm1, 2) + Math.pow(yt - ym1, 2)); //Distance between motor and pen
    private double leftd = Math.sqrt(Math.pow(xt - xm2, 2) + Math.pow(yt - ym2, 2));
    
    private double rightxA = (xt + xm1)/2; //x point of middle of circles
    private double rightyA = (yt + ym1)/2; //y point of middle of circles
    
    private double leftxA = (xt + xm2)/2; //x point of middle of circles
    private double leftyA = (yt + ym2)/2; //y point of middle of circles

    private double righth = Math.sqrt(Math.pow(R, 2) - Math.pow(rightd/2, 2)); //Distance between A and intersection of circles
    private double lefth = Math.sqrt(Math.pow(R, 2) - Math.pow(leftd/2, 2));
    
    private double rightcosa = (xm1 - xt)/rightd; //cos of angle a for right motor
    private double rightsina = (ym1 - yt)/rightd; //sin of angle a for right motor 
    
    private double leftcosa = (xm2 - xt)/leftd; //cos of angle a for left motor
    private double leftsina = (ym2 - yt)/leftd; //sin of angle a for left motor

    private double x3 = leftxA + lefth*leftsina; //left x joint position
    private double y3 = leftyA - lefth*leftcosa; //left y joint position

    private double x4 = rightxA - righth*rightsina; //right x joint position
    private double y4 = rightyA + righth*rightcosa; //right y joint position

    private double rightangle = (180/Math.PI)*Math.atan2(rightcosa,rightsina);
    private double leftangle = (180/Math.PI)*Math.atan2(leftcosa,leftsina);
    
    private double rightslope = (motor1 + (rightangle - theta1)*((motor2 - motor1)/(theta2 - theta1)));
    private double leftslope = (motor1 + (leftangle - theta1)*((motor2 - motor1)/(theta2 - theta1)));
    
    public Arm(){
        UI.println(rightslope + " " + leftslope);
        xt = 230;
        yt =  378;
        theta1 = theta1 - rightslope;
        theta2 = theta2 - leftslope; 
        UI.println(rightslope + " " + leftslope);
    }

    public void process(){
    }

    public void output(){

    }

    
    
    
}
*/