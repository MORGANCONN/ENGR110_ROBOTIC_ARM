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

    double motor11 = 1500;
    double motor12 = 1550;

    double motor21 = 1500;
    double motor22 = 1550;

    double theta11 = 116; //Angle of motor 1
    double theta12 = 120;

    double theta21 = 40; //Angle of motor 2
    double theta22 = 46;

    Arm(){}

    public void run(ArrayList<Line> points){
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < points.size(); i++){
            
                double firstangle = process(true, points.get(i).getStartX() + 190, points.get(i).getStartY()+200);
                double secondangle = process(false, points.get(i).getStartX()+ 190, points.get(i).getStartY()+ 200);
                double thirdangle = process(true, points.get(i).getEndX()+ 190, points.get(i).getEndY()+ 200);
                double fourthangle = process(false, points.get(i).getEndX()+ 190, points.get(i).getEndY()+ 200);   
                
                double slope = (motor12 - motor11) / (theta12 - theta11);
                int pwm1 = (int) (motor11 + slope*(firstangle - theta11));

                slope = (motor12 - motor11) / (theta22 - theta21);
                int pwm2 = (int) (motor11 + slope*(secondangle - theta21)); 
                
                values.add(pwm1 + "," + pwm2 + "," + "1500");

                slope = (motor12 - motor11) / (theta12 - theta11);
                int pwm3 = (int) (motor11 + slope*(thirdangle - theta11)); 

                slope = (motor12 - motor11) / (theta22 - theta21);
                int pwm4 = (int) (motor11 + slope*(fourthangle - theta21)); 

                values.add(pwm3 + "," + pwm4 + "," + "1200");
            }
        
    output(values);
    }

    double process(boolean motorValue, double xt, double yt){

        if (motorValue == true){
            xm = xm1;
            ym = ym1;
        }
        else {
            xm = xm2;
            ym = ym2;
        }

        double d = Math.sqrt(Math.pow(xt - xm, 2) + Math.pow(yt - ym, 2)); //Distance between motor and pen

        double xA = (xt + xm)/2; //x point of middle of circles
        double yA = (yt + ym)/2; //y point of middle of circles

        double h = Math.sqrt(Math.pow(R, 2) - Math.pow(d/2, 2)); //Distance between A and intersection of circles

        double a = Math.acos((xm - xt)/d); //cos of angle a for right motor
 

        double x3 = xA + h*Math.sin(a); //x joint position
        double y3 = yA - h*Math.cos(a); //y joint position

         
        UI.println (yt + " " + xt);
        
        double x4 = xA - h*Math.sin(a); //x joint position
        double y4 = yA + h*Math.cos(a); //y joint position

        double angle1 = Math.PI/2 - Math.atan2((yt - y3), (x3 - xt));
        double angle2 = Math.PI/2 - Math.atan2((yt - y4), (x4 - xt));

        angle1 = Math.toDegrees(angle1) - 105;
        angle2 = Math.toDegrees(angle2) - 105;
        
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
                out.println(Values.get(i));
            }
        } catch (IOException e) {
        }
    }
}