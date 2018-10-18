public class Line {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    Line(int x1, int y1, int x2, int y2){
        startX = x1;
        startY = y1;
        endX   = x2;
        endY   = y2;

    }
    public int getStartX(){
        return startX;
    }
    public int getStartY(){
        return startY;
    }
    public int getEndX(){
        return endX;
    }
    public int getEndY(){
        return endY;
    }
}
