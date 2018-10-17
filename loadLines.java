import ecs100.*;
import java.util.*;
import java.io.*;

public class loadLines{

    private ArrayList<Integer> lineStart;
    private ArrayList<Integer> lineEnd;

    public loadLines(){
        setupGUI();
    }

    public void setupGUI(){
        UI.initialise();
        UI.addButton("Load start",     this::loadStart );
        UI.addButton("load end",       this::loadEnd);
        UI.addButton("display",        this::display);
        UI.addButton("Quit", UI::quit );
    }

    public void loadStart(){
        try{
            lineStart = new ArrayList<>();
            Scanner scan = new Scanner(new File(UIFileChooser.open()));
            while(scan.hasNext()){
                lineStart.add(scan.nextInt());
            }
            UI.println("done");
        }
        catch(IOException e){}
    }

    public void loadEnd(){
        try{
            lineEnd = new ArrayList<>();
            Scanner scan = new Scanner(new File(UIFileChooser.open()));
            while(scan.hasNext()){
                lineEnd.add(scan.nextInt());
            }
            UI.println("done");
        }
        catch(IOException e){}
    }

    public void display(){
        for(int i = 0; i < lineEnd.size()-1; i += 2){
            UI.drawLine(lineStart.get(i), lineStart.get(i+1), lineEnd.get(i), lineEnd.get(i+1));
        }
        UI.println("done");
    }

    // Main
    public static void main(String[] arguments){
        new loadLines();
    }
}