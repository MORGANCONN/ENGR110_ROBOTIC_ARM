import ecs100.*;
import java.util.*;
import java.io.*;

public class Arm {
    int R = 200; //Length of arm sections;

    double xm1 = 218; //x of motor 1
    double ym1 = 467; //y of motor 1

    double xm2 = 397; //x of motor 2
    double ym2 = 467; //x of motor 2

    private double xm;
    private double ym;

    private double motor1 = 1500;
    private double motor2 = 1500;

    private double theta1 = 116; //Angle of motor 1
    private double theta2 = 46; //Angle of motor 2

    Arm(){}

    public void run(ArrayList<Line> points){
     ArrayList<String> values = new ArrayList<String>();
     for (int i = 0; i < points.size(); i++){
     double firstangle = process(true, points.get(i).getStartX(), points.get(i).getStartY());
     double secondangle = process(false, points.get(i).getStartX(), points.get(i).getStartY());
     double thirdangle = process(false, points.get(i).getEndX(), points.get(i).getEndY());
     double fourthangle = process(true, points.get(i).getEndX(), points.get(i).getEndY());

     values.add(firstangle + "," + secondangle+ "," + "1400," + thirdangle + "," + fourthangle + "," + "1500");
     }
     output(values);
     }

    double process(boolean motorValue, double xt, double yt){
        if (motorValue == true){
            xm = xm2;
            ym = ym2;
        }
        else {
            xm = xm1;
            ym = ym1;
        }

        double d = Math.sqrt(Math.pow(xt - xm, 2) + Math.pow(yt - ym, 2)); //Distance between motor and pen

        double xA = (xt + xm)/2; //x point of middle of circles
        double yA = (yt + ym)/2; //y point of middle of circles

        double h = Math.sqrt(Math.pow(R, 2) - Math.pow(d/2, 2)); //Distance between A and intersection of circles

        double a = Math.acos((xm - xt)/d); //cos of angle a for right motor

        double x3 = xA + h*Math.sin(a); //x joint position
        double y3 = yA - h*Math.cos(a); //y joint position

        double x4 = xA - h*Math.sin(a); //x joint position
        double y4 = yA + h*Math.cos(a); //y joint position

        double angle1 = Math.PI/2 - Math.atan2((yt - y3), (x3 - xt));
        double angle2 = Math.PI/2 - Math.atan2((yt - y4), (x4 - xt));

        UI.println(angle1 + " " + angle2);

        angle1 = Math.toDegrees(angle1) - 105;
        angle2 = Math.toDegrees(angle2) - 105;

        UI.println(angle1 + " " + angle2);

        double rightslope = (motor1 + (angle2 - theta1)*((motor2 - motor1)/(theta2 - theta1)));
        double leftslope = (motor1 + (angle2 - theta1)*((motor2 - motor1)/(theta2 - theta1)));

        motor1 = rightslope;
        motor2 = leftslope;
        theta1 = angle1;
        theta2 = angle2;

        if (motorValue){
            return (Math.max(angle1, angle2));
        }
        else {
            return (Math.min(angle1, angle2));
        }
    }

     public void output(ArrayList Values) {
         try {
             PrintStream out = new PrintStream(new File("file.txt"));
             for (int i = 0; i < Values.size(); i++) {
                 out.println(i);
             }
         } catch (IOException e) {
         }
     }
}