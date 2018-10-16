import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class ImageProcessing {
    String fileName;
    Scanner sc;
    String magicNumber;
    int colNum, rowNum, intensity;
    Integer[][] verticleEdgeArray, horizontalEdgeArray;


    public ImageProcessing(String file) {
        fileName = file;
        processFile();
        outputEdgePictures();
    }

    public void processFile() {
        try {
            sc = new Scanner(new File(fileName));
            magicNumber = sc.next();
            colNum = sc.nextInt() / 3;
            rowNum = sc.nextInt() / 3;
            intensity = sc.nextInt();
            Integer[][] imageArray = new Integer[rowNum][colNum];
            verticleEdgeArray = new Integer[rowNum - 2][colNum - 2];
            horizontalEdgeArray = new Integer[rowNum - 2][colNum - 2];
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int R = sc.nextInt();
                    int G = sc.nextInt();
                    int B = sc.nextInt();
                    int greyscale = (int) (R * 0.3 + G * 0.6 + B * 0.11);
                    imageArray[row][col] = greyscale;
                }
            }
            for (int nRow = 1; nRow < rowNum - 2; nRow++) {
                for (int nCol = 1; nCol < colNum - 2; nCol++) {
                    if (!isBoundary(imageArray, nRow, nCol)) {
                        System.out.println("Row: " + nRow + " Col: " + nCol);
                        verticleEdgeArray[nRow-1][nCol-1] = verticleEdge(imageArray, nRow, nCol);
                        horizontalEdgeArray[nRow-1][nCol-1] = horizontalEdge(imageArray, nRow, nCol);
                    }
                }
            }
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("File Not Found");
        }


    }

    public boolean isBoundary(Integer[][] input, int row, int col) {
        if (input[row + 1][col + 1] != null && input[row - 1][col - 1] != null && input[row + 1][col - 1] != null && input[row - 1][col + 1] != null && input[row - 1][col - 1] != null
                && input[row][col + 1] != null && input[row + 1][col] != null && input[row][col - 1] != null && input[row - 1][col] != null) {
            return false;
        }
        return true;
    }

    public int verticleEdge(Integer[][] input, int row, int col) {
        int vEdgeValue = -1 * input[row - 1][col - 1] + 0 * input[row - 1][col] + 1 * input[row - 1][col + 1] +
                -2 * input[row][col - 1] + 0 * input[row][col] + 2 * input[row][col + 1] +
                -1 * input[row + 1][col - 1] + 0 * input[row + 1][col] + 1 * input[row + 1][col + 1];
        if (vEdgeValue > 0) {
            return 0;
        } else if (vEdgeValue <= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public int horizontalEdge(Integer[][] input, int row, int col) {
        int hEdgeValue =
                1 * input[row - 1][col - 1] + 2 * input[row - 1][col] + 1 * input[row - 1][col + 1] +
                        0 * input[row][col - 1] + 0 * input[row][col] + 0 * input[row][col + 1] +
                        -1 * input[row + 1][col - 1] + 2 * input[row + 1][col] + 1 * input[row + 1][col + 1];
        if (hEdgeValue > 0) {
            return 0;
        } else if (hEdgeValue <= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public void outputEdgePictures() {
        try {
            PrintStream horizontalPictureOutput = new PrintStream(new File("HorizEdge.pbm"));
            PrintStream verticlePictureOutput = new PrintStream(new File("VertEdge.pbm"));
            if (horizontalEdgeArray != null) {
                horizontalPictureOutput.println("P1");
                horizontalPictureOutput.print(horizontalEdgeArray[0].length+" ");
                horizontalPictureOutput.print(horizontalEdgeArray.length+" \n");
                for (int row = 0; row < horizontalEdgeArray.length; row++) {
                    for (int col = 0; col < horizontalEdgeArray[0].length; col++) {
                    horizontalPictureOutput.print(horizontalEdgeArray[row][col]+" ");
                    }
                    horizontalPictureOutput.print("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("An Error Has Occured With The Picture Output");
        }
    }

    public static void main(String[] args) {
        new ImageProcessing("1-image-bee.ppm");
    }
}
