import ecs100.*;
import java.awt.*;
import java.util.*;

public class cordinateOutput {
    private ArrayList<Double> cordinates = new ArrayList<Double>();

    public cordinateOutput() {
        circleGen();
        for (int i = 0; i < cordinates.size(); i = i + 2) {
            UI.fillRect(cordinates.get(i), cordinates.get(i + 1), 1, 1);
        }
        cordinates.clear();

    }

    public void horizLineGen() {
        double y = 200;
        for (double x = 100; x <= 300; x++) {
            cordinates.add(x);
            cordinates.add(y);
        }
    }

    public void vertLineGen() {
        double x = 200;
        for (double y = 100; y <= 300; y++) {
            cordinates.add(x);
            cordinates.add(y);
        }
    }

    public void circleGen() {
        double centerX = 200;
        double centerY = 200;
        for (float i = 0; i < 360; i++) {
        cordinates.add((centerX + -30 * Math.cos(Math.toRadians(i))));
            cordinates.add((centerY + -30 * Math.sin(Math.toRadians(i))));
        }
    }


    public static void main(String[] args) {
        new cordinateOutput();
    }
}