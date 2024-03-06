/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package programtwo;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Math;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Abraham
 */
public class ProgramTwo {

    public void start() {
        try {
            createWindow();
            initGL();
            createKeyboard();
            while(!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                render();
            }
            Keyboard.destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //method: createKeyboard
    //purpose: creates a Keyboard to be used in the program, so input from the keyboard
    // is recognized
    private void createKeyboard() {
        try {
            org.lwjgl.input.Keyboard.create();
            Keyboard.enableRepeatEvents(true);
        } catch (LWJGLException e) {
            System.out.println("Error from creating keyboard.");
        }
    }
    
    //method: createWindow
    //purpose: creates a window of 640x480 size on the monitor
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("OpenGL");
        Display.create();
    }
    
    //method: initGL
    //purpose: to initialize OpenGL settings, like setting the window background color
    // and matrix mode
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        glOrtho(0, 640, 0, 480, 1, -1);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: render
    //purpose: to set up the render settings for shapes to be drawn on display
    private void render() {
        while (!Display.isCloseRequested()) {
            try {  
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                drawPolygon(); 
                
                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }
    
    //method: drawFigure
    //purpose: opens coordinates file and, depending on letter, draws an appropiate shape
    private void drawPolygon() {
        try {     
            ArrayList<String>coordinateGroup = createCoordinatePointHolder();
            ArrayList<String>translationGroup = createTranslationPointHolder();
            ArrayList<String>rotationGroup = createRotationInfoHolder();
            ArrayList<String>scalingGroup = createScalingInfoHolder();
            
            File inputFile = new File ("coordinates.txt");
            Scanner reader = new Scanner(inputFile);
            
            ArrayList<String> rotationInfo = new ArrayList();
            ArrayList<String> scalingInfo = new ArrayList();
            ArrayList<String> translationInfo = new ArrayList();
            ArrayList<String> coordinateInfo = new ArrayList();
            
            while (reader.hasNextLine()) {            
                String point = reader.nextLine();
                if(point.contains("P")) {
                    
                    setPolygonColor(point);
                } else if(point.contains("T")) {
                    
                    ArrayList<String> allEdges = createAllEdgesTable(coordinateInfo); 
                    createGlobalEdgeTable(allEdges);
                    
                    coordinateInfo.clear();
                    
                } else if(point.contains("r")) {
                    
                    String newRotationPoint = point.substring(2);
                    rotationInfo = setRotationInfo(newRotationPoint, rotationGroup);
                } else if(point.contains("s")) {
                    
                    String newScalingPoint = point.substring(2);
                    scalingInfo = setScalingInfo(newScalingPoint, scalingGroup);
                } else if(point.contains("t")) {
                    
                    String newTranslationPoint = point.substring(2);
                    translationInfo = setTranslationInfo(newTranslationPoint, translationGroup);
                } else {
                    coordinateInfo = setPolygonCoordinates(point, coordinateGroup);
                }  
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
        
    private ArrayList<String> createCoordinatePointHolder() {
        ArrayList<String> coordinatePoints = new ArrayList();
        return coordinatePoints;
    }
    
    private ArrayList<String> createTranslationPointHolder() {
        ArrayList<String> translationInfo = new ArrayList();
        return translationInfo;
    }
    
    private ArrayList<String> createRotationInfoHolder() {
        ArrayList<String> rotationInfo = new ArrayList();
        return rotationInfo;
    }
    
    private ArrayList<String> createScalingInfoHolder() {
        ArrayList<String> scalingInfo = new ArrayList();
        return scalingInfo;
    }
    
    private void setPolygonColor(String currentValue){
        float redValue, greenValue, blueValue;
        String line = currentValue.substring(2);
        String[] splitRGBValues = line.split("[ ]");
            
        redValue = Float.parseFloat(splitRGBValues[0]);
        greenValue = Float.parseFloat(splitRGBValues[1]);
        blueValue = Float.parseFloat(splitRGBValues[2]);
        
        glColor3f(redValue, greenValue, blueValue);
        glPointSize(1);
    }
   
    private ArrayList<String> setRotationInfo(String currentPoint, ArrayList<String> rotationPoints) {
        rotationPoints.add(currentPoint);
        return rotationPoints;
    }
    
    private ArrayList<String> setScalingInfo(String currentPoint, ArrayList<String> scalingPoints) {
        scalingPoints.add(currentPoint);
        return scalingPoints;
    }
    
    private ArrayList<String> setTranslationInfo(String currentPoint, ArrayList<String> translationPoints) {
        translationPoints.add(currentPoint);
        return translationPoints;
    }
    
    private ArrayList<String> setPolygonCoordinates(String currentPoint, ArrayList<String> pointCarrier) {
        
        pointCarrier.add(currentPoint);
        return pointCarrier;
    }
    
    private ArrayList<String> createAllEdgesTable(ArrayList<String> coordinateInfo) {
        ArrayList<String> allEdgesTable = new ArrayList();
        int currentTempX, currentTempY, nextTempX, nextTempY, yZero, yOne, xZero, xOne, xValue;
        double genericTempOne, genericTempTwo, m, oneDivideByM;
        String stringTemp, edgeTableInfo = "";
        
        for(int i = 0; i < coordinateInfo.size(); i++) {
            if(i != coordinateInfo.size() - 1) {
                String[] currentCoordinates = coordinateInfo.get(i).split("[ ]");
                String[] nextCoordinates = coordinateInfo.get(i + 1).split("[ ]");

                currentTempX = Integer.parseInt(currentCoordinates[0]);
                currentTempY = Integer.parseInt(currentCoordinates[1]);

                nextTempX = Integer.parseInt(nextCoordinates[0]);
                nextTempY = Integer.parseInt(nextCoordinates[1]);
            } else {
                String[] currentCoordinates = coordinateInfo.get(i).split("[ ]");
                String[] nextCoordinates = coordinateInfo.get(0).split("[ ]");

                currentTempX = Integer.parseInt(currentCoordinates[0]);
                currentTempY = Integer.parseInt(currentCoordinates[1]);

                nextTempX = Integer.parseInt(nextCoordinates[0]);
                nextTempY = Integer.parseInt(nextCoordinates[1]);
            }
          
            if(currentTempY > nextTempY) {
                yZero = currentTempY;
                xValue = currentTempX;
                yOne = nextTempY;
            } else {
                yZero = nextTempY;
                xValue = nextTempX;
                yOne = currentTempY;
            }   
            
            if(currentTempX > nextTempX) {
                xZero = currentTempX;
                xOne = nextTempX;
            } else {
                xZero = nextTempX;
                xOne = currentTempX;
            }
            
            genericTempOne = yZero - yOne;
            genericTempTwo = xZero - xOne;
            m = genericTempOne / genericTempTwo;
            oneDivideByM = (1 / m);
            
            stringTemp = String.valueOf(yOne);
            edgeTableInfo += " " + stringTemp;
            
            stringTemp = String.valueOf(yZero);
            edgeTableInfo += " " + stringTemp;
            
            stringTemp = String.valueOf(xValue);
            edgeTableInfo += " " + stringTemp;
            
            stringTemp = String.valueOf(oneDivideByM);
            edgeTableInfo += " " + stringTemp;
            
            allEdgesTable.add(edgeTableInfo);
            edgeTableInfo = "";
            stringTemp = "";
        }
        return allEdgesTable;
    }
    
    private ArrayList<String> createGlobalEdgeTable(ArrayList<String> allEdgesTable) {
        ArrayList<String> globalEdgeTable = new ArrayList();
        
        System.out.println("allEdges: " + allEdgesTable);
        for(int i = 0; i < allEdgesTable.size(); i++) {
            String section = allEdgesTable.get(i);
            String[] currentSection = allEdgesTable.get(i).split("[ ]");
            float slope = Float.parseFloat(currentSection[4]);      

            if(slope == Infinity) {
                globalEdgeTable.add(section);
            }
        }
        System.out.println("Global Edge Table: " + globalEdgeTable);
        return globalEdgeTable;
    }
     
    //initializes program start
    public static void main(String[] args) {
        ProgramTwo basicProgram = new ProgramTwo();
        basicProgram.start();
    }
    
}
