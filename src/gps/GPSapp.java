package gps;

import java.io.File;
import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import node.AbsNode;
import node.Place;
import ui.Node;
import ui.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javafx.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import io.JsonParser;


public class GPSapp extends Application{
	public static void main(String[] args) {
        launch(args);
    }
	
	//Load up the JSON data and create the nodes for the map
	JsonParser json = new JsonParser();
	LinkedList<AbsNode> nodeList = json.getJsonContent("Graphs/AK1.json");
	boolean start, end = false;
	String startNode, endNode;
    @Override
    public void start(Stage primaryStage) {
    	
    	final Pane root = new Pane(); 
          
    	
    	
   
    	
    	
    	//Create a map selection drop down menu
    	final VBox mapSelectionBoxV = new VBox(5);
    	final Label mapSelectorLabel = new Label("Choose map");
    	mapSelectorLabel.setTextFill(Color.WHITE);
    	final HBox mapSelectionBoxH = new HBox(5);
    	ObservableList<String> mapOptions = FXCollections.observableArrayList("AK0", "AK1", "AK2");
    	final ComboBox<String> mapSelector = new ComboBox<String>(mapOptions);
    	final Button LoadMapButton = new Button("Load Map");
    	mapSelectionBoxH.getChildren().addAll(mapSelector, LoadMapButton);
    	mapSelectionBoxV.setLayoutX(820);
    	mapSelectionBoxV.setLayoutY(20);
    	mapSelectionBoxV.getChildren().addAll(mapSelectorLabel, mapSelectionBoxH);
    	
    	
    	//Create a label and box for warnings, ie when the coordinates are outside the name
    	final HBox warningBox = new HBox(0); 
    	final Label warningLabel = new Label("");
    	warningLabel.setTextFill(Color.WHITE);
    	warningBox.setLayoutX(10);
    	warningBox.setLayoutY(680);
    	warningBox.getChildren().addAll(warningLabel); 
        
      //Create the START selection drop down menu
        final Button findRouteButton = new Button("Find Route");
    	final VBox LocationSelectionBoxV = new VBox(5);
    	final Label LocationSelectorLabelSTART = new Label("Start");
    	LocationSelectorLabelSTART.setTextFill(Color.WHITE);
    	final Label LocationSelectorLabelDEST = new Label("Destination");
    	LocationSelectorLabelDEST.setTextFill(Color.WHITE);
    	final HBox LocationSelectionBoxHLABEL = new HBox(185);
    	final HBox LocationSelectionBoxH = new HBox(60); 
    	ObservableList<String> LocationOptions = FXCollections.observableArrayList();
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	LocationOptions.add("asdasdasdasdasdasd"); //ADD THESE WHILE READING IN JSON - ADD NAME STRING
    	final ComboBox<String> LocationSelectorSTART = new ComboBox<String>(LocationOptions);
    	final ComboBox<String> LocationSelectorDEST = new ComboBox<String>(LocationOptions);
    	LocationSelectorSTART.setPrefWidth(150);
    	LocationSelectorDEST.setPrefWidth(150);
    	LocationSelectorSTART.setVisibleRowCount(3);
    	LocationSelectorDEST.setVisibleRowCount(3);
    	LocationSelectionBoxHLABEL.getChildren().addAll(LocationSelectorLabelSTART, LocationSelectorLabelDEST);
    	LocationSelectionBoxH.getChildren().addAll(LocationSelectorSTART, LocationSelectorDEST, findRouteButton);
    	LocationSelectionBoxV.setLayoutX(10);
    	LocationSelectionBoxV.setLayoutY(620);
    	LocationSelectionBoxV.getChildren().addAll(LocationSelectionBoxHLABEL, LocationSelectionBoxH);
  
        //Create the map image
        File mapFile = new File("CS3733_Graphics/AK1.png");
        mapSelector.setValue("AK1"); // Default Map when App is opened
        Image mapImage = new Image(mapFile.toURI().toString());
        ImageView imageView = new ImageView();
        imageView.setImage(mapImage);
        imageView.setLayoutX(0);  
        imageView.setLayoutY(0);
        
        //create background
        File backgroundFile = new File("CS3733_Graphics/BlueBackground.jpg");
        Image bgImage = new Image(backgroundFile.toURI().toString());
        ImageView bgView = new ImageView();
        bgView.setImage(bgImage);
        bgView.setLayoutX(0);  
        bgView.setLayoutY(0);
        
        //Add images to the screen
        root.getChildren().add(bgView); //Must add background image first!
        root.getChildren().add(mapSelectionBoxV);
        root.getChildren().add(LocationSelectionBoxV);
        root.getChildren().add(imageView);  
        
        drawPlaces(nodeList, root);
        
        
        //Add actions to the Load Map button
        LoadMapButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
            	root.getChildren().remove(imageView); //remove current map, then load new one
            	nodeList.clear(); 
            	nodeList = json.getJsonContent("Graphs/" + (String) mapSelector.getValue() + ".json");
            	
            	File newMapFile = new File("CS3733_Graphics/" + (String) mapSelector.getValue() + ".png"); //MUST ADD png extension!
            	Image mapImage = new Image(newMapFile.toURI().toString());
                ImageView imageView = new ImageView();
                imageView.setImage(mapImage);
                imageView.setLayoutX(0);  
                imageView.setLayoutY(0);
                imageView.resize(800, 600); //incase map is not already scaled perfectly
                root.getChildren().add(imageView); 
                //add nodes/node buttons to the screen AND POPULATE DROP DOWN MENUS FOR START AND DESTINATION
                //graph.drawEdges?
                drawPlaces(nodeList, root);
            }
        });
        
        //Add button actions
        findRouteButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
            	// Need to string compare from 
            		// LocationSelectorSTART.getValue() and LocationSelectorDEST.getValue() to get actual nodes 
            		// Any better way???
            	// Call findRoute on 2 nodes, returns a LinkedList<AbsNode>
                // LinkedList<AbsNode> route = findRoute();
                // Call Draw Route
                // drawRoute(root, route);
            }
        });
        
  
        primaryStage.setScene(new Scene(root, 1050, 700));  
        primaryStage.show();  
    }  
    
    
    private void drawPlaces(LinkedList<AbsNode> nodes, Pane root){
    	int i;
    	for(i = 0; i < nodes.size() - 1; i ++){ 
    		Button newNodeButton = new Button("");
        	newNodeButton.setStyle(
                    "-fx-background-radius: 5em; " +
                    "-fx-min-width: 15px; " +
                    "-fx-min-height: 15px; " +
                    "-fx-max-width: 15px; " +
                    "-fx-max-height: 15px;"
            );
        	newNodeButton.relocate(nodes.get(i).getX(), nodes.get(i).getY());
        	AbsNode newNode = nodes.get(i);
        	newNodeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                	//LocationSelectorSTART
                	if (!start){
                		if(newNode.getIsPlace()) startNode = ((Place) newNode).getName();
                		start = true;
                	}
                	if(!end){
                		if(newNode.getIsPlace()) endNode = ((Place) newNode).getName();
                		LocationSelectorDEST.setValue(endNode);
                		start = false;
                	}
                }
            });
        	root.getChildren().add(newNodeButton);
	  		

    	}
    }
    
    private void drawRoute(GraphicsContext gc, LinkedList<AbsNode> route) {
        
    	//iterate through the route drawing a connection between nodes
    	for(int i = 0; i < route.size() - 1; i ++){  
	  		gc.strokeLine(route.get(i).getX(), route.get(i).getY(), route.get(i+1).getX(),route.get(i+1).getY());

    	}
    }

    
}
