import ecs100.*;
import java.util.*;
import java.awt.Color;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageProcessor{
    private int[][] image;
    private int[][] edgeDirection;
    private boolean[][] lineComplete;

    private ArrayList<Integer> lineStart;
    private ArrayList<Integer> lineEnd;

    private double colourMultiplier = 1;
    private int    colourDepth      = 255;

    private int threshold1 = 170;
    private int threshold2 = 150;

    private final int pixelSize = 1;  // the size of the pixels as drawn on screen


    public ImageProcessor(){
        this.setupGui();
    }

    public void setupGui(){
        UI.initialise();
        UI.addButton("Load image",     this::loadImage );
        UI.addButton("Save to ppm",    this::saveAsPPM);
        UI.addButton("Load ppm",       this::loadFromPPM);
        UI.addButton("Edge detection", this::edgeDetection);
        UI.addButton("Save line coords", this::saveCoords);
        UI.addButton("Quit", UI::quit );
    }

    // Display the image on the screen with each pixel as a square of size pixelSize.
    public void displayImage(){
        if(image == null) return;
        UI.clearGraphics();
        UI.setImmediateRepaint(false);
        for(int row=0; row<this.image.length; row++){
            int y = row * this.pixelSize;
            for(int col=0; col<this.image[0].length; col++){
                int x = col * this.pixelSize;
                UI.setColor(new Color((int)(colourMultiplier * this.image[row][col]), (int)(colourMultiplier * this.image[row][col]), (int)(colourMultiplier * this.image[row][col])));
                UI.fillRect(x, y, this.pixelSize, this.pixelSize);
            }
        }
        UI.repaintGraphics();
    }

    // Asks for image, then displays it
    public void loadImage(){
        colourDepth = 255;
        colourMultiplier = 1;
        this.image = this.loadAnImage(UIFileChooser.open());
        this.displayImage();
    }


    // Load an image and convert it to greyscale
    public int[][] loadAnImage(String imageName) {
        int[][] ans = null;
        if (imageName==null) return null;
        try {
            BufferedImage img = ImageIO.read(new File(imageName));
            UI.printMessage("loaded image height(rows)= " + img.getHeight() + "  width(cols)= " + img.getWidth());
            ans = new int[img.getHeight()][img.getWidth()];
            for (int row = 0; row < img.getHeight(); row++){
                for (int col = 0; col < img.getWidth(); col++){
                    Color c = new Color(img.getRGB(col, row), true);
                    ans[row][col] = (int)Math.round((0.3 * c.getRed()) + (0.59 * c.getGreen()) + (0.11 * c.getBlue()));
                }
            }
        } catch(IOException e){UI.println("Image reading failed: "+e);}
        return ans;
    }

    // Saves image as greyScale PPM file
    public void saveAsPPM(){
        if(image == null) return;
        try{
            PrintWriter out = new PrintWriter(UI.askString("File name")+".ppm");
            out.println("P2");
            out.println(image[0].length);
            out.println(image.length);
            out.println(colourDepth);
            for(int i = 0; i<image.length; i++){
                for(int n = 0; n<image[0].length; n++){
                    out.print(image[i][n] + " ");
                }
                out.print("\n");
            }
            out.flush();
            out.close();
            UI.println("done");
        }
        catch(IOException e){}
    }

    // Loads image from greyscale PPM//PGMT file
    public void loadFromPPM(){
        try{
            Scanner scan = new Scanner(new File(UIFileChooser.open()));
            String type = scan.next();
            if(!type.equals("P2")){
                UI.println("File is not greyscale ppm/pgm");
                return;
            }
            int width = scan.nextInt();
            int height = scan.nextInt();
            int colourDepth = scan.nextInt();
            colourMultiplier = 255/colourDepth;
            image = new int[height][width];
            for(int row = 0; row<image.length; row++){
                for(int col = 0; col<image[0].length; col++){
                    image[row][col] = scan.nextInt();
                }
            }
            scan.close();
            displayImage();
        }
        catch(IOException e){}
    }

    public void saveCoords(){
        if(image == null){return;}
        try {
            PrintStream outStart = new PrintStream(new File("lineStart.txt"));
            PrintStream outEnd = new PrintStream(new File("lineEnd.txt"));
            for (int i = 0; i < lineStart.size() - 1; i += 2) {
                outStart.println(lineStart.get(i));
                outStart.println(lineStart.get(i + 1));
                outEnd.println(lineEnd.get(i));
                outEnd.println(lineEnd.get(i + 1));
            }
            outStart.flush();
            outEnd.flush();
            outStart.close();
            outEnd.close();
            UI.println("Done saving");
        }
        catch(IOException e){}
    }

    public void edgeDetection(){
        if(image == null){return;}
        threshold1 = UI.askInt("Threshold 1: ");
        threshold2 = UI.askInt("Threshold 2: ");
        gausBlur();
        sobelKernal();
        nonMaxSuppress();
        doubleThreshold();
        displayImage();
        UI.println("done");
    }

    // Applies a gaussian blur to the image
    public void gausBlur(){
        int[][] gaussianMask = new int[5][5];
        gaussianMask[0][0] = 2;	 gaussianMask[0][1] = 4;  gaussianMask[0][2] = 5;  gaussianMask[0][3] = 4;  gaussianMask[0][4] = 2;
        gaussianMask[1][0] = 4;	 gaussianMask[1][1] = 9;  gaussianMask[1][2] = 12; gaussianMask[1][3] = 9;  gaussianMask[1][4] = 4;
        gaussianMask[2][0] = 5;	 gaussianMask[2][1] = 12; gaussianMask[2][2] = 15; gaussianMask[2][3] = 12; gaussianMask[2][4] = 2;
        gaussianMask[3][0] = 4;	 gaussianMask[3][1] = 9;  gaussianMask[3][2] = 12; gaussianMask[3][3] = 9;  gaussianMask[3][4] = 4;
        gaussianMask[4][0] = 2;	 gaussianMask[4][1] = 4;  gaussianMask[4][2] = 5;  gaussianMask[4][3] = 4;  gaussianMask[4][4] = 2;

        for(int row = 2; row < image.length-2; row++){
            for(int col = 2; col < image[row].length-2; col++){
                int value = 0;
                for(int rowOffSet = -2; rowOffSet<3; rowOffSet++){
                    for(int colOffSet = -2; colOffSet<3; colOffSet++){
                        value += image[row+rowOffSet][col+rowOffSet] * gaussianMask[rowOffSet+2][colOffSet+2];
                    }
                }
                image[row][col] = value/159;
            }
        }
    }

    // Detects edges
    public void sobelKernal(){
        double max = 0;
        int[][] imageEdges = new int[image.length][image[0].length];
        edgeDirection      = new int[image.length][image[0].length];

        int[][] sobalMaskVertical = new int[3][3];
        sobalMaskVertical[0][0] = -1;  sobalMaskVertical[0][1] = 0;  sobalMaskVertical[0][2] = 1;
        sobalMaskVertical[1][0] = -2;  sobalMaskVertical[1][1] = 0;  sobalMaskVertical[1][2] = 2;
        sobalMaskVertical[2][0] = -1;  sobalMaskVertical[2][1] = 0;  sobalMaskVertical[2][2] = 1;

        int[][] sobalMaskHorizontal = new int[3][3];
        sobalMaskHorizontal[0][0] =  1;  sobalMaskHorizontal[0][1] = 2;  sobalMaskHorizontal[0][2] = 1;
        sobalMaskHorizontal[1][0] =  0;  sobalMaskHorizontal[1][1] = 0;  sobalMaskHorizontal[1][2] = 0;
        sobalMaskHorizontal[2][0] = -1;  sobalMaskHorizontal[2][1] = -2;  sobalMaskHorizontal[2][2] = -1;

        for(int row = 1; row < image.length-1; row++){
            for(int col = 1; col < image[row].length-1; col++){
                int vert = 0;
                int horz = 0;
                for(int rowOffSet = -1; rowOffSet < 2; rowOffSet++){
                    for(int colOffSet = -1; colOffSet < 2; colOffSet++){
                        vert += image[row+rowOffSet][col+colOffSet] * sobalMaskVertical[rowOffSet+1][colOffSet+1];
                        horz += image[row+rowOffSet][col+colOffSet] * sobalMaskHorizontal[rowOffSet+1][colOffSet+1];
                    }
                }
                //combine values
                int value = (int)(Math.sqrt((Math.pow(vert,2) + (Math.pow(horz,2)))));
                imageEdges[row][col] = value;

                //find the maximum colour value of the image
                if(value > max){
                    max = value;
                }

                //find the angle of the line
                double angle = Math.abs((Math.atan2(vert,horz)/3.14159) * 180.0);
                //make approximate
                if(angle<22.5 || angle>157.5){
                    angle = 0;
                }
                else if(angle<67.5){
                    angle = 45;
                }
                else if(angle<112.5){
                    angle = 90;
                }
                else{
                    angle = 135;
                }
//                System.out.println((int)angle);
                edgeDirection[row][col] = (int)angle;		// Store the approximate edge direction of each pixel in one array
            }
        }
        //scale every pixel's brightness by the maximum brightness
        double scale = 255/max;
        for(int row = 0; row < imageEdges.length; row++){
            for(int col = 0; col < imageEdges[row].length; col++){
                imageEdges[row][col] = (int)(imageEdges[row][col]*scale);
            }
        }
        image = imageEdges;
    }

    // Suppresses the dim pixels which are next to an edge, makes the edge 1 pixel wide
    public void nonMaxSuppress(){
        int[][] imageEdges = new int[image.length][image[0].length];
        for(int row = 1; row < image.length-1; row++){
            for(int col = 1; col < image[0].length-1; col++){
                if(edgeDirection[row][col] == 0){
                    for(int i = 0; i < 2; i++){
                        if (image[row+1][col]<image[row][col] && image[row-1][col]<image[row][col]){
                            imageEdges[row][col] = image[row][col];
                        }
                    }
                }
                else if(edgeDirection[row][col] == 45){
                    for(int i = 0; i < 2; i++){
                        if (image[row+1][col+1]<image[row][col] && image[row-1][col-1]<image[row][col]){
                            imageEdges[row][col] = image[row][col];
                        }
                    }
                }
                else if(edgeDirection[row][col] == 90){
                    for(int i = 0; i < 2; i++){
                        if (image[row][col+1]<image[row][col] && image[row][col-1]<image[row][col]){
                            imageEdges[row][col] = image[row][col];
                        }
                    }
                }
                else if(edgeDirection[row][col] == 135){
                    for(int i = 0; i < 2; i++){
                        if (image[row-1][col+1]<image[row][col] && image[row+1][col-1]<image[row][col]){
                            imageEdges[row][col] = image[row][col];
                        }
                    }
                }
            }
        }
        image = imageEdges;
    }

    public void doubleThreshold(){
        lineStart = new ArrayList<Integer>();
        lineEnd   = new ArrayList<Integer>();
        lineComplete = new boolean[image.length][image[0].length];

        for(int row = 0; row < image.length; row++) {
            for (int col = 0; col < image[0].length; col++) {
                if(image[row][col] > threshold1){
                    image[row][col] = 255;
                }
                else if(image[row][col] > threshold2){
                    image[row][col] = 127;
                }
                else{
                    image[row][col] = 0;
                }
            }
        }
        for(int row = 1; row < image.length-1; row++){
            for(int col = 1; col < image[0].length-1; col++){
                if (image[row][col] == 255 && !lineComplete[row][col]){
                    int[] shift = new int[2];
                    int[] location = new int[2];
                    int[] tempStart = new int[2];
                    int[] tempEnd = new int[2];
                    location[0] = row; location[1] = col;
                    if(edgeDirection[row][col] == 0){
                        shift[0] = 1; shift[1] = 0;
                        tempStart = traceEdge(shift, location);
                        shift[0] = -1; shift[1] = 0;
                        tempEnd = traceEdge(shift, location);
                    }
                    else if(edgeDirection[row][col] == 45){
                        shift[0] = 1; shift[1] = 1;
                        tempStart = traceEdge(shift, location);
                        shift[0] = -1; shift[1] = -1;
                        tempEnd = traceEdge(shift, location);
                    }
                    else if(edgeDirection[row][col] == 90){
                        shift[0] = 0; shift[1] = 1;
                        tempStart = traceEdge(shift, location);
                        shift[0] = 0; shift[1] = -1;
                        tempEnd = traceEdge(shift, location);
                    }
                    else if(edgeDirection[row][col] == 135){
                        shift[0] = 1; shift[1] = -1;
                        tempStart = traceEdge(shift, location);
                        shift[0] = -1; shift[1] = 1;
                        tempEnd = traceEdge(shift, location);
                    }
                    lineStart.add(tempStart[0]); lineStart.add(tempStart[1]);
                    lineEnd.add(tempEnd[0]);     lineEnd.add(tempEnd[1]);
                    lineComplete[tempStart[0]][tempStart[1]] = true;
                    System.out.println(tempStart[0] + " " + tempStart[1]);
                    System.out.println(tempEnd[0] + " " + tempEnd[1]);
                    while(tempStart != tempEnd){
                        tempStart[0] += shift[0]; tempStart[1] += shift[1];
                        lineComplete[tempStart[0]][tempStart[1]] = true;
                    }
                }
            }
        }
        for(int row = 0; row < image.length; row++) {
            for (int col = 0; col < image[0].length; col++) {
                if(image[row][col] == 127) {
                    image[row][col] = 0;
                }
            }
        }
    }

    public int[] traceEdge(int[] shift, int[] location){
        location[0] += shift[0]; location[1] += shift[1];
        while(edgeDirection[location[0]][location[1]] == edgeDirection[location[0]-shift[0]][location[1]-shift[1]] && (image[location[0]][location[1]] == 127 || image[location[0]][location[1]] == 127)){
            image[location[0]][location[1]] = 255;
            if(location[0] + shift[0] > image.length || location[0] + shift[0] < 0 || location[1] + shift[1] > image[0].length || location[1] + shift[1] < 0){return location;}
            location[0] += shift[0]; location[1] += shift[1];
        }
        return location;
    }

    // Main
    public static void main(String[] arguments){
        new ImageProcessor();
    }

}
