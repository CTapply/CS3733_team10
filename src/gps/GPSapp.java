package gps;

import TurnByTurn.Step;
import TurnByTurn.stepIndicator;
import io.JsonParser;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import node.*;

import java.io.File;
import java.util.LinkedList;



public class GPSapp extends Application{
	public static void main(String[] args) {
        launch(args);
    }

	//Load up the JSON data and create the nodes for the map
	JsonParser json = new JsonParser();
	LinkedList<Node> nodeList = JsonParser.getJsonContent("Graphs/Nodes/CampusMap.json");
	LinkedList<EdgeDataConversion> edgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/CampusMapEdges.json");

	//LinkedList<Edge> edgeList = convertEdgeData(edgeListConversion);
	Canvas canvas = new Canvas(3000, 2000);
    GraphicsContext gc = canvas.getGraphicsContext2D();
	boolean start, end = false, toggle = true, startBool = false, destBool = false, startButtonBool = false, destButtonBool = false;
	String startNode, endNode;
	Graph graph = new Graph();
	ObservableList<String> LocationOptions = FXCollections.observableArrayList();
	ListView<String> StartList = new ListView<String>();
    ListView<String> DestList = new ListView<String>();
    TextField StartText = new TextField();
	TextField DestText = new TextField();
	int k = 0; // Set Max zoom Variable
	static Parent zoomPane;
	Pane NodePane = new Pane();
	Label BuildingNameLabel = new Label();
	Double buttonRescale = 1/0.75;
	Button startButton = null, endButton = null, currentButton = null;
	VBox directionBox = new VBox(2);
	ScrollPane s1 = new ScrollPane();
	double mouseYposition, mouseXposition;
	
	//Groups to attach layered map
	Group LayerGroup = new Group();

	//TODO PROBABLY CHANGE THIS TO SELECT BUILDING AND THEN SUB DROP DOWN TO SELECT FLOOR
	//THIS IS TOO CLUTTERED. NOT ORGANIZED
	ObservableList<String> mapOptions = FXCollections.observableArrayList("CampusMap", "AKB", "AK1", "AK2", "AK3", "GLSB", "GLB", "GL1", "GL2", "GL3", "BHB", "BH1", "BH2", "BH3", "CC1", "CC2", "CC3", "HHB", "HH1", "HH2", "HH3", "HHAPT", "HHGAR", "PC1", "PC2", "SHB", "SH1", "SH2", "SH3");
	final ComboBox<String> mapSelector = new ComboBox<String>(mapOptions);
	
	//Lists of all nodes of the types
	LinkedList<Node> PointNodes = new LinkedList<Node>();
	LinkedList<Node> StaircaseNodes = new LinkedList<Node>();
	LinkedList<Node> TransitionNodes = new LinkedList<Node>();
	LinkedList<Node> VendingMachineNodes = new LinkedList<Node>();
	LinkedList<Node> WaterFountainNodes = new LinkedList<Node>();
	LinkedList<Node> MensBathroomNodes = new LinkedList<Node>();
	LinkedList<Node> WomensBathroomNodes = new LinkedList<Node>();
	LinkedList<Node> EmergencyPoleNodes = new LinkedList<Node>();
	LinkedList<Node> DiningNodes = new LinkedList<Node>();
	LinkedList<Node> ElevatorNodes = new LinkedList<Node>();
	LinkedList<Node> ComputerLabNodes = new LinkedList<Node>();
	
	//For Rescaling the application
	final Pane root = new Pane();
	final BorderPane Borders = new BorderPane();
    ScrollPane scrollPane = new ScrollPane();
	Scene scene = new Scene(root, 1050,750);
	double scaleXratio = 1;
    double scaleYRatio = 1;
    double stageInitialWidthDifference = 100;
    double stageInitialHeightDifference = 100;
	

	//Building Buildings with their content
	Building Campus = new Building("Campus");
  	Building AtwaterKent = new Building("Atwater Kent"); //need layered maps
  	Building BoyntonHall = new Building("Boynton Hall");
  	Building CampusCenter = new Building("Campus Center");
  	Building GordonLibrary = new Building("Gordon Library");
  	Building HigginsHouse = new Building("Higgins House"); //need layered maps
  	Building HigginsHouseGarage = new Building("Higgins House Garage"); //need layered maps
  	Building ProjectCenter = new Building("Project Center");
  	Building StrattonHall = new Building("Stratton Hall");

  	//Map Buildings with their content
  	Map CampusMap = new Map("Campus Map", "CampusMap", "CS3733_Graphics/CampusMap.png", "Graphs/Nodes/CampusMap.json", "Graphs/Edges/CampusMapEdges.json", 0, 0, 0, 2.6053, "");

	Map AtwaterKentB = new Map("Atwater Kent B", "AK", "CS3733_Graphics/AKB.png", "Graphs/Nodes/AKB.json", "Graphs/Edges/AKBEdges.json", -2.617, 1548, 594, 0.1627, "B");
	Map AtwaterKent1 = new Map("Atwater Kent 1", "AK", "CS3733_Graphics/AK1.png", "Graphs/Nodes/AK1.json", "Graphs/Edges/AK1Edges.json", -2.617, 1548, 594, 0.1312, "1");
	Map AtwaterKent2 = new Map("Atwater Kent 2", "AK", "CS3733_Graphics/AK2.png", "Graphs/Nodes/AK2.json", "Graphs/Edges/AK2Edges.json", -2.617, 1548, 594, 0.1692, "2");
	Map AtwaterKent3 = new Map("Atwater Kent 3", "AK", "CS3733_Graphics/AK3.png", "Graphs/Nodes/AK3.json", "Graphs/Edges/AK3Edges.json", -2.617, 1548, 594, 0.1690, "3");

	Map GordonLibrarySB = new Map("Gordon Library SB", "GL", "CS3733_Graphics/GLSB.png", "Graphs/Nodes/GLSB.json", "Graphs/Edges/GLSBEdges.json", 1.762, 1668, 726, 0.1187, "SB");
	Map GordonLibraryB = new Map("Gordon Library B",  "GL", "CS3733_Graphics/GLB.png", "Graphs/Nodes/GLB.json", "Graphs/Edges/GLBEdges.json", 1.762, 1668, 726, 0.1251, "B");
	Map GordonLibrary1 = new Map("Gordon Library 1",  "GL", "CS3733_Graphics/GL1.png", "Graphs/Nodes/GL1.json", "Graphs/Edges/GL1Edges.json", 1.762, 1668, 726, 0.1194, "1");
	Map GordonLibrary2 = new Map("Gordon Library 2",  "GL", "CS3733_Graphics/GL2.png", "Graphs/Nodes/GL2.json", "Graphs/Edges/GL2Edges.json", 1.762, 1668, 726, 0.1223, "2");
	Map GordonLibrary3 = new Map("Gordon Library 3",  "GL", "CS3733_Graphics/GL3.png", "Graphs/Nodes/GL3.json", "Graphs/Edges/GL3Edges.json", 1.762, 1668, 726, 0.1387, "3");

	Map BoyntonHallB = new Map("Boynton Hall B", "BH","CS3733_Graphics/BHB.png","Graphs/Nodes/BHB.json","Graphs/Edges/BHBEdges.json", 0.157, 1496, 991, 0.0956, "B");
	Map BoyntonHall1 = new Map("Boynton Hall 1", "BH","CS3733_Graphics/BH1.png","Graphs/Nodes/BH1.json","Graphs/Edges/BH1Edges.json", 0.157, 1496, 991, 0.0973, "1");
	Map BoyntonHall2 = new Map("Boynton Hall 2", "BH","CS3733_Graphics/BH2.png","Graphs/Nodes/BH2.json","Graphs/Edges/BH2Edges.json", 0.157, 1496, 991, 0.0981, "2");
	Map BoyntonHall3 = new Map("Boynton Hall 3", "BH","CS3733_Graphics/BH3.png","Graphs/Nodes/BH3.json","Graphs/Edges/BH3Edges.json", 0.157, 1496, 991, 0.1003, "3");

	Map CampusCenter1 = new Map("Campus Center 1", "CC", "CS3733_Graphics/CC1.png", "Graphs/Nodes/CC1.json", "Graphs/Edges/CC1Edges.json", -1.413, 1175, 670, 0.1695, "1");
	Map CampusCenter2 = new Map("Campus Center 2", "CC", "CS3733_Graphics/CC2.png", "Graphs/Nodes/CC2.json", "Graphs/Edges/CC2Edges.json", -1.413, 1175, 670, 0.166, "2");
	Map CampusCenter3 = new Map("Campus Center 3", "CC", "CS3733_Graphics/CC3.png", "Graphs/Nodes/CC3.json", "Graphs/Edges/CC3Edges.json", -1.413, 1175, 670, 0.1689, "3");

	Map HigginsHouseB = new Map("Higgins House B", "HH", "CS3733_Graphics/HHB.png", "Graphs/Nodes/HHB.json", "Graphs/Edges/HHBEdges.json", -2.529, 1161, 504, 0.1314, "B");
	Map HigginsHouse1 = new Map("Higgins House 1", "HH", "CS3733_Graphics/HH1.png", "Graphs/Nodes/HH1.json", "Graphs/Edges/HH1Edges.json", -2.529, 1161, 504, 0.1364, "1");
	Map HigginsHouse2 = new Map("Higgins House 2", "HH", "CS3733_Graphics/HH2.png", "Graphs/Nodes/HH2.json", "Graphs/Edges/HH2Edges.json", -2.529, 1161, 504, 0.1343, "2");
	Map HigginsHouse3 = new Map("Higgins House 3", "HH", "CS3733_Graphics/HH3.png", "Graphs/Nodes/HH3.json", "Graphs/Edges/HH3Edges.json", -2.529, 1161, 504, 0.1317, "3");
	Map HigginsHouseAPT = new Map("Higgins House Apartment", "HH", "CS3733_Graphics/HHAPT.png", "Graphs/Nodes/HHAPT.json", "Graphs/Edges/HHAPTEdges.json", -0.942, 1215, 394, 0.0521, "APT");
	Map HigginsHouseGAR = new Map("Higgins House Garage", "HH", "CS3733_Graphics/HHGAR.png", "Graphs/Nodes/HHGAR.json", "Graphs/Edges/HHGAREdges.json", -0.942, 1215, 394, 0.053, "GAR");

	Map ProjectCenter1 = new Map("Project Center 1", "PC", "CS3733_Graphics/PC1.png", "Graphs/Nodes/PC1.json", "Graphs/Edges/PC1Edges.json", 1.71, 1228, 772, 0.0701, "1");
	Map ProjectCenter2 = new Map("Project Center 2", "PC", "CS3733_Graphics/PC2.png", "Graphs/Nodes/PC2.json", "Graphs/Edges/PC2Edges.json", 1.71, 1228, 772, 0.1016, "2");

	Map StrattonHallB = new Map("Stratton Hall B", "SH", "CS3733_Graphics/SHB.png", "Graphs/Nodes/SHB.json", "Graphs/Edges/SHBEdges.json", 1.71, 1364, 898, 0.0804, "B");
	Map StrattonHall1 = new Map("Stratton Hall 1", "SH", "CS3733_Graphics/SH1.png", "Graphs/Nodes/SH1.json", "Graphs/Edges/SH1Edges.json", 1.71, 1364, 898, 0.0813, "1");
	Map StrattonHall2 = new Map("Stratton Hall 2", "SH", "CS3733_Graphics/SH2.png", "Graphs/Nodes/SH2.json", "Graphs/Edges/SH2Edges.json", 1.71, 1364, 898, 0.0766, "2");
	Map StrattonHall3 = new Map("Stratton Hall 3", "SH", "CS3733_Graphics/SH3.png", "Graphs/Nodes/SH3.json", "Graphs/Edges/SH3Edges.json", 1.71, 1364, 898, 0.0749, "3");

	//set perspective transformations to all 3 groups
	PerspectiveTransform pt = new PerspectiveTransform();
	final DropShadow shadow = new DropShadow();
	final FadeTransition fader = new FadeTransition();

	Path g1path = new Path();
	MoveTo g1moveTo = new MoveTo();
	LineTo g1lineTo = new LineTo();


	//Create the global graph
	Graph globalGraph = new Graph();
	LinkedList<Building> buildings = new LinkedList<>();
	LinkedList<Map> maps = new LinkedList<>();

	final Button BackButton = new Button("Back");

    Text keyText = new Text(600, 640,"");
    Text toggleKeyText = new Text(995, 740,"Show Key");
	
	//Vars used for displaying Multiple maps after finding the route
	LinkedList<LinkedList<Node>> multiMap = new LinkedList<LinkedList<Node>>();
	int currMaps = 0;
	int currRoute = 0;
	Button NextInstruction = new Button("Next");
	Button PrevInstruction = new Button("Prev");

	LinkedList<Node> globalNodeList = new LinkedList<Node>();
	
	 ImageView pinView = new ImageView();
	 boolean pinAttached = false;

	Building BuildingRolledOver = new Building("");
	PauseTransition pause = new PauseTransition(Duration.millis(1000));

	@Override
    public void start(Stage primaryStage) {

    	//Add Maps to buildings
    	Campus.addMap(CampusMap);

    	AtwaterKent.addMap(AtwaterKentB);
    	AtwaterKent.addMap(AtwaterKent1);
    	AtwaterKent.addMap(AtwaterKent2);
    	AtwaterKent.addMap(AtwaterKent3);

    	GordonLibrary.addMap(GordonLibrarySB);
    	GordonLibrary.addMap(GordonLibraryB);
    	GordonLibrary.addMap(GordonLibrary1);
    	GordonLibrary.addMap(GordonLibrary2);
    	GordonLibrary.addMap(GordonLibrary3);

    	BoyntonHall.addMap(BoyntonHallB);
    	BoyntonHall.addMap(BoyntonHall1);
    	BoyntonHall.addMap(BoyntonHall2);
    	BoyntonHall.addMap(BoyntonHall3);

    	CampusCenter.addMap(CampusCenter1);
    	CampusCenter.addMap(CampusCenter2);
    	CampusCenter.addMap(CampusCenter3);

    	HigginsHouse.addMap(HigginsHouseB);
    	HigginsHouse.addMap(HigginsHouse1);
    	HigginsHouse.addMap(HigginsHouse2);
    	HigginsHouse.addMap(HigginsHouse3);
    	
    	HigginsHouseGarage.addMap(HigginsHouseGAR);
    	HigginsHouseGarage.addMap(HigginsHouseAPT);

    	StrattonHall.addMap(StrattonHallB);
    	StrattonHall.addMap(StrattonHall1);
    	StrattonHall.addMap(StrattonHall2);
    	StrattonHall.addMap(StrattonHall3);

    	ProjectCenter.addMap(ProjectCenter1);
    	ProjectCenter.addMap(ProjectCenter2);

    	 // Store the Buildings in a list
        // TODO Add more buildings to this list
    	buildings.add(Campus);
        buildings.add(AtwaterKent);
        buildings.add(GordonLibrary);
        buildings.add(CampusCenter);
        buildings.add(HigginsHouse);
        buildings.add(StrattonHall);
        buildings.add(ProjectCenter);
        buildings.add(BoyntonHall);

    	        
        toggleKeyText.setFont(Font.font ("manteka", 10));
        keyText.setFont(Font.font ("manteka", 20));
        
     // Iterate over the list of buildings and add their maps to another list
        //LinkedList<Map> maps = new LinkedList<>();
        for (Building b : buildings){
             maps.addAll(b.getMaps());
        }
        
        
    	double width = 80;
	    double height = 60;
    	pt = setCorners(pt, width, height);
    	shadow.setInput(pt);
    	

    	//Create a Building selection drop down menu
    	final VBox mapSelectionBoxV = new VBox(5);
    	final Label mapSelectorLabel = new Label("Choose map");
    	mapSelectorLabel.setTextFill(Color.WHITE);
    	mapSelectorLabel.setFont(Font.font ("manteka", 20));
    	mapSelectorLabel.setTextFill(Color.WHITE);
    	final HBox mapSelectionBoxH = new HBox(5);
    	final Button LoadMapButton = new Button("Load Map");
    	mapSelectionBoxH.getChildren().addAll(mapSelector, LoadMapButton);
    	mapSelectionBoxV.setLayoutX(820);
    	mapSelectionBoxV.setLayoutY(10);
    	mapSelectionBoxV.getChildren().addAll(mapSelectorLabel, mapSelectionBoxH);

    	//create Label for directions
		Label directionsTitle = new Label("Directions");
		directionsTitle.setTextFill(Color.WHITE);
		directionsTitle.setFont(Font.font ("manteka", 20));
		directionsTitle.setLayoutX(820);
    	directionsTitle.setLayoutY(80);

    	//Create a label and box for warnings, ie when the coordinates are outside the name
    	final HBox warningBox = new HBox(0);
    	final Label warningLabel = new Label("");
    	warningLabel.setTextFill(Color.WHITE);
    	warningBox.setLayoutX(10);
    	warningBox.setLayoutY(680);
    	warningBox.getChildren().addAll(warningLabel);

    	
    	//Find Route Button
    	//final Button findRouteButton = new Button("Find Route");
    	//findRouteButton.relocate(640, 640);
    	
    	//Next button (and previous)
    	NextInstruction.setTextFill(Color.WHITE);
    	NextInstruction.setLayoutX(950);
    	NextInstruction.setLayoutY(470);
    	
    	PrevInstruction.setTextFill(Color.WHITE);
    	PrevInstruction.setLayoutX(870);
    	PrevInstruction.setLayoutY(470);

    	//Searchable text boxes
    	VBox StartSearch = new VBox();
        VBox DestSearch = new VBox();
        StartText.setPromptText("Start");
        DestText.setPromptText("Destination");
        StartList.setMaxHeight(75);
        DestList.setMaxHeight(75);
        StartList.setItems(LocationOptions);
        DestList.setItems(LocationOptions);
        StartSearch.relocate(20, 640);
        StartSearch.getChildren().addAll(StartText, StartList);
        DestSearch.relocate(300, 640);
        DestSearch.getChildren().addAll(DestText, DestList);
        StartList.setOpacity(0);
        DestList.setOpacity(0);

        //create Label for Start and Destination
        Label StartLabel = new Label("Start");
        StartLabel.setTextFill(Color.WHITE);
        StartLabel.setFont(Font.font ("manteka", 20));
        StartLabel.setLayoutX(20);
        StartLabel.setLayoutY(610);
        Label DestLabel = new Label("Destination");
        DestLabel.setTextFill(Color.WHITE);
        DestLabel.setFont(Font.font ("manteka", 20));
        DestLabel.setLayoutX(300);
        DestLabel.setLayoutY(610);

        //Labels for the direction
        //MOVE TO METHOD AND USE FOR LOOP ONCE WE HAVE THE ROUTE CALCULATED

        //Create the map image
        File mapFile = new File("CS3733_Graphics/CampusMap.png");
        mapSelector.setValue("CampusMap"); // Default Map when App is opened
        Image mapImage = new Image(mapFile.toURI().toString());
        ImageView imageView = new ImageView();
        imageView.setImage(mapImage);
        imageView.setLayoutX(0);
        imageView.setLayoutY(0);

        //create background
        File backgroundFile = new File("CS3733_Graphics/Background.jpg");
        Image bgImage = new Image(backgroundFile.toURI().toString());
        ImageView bgView = new ImageView();
        bgView.setImage(bgImage);
        bgView.setLayoutX(0);
        bgView.setLayoutY(0);

        //Create a keyimage to place the map key on screen
    	File keyFile = new File("CS3733_Graphics/Key.png");
        Image keyImage = new Image(keyFile.toURI().toString());
        ImageView imageViewKey = new ImageView();
        imageViewKey.setImage(keyImage);
        imageViewKey.setLayoutX(830);
        imageViewKey.setLayoutY(570);

        
        //hide key
        toggleKeyText.setFill(new Color(1, 1, 1, 0.5));
        toggleKeyText.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {


            	toggleKeyText.setFill(new Color(1, 1, 1, 1));

            }
        });
        toggleKeyText.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

            	toggleKeyText.setFill(new Color(1, 1, 1, 0.5));

            }
        });

        toggleKeyText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
               	if(toggle) {
            		root.getChildren().add(imageViewKey);
            		toggle = false;
            	}
            	else {
            		root.getChildren().remove(imageViewKey);
            		toggle = true;
            	}

            }
        });
        
        


        //Add images to the screen
        root.getChildren().add(bgView); //Must add background image first!
        root.getChildren().add(mapSelectionBoxV);
        //root.getChildren().add(imageView);
        root.getChildren().add(keyText);
        root.getChildren().add(toggleKeyText);
        root.getChildren().add(StartSearch);
        root.getChildren().add(DestSearch);
        root.getChildren().addAll(directionsTitle, DestLabel, StartLabel);


        //Removes top bar!! Maybe implement a custom one to look better
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        //Generate the Global map graph
        globalGraph = createGlobalGraph(globalGraph);
        
        //now we can create the local edge connections
        LinkedList<Edge> edgeList = convertEdgeData(edgeListConversion);

        //generate the local map graph
        //graph = createGraph(graph, nodeList, edgeList);
      //Initialize the Drop down menu for initial Map
    	for(int i = 0; i < globalNodeList.size() ; i ++){
    		if(globalNodeList.get(i).getIsPlace())
    			LocationOptions.add((globalNodeList.get(i)).getName());
        }
    	
    	
    	 stageInitialWidthDifference = scene.getWidth()-1050;
         stageInitialHeightDifference = scene.getHeight()-750;


        imageView.setScaleX(0.75);
		imageView.setScaleY(0.75);
		imageView.relocate(-1000, -600);
        NodePane.setPrefSize(3000, 2000);
        canvas.resize(3000, 2000);
        canvas.setScaleX(0.75);
        canvas.setScaleY(0.75);
        canvas.relocate(-965, -643);
        highLight(NodePane, imageView, root, keyText);
	    drawNodes(nodeList, NodePane, root, StartText, DestText,imageView);
	    NodePane.setScaleX(0.75);
		NodePane.setScaleY(0.75);
		NodePane.relocate(-965, -643);
        final Group group = new Group(imageView, canvas, NodePane);
	    zoomPane = createZoomPane(group);
	    


	    //add to load map...
	    
	 
	    root.getChildren().add(zoomPane);
	    
	    
        //Add actions to the Load Map button
        LoadMapButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
            	loadMap( root, imageView);

            }
        });
        
        //Next instruction button actions
	    NextInstruction.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				currRoute++;
				changeInstructions(NodePane,  root,  imageView);
			}
	    });

	  //Next instruction button actions
	    PrevInstruction.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(currRoute > 0 || currRoute < currMaps){
					currRoute--;
					changeInstructions(NodePane,  root,  imageView);
				}
			}
	    });

        
        
        scene.getStylesheets().add(getClass().getResource("Buttons.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            
    		@Override
    		public void changed(ObservableValue<? extends Number> observable,
    				Number oldValue, Number newValue) {
    			stageInitialWidthDifference = scene.getWidth()- 1050;    			
    			scrollPane.setPrefViewportWidth(800 + stageInitialWidthDifference);
    			imageViewKey.setTranslateX(stageInitialWidthDifference);
    			NextInstruction.setTranslateX(stageInitialWidthDifference);
    			PrevInstruction.setTranslateX(stageInitialWidthDifference);
    			directionsTitle.setTranslateX(stageInitialWidthDifference);
    			toggleKeyText.setTranslateX(stageInitialWidthDifference);
    			mapSelectionBoxV.setTranslateX(stageInitialWidthDifference);
    			s1.setTranslateX(stageInitialWidthDifference);

    		}
    	});

	    scene.heightProperty().addListener(new ChangeListener<Number>() {
    		@Override
    		public void changed(ObservableValue<? extends Number> observable,
                   Number oldValue, Number newValue) {
    	        stageInitialHeightDifference = scene.getHeight()-750;
    			scrollPane.setPrefViewportHeight(605 + stageInitialHeightDifference);
    			
    			DestLabel.setTranslateY(stageInitialHeightDifference);
    			StartLabel.setTranslateY(stageInitialHeightDifference);
    			DestSearch.setTranslateY(stageInitialHeightDifference);
    			StartSearch.setTranslateY(stageInitialHeightDifference);
    			toggleKeyText.setTranslateY(stageInitialHeightDifference);
    			imageViewKey.setTranslateY(stageInitialHeightDifference);
    			keyText.setTranslateY(stageInitialHeightDifference);

    		}
    	});
	    
        

        DestText.textProperty().addListener(
                new ChangeListener<Object>() {
                    public void changed(ObservableValue<?> observable2,
                                        Object oldVal2, Object newVal2) {
                        handleSearchByKeyDest((String)oldVal2, (String)newVal2);
                    }
                });

            StartText.textProperty().addListener(
                    new ChangeListener<Object>() {
                        public void changed(ObservableValue<?> observable,
                                            Object oldVal, Object newVal) {
                            handleSearchByKeyStart((String)oldVal, (String)newVal);
                        }
                    });

            DestText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            	public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
            		if (newPropertyValue) {
            			DestList.setOpacity(100);
            			destBool = false;
                    }
                    else {
                    	DestList.setOpacity(0);
                    }
            	}
            });

            StartText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            	public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
            		if (newPropertyValue) {
            			StartList.setOpacity(100);
            			startBool= false;
                    }
                    else {
                    	StartList.setOpacity(0);
                    }
            	}
            });

            StartList.setOnMouseClicked(new EventHandler<MouseEvent>() {

    			public void handle(MouseEvent arg0) {
    				StartText.setText((String) StartList.getSelectionModel().getSelectedItem());
    				startBool = true;
    				if(destBool && startBool) {
    					gc.clearRect(0, 0, 8000, 6000); // Clears old path
    					root.getChildren().remove(zoomPane);
    					keyText.setText("");

                    	// Need to string compare from
                    	Node startPlace = new Node(0, 0, 0, "","", "", false, false, "");
                    	Node endPlace = new Node(0, 0, 0, "","","", false, false, "");
                    	for(int i = 0; i < globalGraph.getNodes().size(); i ++){
                        	if((globalGraph.getNodes().get(i)).getName().equals(StartText.getText())) {
                        		startPlace = (globalGraph.getNodes().get(i));
                        	}
                        	if((globalGraph.getNodes().get(i)).getName().equals(DestText.getText())) {
                        		endPlace = (globalGraph.getNodes().get(i));
                        	}
                        }
                    	System.out.println("start: " + startPlace.getName());
                    	System.out.println("end: " + endPlace.getName());

                    	LinkedList<Node> route = new LinkedList<Node>();
                        route = globalGraph.findRoute(startPlace, endPlace);
                        keyText.setFont(Font.font ("manteka", 20));

                        if(!(startPlace.equals(endPlace))) {

                            try {
                            	
                            //if the entire route is only on 1 map, display all instruction at once
                            displayInstructions(multiMap.get(currRoute), root);
                            if(currRoute > 0){
                            	root.getChildren().remove(PrevInstruction);
                            	root.getChildren().add(PrevInstruction);
                            }
                            root.getChildren().remove(NextInstruction);
                        	root.getChildren().add(NextInstruction); //attach next button
                        	String initials = "";
                        	
                        	for(int i = 0; i < maps.size(); i++){
                        		System.out.println("CURRENT ROUTE: "+ currRoute);
                        		System.out.println("multiMap.get(currRouteE: "+ multiMap.get(currRoute).get(0).getFloorMap());
                        		if(maps.get(i).getName().equals(multiMap.get(currRoute).get(0).getFloorMap()))
                        			initials = maps.get(i).getInitials()+maps.get(i).getFloor();
                        	}
                        	System.out.println("INITIALS: "+ initials);
                        	gc.clearRect(0, 0, 6000, 3000);

                            
                                System.out.println("Route length: " + route.size());
                                //Display the directions on the side
                                System.out.println("Route = " + route);
                                //if(!(route.size() <= 1)){
                                multiMap = splitRoute(route);//is endlessly looping or suttin
                                currRoute = 0;

                                //}
                                //if the entire route is only on 1 map, display all instruction at once
                                displayInstructions(multiMap.get(currRoute), root);
                                root.getChildren().remove(NextInstruction);
                                root.getChildren().add(NextInstruction); //attach next button

                                for (int i = 0; i < maps.size(); i++) {
                                    System.out.println("CURRENT ROUTE: " + currRoute);
                                    System.out.println("multiMap.get(currRouteE: " + multiMap.get(currRoute).get(0).getFloorMap());
                                    if (maps.get(i).getName().equals(multiMap.get(currRoute).get(0).getFloorMap()))
                                        initials = maps.get(i).getInitials() + maps.get(i).getFloor();
                                }
                                System.out.println("INITIALS: " + initials);
                                gc.clearRect(0, 0, 6000, 3000);

                                mapSelector.setValue(initials);
                                loadMap(root, imageView);
                                drawNodes(nodeList, NodePane, root, StartText, DestText, imageView);
                                drawRoute(gc, multiMap.get(currRoute));


                                System.out.println(" " + route);
                                for (int i = 0; i < route.size(); i++) {
                                    System.out.println("Route node: " + i + " , " + route.get(i).getName());
                                }

                                final Group group = new Group(imageView, canvas, NodePane);
                                zoomPane = createZoomPane(group);
                                root.getChildren().add(zoomPane);

                                route = new LinkedList<Node>();
                            } catch (NullPointerException n){
                                keyText.setText("Path not Found");
                                keyText.setFill(Color.RED);
                                loadMap(root, imageView);
                            }
        				} else {
        					loadMap(root, imageView);
        					keyText.setFont(Font.font ("manteka", 14));
        					keyText.setFill(Color.RED);
        					keyText.setText("Your Start and Destination are the same");
        				}
    				}

    			}
            });
            DestList.setOnMouseClicked(new EventHandler<MouseEvent>() {

    			public void handle(MouseEvent arg0) {
    				DestText.setText((String) DestList.getSelectionModel().getSelectedItem());
    				destBool = true;
    				if(destBool && startBool) {
    					gc.clearRect(0, 0, 8000, 6000); // Clears old path
    					root.getChildren().remove(zoomPane);
    					keyText.setText("");

                    	// Need to string compare from
                    	Node startPlace = new Node(0, 0, 0, "","", "", false, false, "");
                    	Node endPlace = new Node(0, 0, 0, "","","", false, false, "");
                    	for(int i = 0; i < globalGraph.getNodes().size(); i ++){
                        	if((globalGraph.getNodes().get(i)).getName().equals(StartText.getText())) {
                        		startPlace = (globalGraph.getNodes().get(i));
                        	}
                        	if((globalGraph.getNodes().get(i)).getName().equals(DestText.getText())) {
                        		endPlace = (globalGraph.getNodes().get(i));
                        	}
                        }
                    	System.out.println("start: " + startPlace.getName());
                    	System.out.println("end: " + endPlace.getName());

                    	LinkedList<Node> route = new LinkedList<Node>();
                        route = globalGraph.findRoute(startPlace, endPlace);
                        
                        if(!(startPlace.equals(endPlace))) {
                        
                        	try{
                            	System.out.println("Route lenth: " + route.size());
                                //Display the directions on the side
                                System.out.println("Route = " + route);
                                //if(!(route.size() <= 1)){
                                multiMap = splitRoute(route);//is endlessly looping or suttin
                                currRoute = 0;

                                //if the entire route is only on 1 map, display all instruction at once
                                displayInstructions(multiMap.get(currRoute), root);
                                if(currRoute > 0){
                                	root.getChildren().remove(PrevInstruction);
                                	root.getChildren().add(PrevInstruction);
                                }
                                root.getChildren().remove(NextInstruction);
                            	root.getChildren().add(NextInstruction); //attach next button
                            	String initials = "";
                				
                            	for(int i = 0; i < maps.size(); i++){
                            		System.out.println("CURRENT ROUTE: "+ currRoute);
                            		System.out.println("multiMap.get(currRouteE: "+ multiMap.get(currRoute).get(0).getFloorMap());
                            		if(maps.get(i).getName().equals(multiMap.get(currRoute).get(0).getFloorMap()))
                            			initials = maps.get(i).getInitials()+maps.get(i).getFloor();
                            	}
                            	System.out.println("INITIALS: "+ initials);
                            	gc.clearRect(0, 0, 6000, 3000);

                            	mapSelector.setValue(initials);
                            	loadMap(root, imageView);
                            	drawNodes(nodeList, NodePane, root, StartText, DestText, imageView);
                                drawRoute(gc, multiMap.get(currRoute));
                            	
                                System.out.println(" " +route);
                                for(int i = 0; i < route.size(); i++){
                                	System.out.println("Route node: " + i + " , " + route.get(i).getName());
                                }

                                final Group group = new Group(imageView, canvas, NodePane);
                        	    zoomPane = createZoomPane(group);
                        	    root.getChildren().add(zoomPane);
                                route = new LinkedList<Node>();
                                
            				} catch (NullPointerException n){
            					keyText.setText("Path not Found");
            					keyText.setFill(Color.RED);
            					loadMap(root, imageView);
                            }
        				} else {
        					loadMap(root, imageView);
        					keyText.setFont(Font.font ("manteka", 14));
        					keyText.setFill(Color.RED);
        					keyText.setText("Your Start and Destination are the same");
        				}
				}

    			}
            });
    }
	///END OF MAIN ***************************************************************

    //Display all of the instructions on screen
    private void displayInstructions(LinkedList<Node> route, Pane root){

    	//what if we move this in here..???
    	//multiMap = splitRoute(route);

    	//create vertical box to add labels too
    	directionBox.setLayoutX(820);
    	directionBox.setLayoutY(100);

    	//add a possible scroll box for long routes..
		 s1.setPrefSize(220, 360);
		 s1.setStyle("-fx-background-color: transparent");
		 s1.setLayoutX(820);
		 s1.setLayoutY(110);
		 
    	//or eventually break up into multiple sections of instructions based
    	//on current map displayed
    	//for(int k = 0; k < multiMap.size(); k++){
    		
    	//}
    		
    	//convert the route to a list of string instructions
    	stepIndicator steps = new stepIndicator(route);
    	
    	LinkedList<Step> directions = steps.lInstructions();
    	if(currRoute == 0)
    		directions.addFirst(new Step(0,"Walk Straight",0, directions.get(0).getX(),directions.get(0).getY()));

    	//iterate through the list of instructions and create labels for each one and attach to the root
    	for(int i = 0; i < directions.size(); i++){
    		HBox StepBox = new HBox(2);
    		//StepBox.setStyle("-fx-border-color: black;");
    		Label newDirection;

    		if(directions.get(i).getDistance() == 0.0) {
    			newDirection = new Label(directions.get(i).getMessage());
    		} else {
    			newDirection = new Label(directions.get(i).getMessage() + " and go for " + round(directions.get(i).getDistance(), 1) + " ft");
    		}

    		File arrowFile = new File("CS3733_Graphics/DirectionImages/"+directions.get(i).getIconID()+".png");
            Image arrowImage = new Image(arrowFile.toURI().toString());
            ImageView arrowView = new ImageView();
            arrowView.setImage(arrowImage);
            Line breakLine = new Line(0, 0, 210, 0);
            breakLine.setLayoutX(10);
            
            String style = StepBox.getStyle();
            
            int currentInstruction = i;
            StepBox.setOnMouseMoved(new EventHandler<MouseEvent>() {
            	public void handle(MouseEvent event) {
            		if(currentInstruction >= 0){
            			StepBox.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
                		//highlight the current path
                		int NodeX = directions.get(currentInstruction).getX();
                		int NodeY = directions.get(currentInstruction).getY();
                		//attach an image, or do an animation on this current node
                		File PinFile = new File("CS3733_Graphics/pin.png");
                        Image pinImage = new Image(PinFile.toURI().toString());
                       
                        pinView.setImage(pinImage);
                        pinView.setLayoutX(NodeX-10);
                        pinView.setLayoutY(NodeY-36);
                        if(!pinAttached){
                        	System.out.println(NodeX + "   "+ NodeY);
                        	NodePane.getChildren().add(pinView);
                        	pinAttached = true;
                        }
            		}
            		
            	}
            });
            StepBox.setOnMouseExited(new EventHandler<MouseEvent>() {
            	public void handle(MouseEvent event) {
            		StepBox.setStyle(style);
            		NodePane.getChildren().remove(pinView);
                    pinAttached = false;
            	}
            });

    		StepBox.getChildren().addAll(arrowView, newDirection);
    		directionBox.getChildren().addAll(StepBox, breakLine);
    	}
    	
    	root.getChildren().remove(s1);
    	s1.setContent(directionBox);
    	root.getChildren().add(s1);
    	return;

    }
    
    public void changeInstructions(Pane NodePane, Pane root, ImageView imageView){
    	displayInstructions(multiMap.get(currRoute), root);
		
    	String initials = "";
    	for(int i = 0; i < maps.size(); i++){
    		System.out.println("CURRENT ROUTE: "+ currRoute);
    		System.out.println("multiMap.get(currRouteE: "+ multiMap.get(currRoute).get(0).getFloorMap());
    		if(maps.get(i).getName().equals(multiMap.get(currRoute).get(0).getFloorMap()))
    			initials = maps.get(i).getInitials()+maps.get(i).getFloor();
    	}
    	gc.clearRect(0, 0, 8000, 6000);
    	mapSelector.setValue(initials);
    	loadMap(root, imageView);
    	
    	drawNodes(nodeList, NodePane, root, StartText, DestText, imageView);
        drawRoute(gc, multiMap.get(currRoute));
		
        //Determine which buttons to display when changing instructions
        if(currRoute > 0){
        	root.getChildren().remove(PrevInstruction);
        	root.getChildren().add(PrevInstruction);
        }
        else if(currRoute == 0){
        	root.getChildren().remove(PrevInstruction);
        }
		
		//if we are on the last page of instructions, remove next button
		if(currRoute >= currMaps-1){
			root.getChildren().remove(NextInstruction);
		}
		else {
			root.getChildren().add(NextInstruction);
		}
    }


	private void getMapSelector(Building building, Pane root, ImageView imageView) {
	   
		//If the user moved out of the building dont display the map
		/*if(BuildingRolledOver == null)
        	return;*/
		
		if(NodePane.getChildren().contains(LayerGroup)){
    		NodePane.getChildren().remove(LayerGroup);
    		LayerGroup.getChildren().clear();
		}
		
        gc.clearRect(0, 0, 8000, 6000);
        //drawNodes(nodeList, NodePane, root, StartText, DestText, imageView);


 	    //****** ATTACHING MAPS OVER, DELETING ON MOUSE OVER ***** 
        //MOve this effect to global so we dont make a new one every time
        

		//Attach Building label
		BuildingNameLabel.setText(building.getName());
		BuildingNameLabel.setTextFill(Color.BLACK);
		BuildingNameLabel.setFont(Font.font ("manteka", 30));
		BuildingNameLabel.setLayoutX(20);
		BuildingNameLabel.setLayoutY(560);
		root.getChildren().remove(BuildingNameLabel);
    	root.getChildren().add(BuildingNameLabel);

    	
    	//Load the layered Maps
	    int currentFloor = 0;
	    root.getChildren().remove(LayerGroup);
	    LayerGroup.getChildren().clear();

    	for(int i = 1; i <= building.getNumMaps(); i++){
    		currentFloor = i-1;
    		System.out.println("CS3733_Graphics/LayerMap/"+building.getMaps().get(currentFloor).getInitials() + building.getMaps().get(currentFloor).getFloor() + "L.png");
    		File mapFile = new File("CS3733_Graphics/LayerMap/"+building.getMaps().get(currentFloor).getInitials() + building.getMaps().get(currentFloor).getFloor() + "L.png");//Change back to above
    		Image image = new Image(mapFile.toURI().toString());
    		ImageView mapImageView = new ImageView();
    		mapImageView.setImage(image);
    		
    		//calculuate the rotational constant and offsets
    		double rc = (180*BuildingRolledOver.getMaps().get(0).getRotationalConstant())/Math.PI;
    		double xplacement = BuildingRolledOver.getMaps().get(0).getGlobalToLocalOffsetX();
    		double yplacement = BuildingRolledOver.getMaps().get(0).getGlobalToLocalOffsetY();


   	     	Group g1 = new Group();
   	     	g1.setEffect(pt);
   	     	g1.setOpacity(.3);
   	     	g1.getChildren().add(mapImageView);
   	     	
	     	//TODO 
   	     	//ATTAH BUILDING to the layeredGroup so that when you roll out you can still be on the builing
	     	//CUSTOM OFFSETS AHHHH DAMNIT
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Campus Center")){
	     		g1.setLayoutX(xplacement+360);
		     	g1.setLayoutY(yplacement+290-i*45);
	     	}
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Higgins House")){
	     		g1.setLayoutX(xplacement+400);
		     	g1.setLayoutY(yplacement+600-i*45);
	     	}
	     	//TODO
	     	///****** WHAT IS THAT ACTUAL NAME OF THIS?????***
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Higgins House Garage")){
	     		g1.setLayoutX(xplacement+250);
		     	g1.setLayoutY(yplacement-500-i*45);
	     	}
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Stratton Hall")){
	     		g1.setLayoutX(xplacement+360);
		     	g1.setLayoutY(yplacement+210-i*45);
	     	}
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Boynton Hall")){
	     		g1.setLayoutX(xplacement+370);
		     	g1.setLayoutY(yplacement+220-i*45);
	     	}
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Gordon Library")){
	     		g1.setLayoutX(xplacement+400);
		     	g1.setLayoutY(yplacement+320-i*45);
	     	}
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Project Center")){
	     		g1.setLayoutX(xplacement+470);
		     	g1.setLayoutY(yplacement+300-i*45);
	     	}
	     	//TODO
	     	//AWKWARD BC OF SHAPE ?? TOP LEVEL NOT HIGHLIGHTING!!!! - is it not being attached? CHECK NUMBER OF FLOORS
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Atwater Kent")){
	     		g1.setLayoutX(xplacement+400);
		     	g1.setLayoutY(yplacement+270-i*30);
	     	}
	     	//TODO FILL IN ONCE WE GET THE FULLER LAB INFO!!!!!!!!
	     	if(BuildingRolledOver.getMaps().get(0).getBuildingName().equals("Fuller Labs")){
	     		g1.setLayoutX(xplacement+400);
		     	g1.setLayoutY(yplacement+270-i*45);
	     	}
	     	/// ABOVE^^^^^^
	     		
	     		
	     	applyAnimation(g1, i, imageView);
	     	
	     	//fade in the group of layered maps
	     	shortFadeIn(LayerGroup);
   	     	LayerGroup.getChildren().add(g1);

   	     	//used inside action scope
   	     	int floor = currentFloor;
   	     	//Add actions to each of the layered map buttons
   	     	g1.setOnMouseClicked(new EventHandler<MouseEvent>() {
   	     			public void handle(MouseEvent event) {
   	     				System.out.println(floor);
   	     				root.getChildren().remove(LayerGroup);
   	     				LayerGroup.getChildren().clear();
   	     				root.getChildren().remove(BackButton);
   	     				if(root.getChildren().contains(BuildingNameLabel))
   	     					root.getChildren().remove(BuildingNameLabel);
   	     				screenFadeBack(imageView);
   	     				BuildingNameLabel.setText(building.getName()+" " + building.getMaps().get(floor).getFloor());
   	     				mapSelector.setValue(building.getMaps().get(floor).getInitials() + building.getMaps().get(floor).getFloor() );
   	     				loadMap(root, imageView);
   	     			}
   	     	});
   	     	g1.setOnMouseExited(new EventHandler<MouseEvent>() {
     			public void handle(MouseEvent event) {
     				BuildingNameLabel.setText(building.getName());
     				g1.setEffect(pt);
     				shortFadeOut(g1);
     			}
   	     	});
   	     	g1.setOnMouseMoved(new EventHandler<MouseEvent>() {
   	     		public void handle(MouseEvent event) {
   	     			BuildingNameLabel.setText(building.getName()+" " + building.getMaps().get(floor).getFloor());
   	     			g1.setEffect(shadow);
   	     		}
   	     	});
   	     	//Fade out the rest of the maps **START OTHER ALPHA AT LOW**
   	     	g1.setOnMouseEntered(new EventHandler<MouseEvent>() {
	     		public void handle(MouseEvent event) {
	     			BuildingNameLabel.setText(building.getName()+" " + building.getMaps().get(floor).getFloor());
	     			g1.setEffect(shadow);
	     			shortFadeIn(g1);
	     		}
	     	});
    	}
    	if(!NodePane.getChildren().contains(LayerGroup))
    		NodePane.getChildren().add(LayerGroup);
	    
	    //add actions to when you leave the layered group, remove it
	    LayerGroup.setOnMouseExited(new EventHandler<MouseEvent>() {
     		public void handle(MouseEvent event) {
     			BuildingNameLabel.setText("..");
     			if(root.getChildren().contains(BuildingNameLabel))
     				root.getChildren().remove(BuildingNameLabel);
     			screenFadeBack(imageView);
     			///Change to fade out of the layered group
     			fadeOutLayeredGroup(LayerGroup);
     		}
			private void fadeOutLayeredGroup(Group layerGroup) {
				FadeTransition blur = new FadeTransition();
				blur.setDuration(Duration.millis(500));
				blur.setFromValue(.3);
				blur.setToValue(0);
				blur.setNode(layerGroup);
				blur.play();
				
				//remove the layered group from stage when we roll out after it fades out
				blur.setOnFinished(new EventHandler<ActionEvent>(){
			            @Override
			            public void handle(ActionEvent arg0) {
			            	NodePane.getChildren().remove(LayerGroup);
			     			LayerGroup.getChildren().clear();
			            }
			    });
			}
     	});
	}
	
	private void shortFadeOut(Group g1){
		FadeTransition blur = new FadeTransition();
		blur.setDuration(Duration.millis(300));
		blur.setFromValue(1);
		blur.setToValue(.3);
		blur.setNode(g1);
		blur.play();
	}
	//Fade in for a group
	private void shortFadeIn(Group g1){
		FadeTransition blur = new FadeTransition();
		blur.setDuration(Duration.millis(300));
		blur.setFromValue(.3);
		blur.setToValue(1);
		blur.setNode(g1);
		blur.play();
	}
	//restore map alpha back to 100%
	private void screenFadeBack(ImageView imageView){
		//Fade the alpha
		FadeTransition blur = new FadeTransition();
		blur.setDuration(Duration.millis(800));
		blur.setFromValue(.3);
		blur.setToValue(1.0);
		blur.setNode(imageView);
		blur.play();
	}
	
	private void applyAnimation(Group g1, int i,  ImageView imageView){
		//Fade the alpha
		FadeTransition blur = new FadeTransition();
		blur.setDuration(Duration.millis(1000));
		blur.setFromValue(1.0);
		blur.setToValue(.3);
		blur.setNode(imageView);
		blur.play();
		
		//where to attach the maps
		//**** CHANGING THESE VARS BELOW (ADDING STUFF)*****
		//double xplacement = BuildingRolledOver.getMaps().get(0).getGlobalToLocalOffsetX();
		//double yplacement = BuildingRolledOver.getMaps().get(0).getGlobalToLocalOffsetY();

		 //FLOOR 1
		 Path g1path = new Path();
		 MoveTo g1moveTo = new MoveTo();
		 g1moveTo.setX(0);
		 g1moveTo.setY(0);
		 LineTo g1lineTo = new LineTo();
		 g1lineTo.setX( -i*40);//-i*15
		 g1lineTo.setY( -i*40);
		 g1path.getElements().add(g1moveTo);
		 g1path.getElements().add(g1lineTo);

		 PathTransition g1pt = new PathTransition();
		 g1pt.setDuration(Duration.millis(1000));
		 g1pt.setPath(g1path);
		 g1pt.setNode(g1);
		 g1pt.setOrientation(PathTransition.OrientationType.NONE);
		 //g1pt.setCycleCount(Timeline.INDEFINITE); //for levitation, but nahh
		 g1pt.setAutoReverse(true);
		 g1pt.play();
    }

	//Custom ones for each building, move pt from global to local again
    private PerspectiveTransform setCorners(PerspectiveTransform pt, double width, double height) {
		 pt.setUlx(0);//upper left
	     pt.setUly(0);
	     pt.setUrx(300);//upper right
	     pt.setUry(0);
	     pt.setLrx(300);//Lower right
	     pt.setLry(150);
	     pt.setLlx(0);//lower left
	     pt.setLly(150);
	     return pt;
	}

    //If the route is across multiple maps we want to break it up
    private LinkedList<LinkedList<Node>> splitRoute(LinkedList<Node> route){
    	System.out.println("routeSize = " + route.size());
    	//change this.. or check if it before splitorator
    	if(route.size() == 0){
    		   LinkedList<LinkedList<Node>> splitRoutes = new LinkedList<LinkedList<Node>>();
    		   return splitRoutes;
    	}
;
    	
    	LinkedList<LinkedList<Node>> splitRoutes = new LinkedList<LinkedList<Node>>();
    	String aBuilding = route.get(0).getFloorMap();
    	int newBuildingIndex = 0;
    	System.out.println("aBuilding = " + aBuilding);
    	
    	for(int i = 0; i < route.size(); i++){
    		System.out.println("Route.get(i)" + route.get(i).getFloorMap());
    		//if the current node is in a different building, chop off the stuff before and place it in its own list
    		if(!aBuilding.equals(route.get(i).getFloorMap()) || i == route.size()-1){
    			System.out.println("Switched Floors");
    			//add from newBuildingIndex to i-1
    			LinkedList<Node> tempRoute = new LinkedList<Node>();
    			for(int k = newBuildingIndex; k <= i; k++){
    				tempRoute.add(route.get(k));
    			}
    			newBuildingIndex = i;
    			aBuilding = route.get(i).getFloorMap(); //since i is the new building
    			splitRoutes.add(tempRoute);
    			System.out.println("splitRoutesize = "+splitRoutes.size());
    		}
    		//if we haven't added any yet, route is all on 1 map
    		
    				
    	}
    	if(splitRoutes.size() == 0){
			splitRoutes.add(route);
		}
    	currMaps = splitRoutes.size();
    	return splitRoutes;
    }
    private Graph createGlobalGraph(Graph GLOBALGRAPH) {

    	//create Global nodes and edges list to pass to other createGraph method
    	LinkedList<Edge> globalEdgeList = new LinkedList<Edge>();
    	LinkedList<EdgeDataConversion> globalEdgeListConversion = new LinkedList<EdgeDataConversion>();

    	//Manually add all of the Nodes...
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/CampusMap.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/AKB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/AK1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/AK2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/AK3.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/BHB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/BH1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/BH2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/BH3.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/CC1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/CC2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/CC3.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/GLSB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/GLB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/GL1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/GL2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/Gl3.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HH1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HH2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HH3.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HHAPT.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HHB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/HHGAR.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/PC1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/PC2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/SHB.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/SH1.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/SH2.json"));
    	globalNodeList.addAll(JsonParser.getJsonContent("Graphs/Nodes/SH3.json"));
    	GLOBALGRAPH = createGraph(GLOBALGRAPH, globalNodeList, globalEdgeList);
    	
    	//Manually add all of the Edges
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/CampusMapEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/AKBEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/AK1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/AK2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/AK3Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/GLSBEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/GLBEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/GL1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/GL2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/GL3Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/SHBEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/SH1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/SH2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/SH3Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/CC1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/CC2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/CC3Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/PC1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/PC2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HHBEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HHAPTEdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HHGAREdges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HH1Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HH2Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	globalEdgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/HH3Edges.json");
    	globalEdgeList.addAll(convertEdgeData(globalEdgeListConversion));
    	
    	//TODO Add rest
    	
    	for(int i=0; i < globalNodeList.size(); i++) {
    		
    		switch (globalNodeList.get(i).getType()) {
    		case "Point":				PointNodes.add(globalNodeList.get(i));
    		case "Transition Point": 	TransitionNodes.add(globalNodeList.get(i));
    		case "Staircase":			StaircaseNodes.add(globalNodeList.get(i));
    		case "Vending Machine":		VendingMachineNodes.add(globalNodeList.get(i));
    		case "Water Fountain":		WaterFountainNodes.add(globalNodeList.get(i));
    		case "Men's Bathroom":		MensBathroomNodes.add(globalNodeList.get(i));
    		case "Women's Bathroom":	WomensBathroomNodes.add(globalNodeList.get(i));
    		case "Emergency Pole":		EmergencyPoleNodes.add(globalNodeList.get(i));
    		case "Dining":				DiningNodes.add(globalNodeList.get(i));
    		case "Elevator":			ElevatorNodes.add(globalNodeList.get(i));
    		case "ComputerLab":			ComputerLabNodes.add(globalNodeList.get(i));
    		}
    		
    	}
    	

    	GLOBALGRAPH = createGraph(GLOBALGRAPH, globalNodeList, globalEdgeList);
    	return GLOBALGRAPH;
	}

	private Graph createGraph(Graph g, LinkedList<Node> nodes, LinkedList<Edge> edges){
    	g.setNodes(nodes);
    	//System.out.print("Nodes: " + nodes);
    	System.out.println();
    	//System.out.print("Edges: " + edges);
    	System.out.println("///***&&&");
    	//Added this way so they can be bi directionally added
    	for(int i = 0; i < edges.size(); i++){
    		System.out.print("Edgefrom: " + edges.get(i).getFrom().getName() + " , to: "+ edges.get(i).getTo().getName());
    		g.addEdgeByString(edges.get(i).getFrom().getName(), edges.get(i).getTo().getName());
    	}
    	return g;
    }


    private void drawNodes(LinkedList<Node> nodes, Pane NodePane, Pane root, TextField startText, TextField destText, ImageView imageView){
    	int i;

    	for(i = 0; i < nodes.size(); i ++){
    		if(nodes.get(i).getIsPlace()){
        		Button newNodeButton = new Button("");
        		newNodeButton.setStyle(
                        "-fx-background-radius: 5em; " +
                        "-fx-min-width: " + 15*buttonRescale + "px; " +
                        "-fx-min-height: " + 15*buttonRescale + "px; " +
                        "-fx-max-width: " + 15*buttonRescale + "px; " +
                        "-fx-max-height: " + 15*buttonRescale + "px;"
                );
        		newNodeButton.setId("glass-grey");
            	newNodeButton.relocate(nodes.get(i).getX()-7*buttonRescale, nodes.get(i).getY()-7*buttonRescale);
            	Node newNode = nodes.get(i);
            	newNodeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                    	if (!start){
                    		if(startButton != null && endButton != startButton)startButton.setId(null);
                            newNodeButton.setId("shiny-orange");
                            startButton = newNodeButton;
                    		if(newNode.getIsPlace()) startNode = newNode.getName();
                    		startText.setText(startNode);
                    		start = true;
                    		startButtonBool = true;
                    	}
                    	else if(!end){
                    		if(endButton != null && endButton != startButton)endButton.setId(null);
                            newNodeButton.setId("shiny-orange");
                            endButton = newNodeButton;
                    		if(newNode.getIsPlace()) endNode = newNode.getName();
                    		DestText.setText(endNode);
                    		start = false;
                    		end = false;
                    		destButtonBool = true;
                    	}
                    	
                    	if(startButtonBool && destButtonBool) {
                            destButtonBool = false;
                            startButtonBool = false;
                            gc.clearRect(0, 0, 8000, 6000); // Clears old path
                            root.getChildren().remove(zoomPane);
                            keyText.setText("");

                            // Need to string compare from
                            Node startPlace = new Node(0, 0, 0, "", "", "", false, false, "");
                            Node endPlace = new Node(0, 0, 0, "", "", "", false, false, "");
                            for (int i = 0; i < globalGraph.getNodes().size(); i++) {
                                if ((globalGraph.getNodes().get(i)).getName().equals(StartText.getText())) {
                                    startPlace = (globalGraph.getNodes().get(i));
                                }
                                if ((globalGraph.getNodes().get(i)).getName().equals(DestText.getText())) {
                                    endPlace = (globalGraph.getNodes().get(i));
                                }
                            }
                            System.out.println("start: " + startPlace.getName());
                            System.out.println("end: " + endPlace.getName());

                            LinkedList<Node> route = new LinkedList<Node>();
                            route = globalGraph.findRoute(startPlace, endPlace);
                            
                            if(!(startPlace.equals(endPlace))) {
                            	try {
                                    System.out.println("Route length: " + route.size());
                                    //Display the directions on the side
                                    System.out.println("Route = " + route);
                                    //if(!(route.size() <= 1)){
                                    root.getChildren().remove(zoomPane);
                                    multiMap = splitRoute(route);//is endlessly looping or suttin
                                    currRoute = 0;
                                    
                                    //if the entire route is only on 1 map, display all instruction at once
                                    displayInstructions(multiMap.get(currRoute), root);
                                    if (multiMap.size() != 1) {
                                        root.getChildren().remove(NextInstruction);
                                        root.getChildren().add(NextInstruction); //attach next button
                                    }
                                    String initials = "";
                                    System.out.println("MAPSIZE: " + maps.size());
                                    for (int i = 0; i < maps.size(); i++) {
                                        System.out.println("MAP!!!!!: " + maps.get(i).getName());
                                        System.out.println("CURRENT ROUTE: " + currRoute);
                                        System.out.println("multiMap.get(currRouteE: " + multiMap.get(currRoute).get(0).getFloorMap());
                                        if (maps.get(i).getName().equals(multiMap.get(currRoute).get(0).getFloorMap()))
                                            initials = maps.get(i).getInitials() + maps.get(i).getFloor();
                                    }

                                    System.out.println("MAP!!!!!: " + multiMap.get(currRoute).get(0).getFloorMap());
                                    gc.clearRect(0, 0, 6000, 3000);

                                    mapSelector.setValue(initials);
                                    System.out.println("initials = " + initials);
                                    nodeList = JsonParser.getJsonContent("Graphs/Nodes/" + initials + ".json");

                                    loadMap(root, imageView);
                                    root.getChildren().remove(zoomPane);
                                    for (int h = 0; h < nodeList.size(); h++) {
                                        System.out.println("NodeList!!! = " + nodeList.get(h).getIsPlace());
                                    }
                                    
                                    System.out.println("NodeList Size = " + nodeList.size());
                                    drawRoute(gc, multiMap.get(currRoute));
                                    NodePane.getChildren().clear();
                                    LinkedList<Node> tempNodeList = new LinkedList<Node>();
                                    tempNodeList.add(multiMap.get(currRoute).get(0));
                                    tempNodeList.add(multiMap.get(currRoute).get(multiMap.get(currRoute).size()-1));
                                    
                                    //Draws only the start and end nodes of the route
                                    drawNodes(tempNodeList, NodePane, root, StartText, DestText, imageView);
                                    
                                    final Group group = new Group(imageView, canvas, NodePane);
                                    zoomPane = createZoomPane(group);
                                    root.getChildren().add(zoomPane);
                                    route = new LinkedList<Node>();
                                    
                                } catch (NullPointerException n){
                                    keyText.setText("No Path Found");
                            		keyText.setFill(Color.RED);
                            		loadMap(root, imageView);
                                }
                            } else {
                            loadMap(root, imageView);
                            keyText.setFont(Font.font ("manteka", 14));
                            keyText.setFill(Color.RED);
                            keyText.setText("Your Start and Destination are the same");
                            }
                    	}
                    	
    				}
                });
            	NodePane.getChildren().add(newNodeButton);
    		} else if(!nodes.get(i).getIsPlace()){
    			//Do nothing
    		}

    	}
    }

    private void drawRoute(GraphicsContext gc, LinkedList<Node> route) {
    	 Color customBlue = Color.web("0x00b3fd");
    	 gc.setLineCap(StrokeLineCap.ROUND);
    	//iterate through the route drawing a connection between nodes
    	for(int i = 1; i < route.size(); i ++){
    		if((!route.get(i-1).getType().equals("Transition Point")&&!route.get(i-1).getType().equals("Staircase")) 
    				||(!route.get(i).getType().equals("Transition Point")&&!route.get(i).getType().equals("Staircase"))){
    			gc.setLineWidth(5*buttonRescale);
                gc.setStroke(Color.BLACK);
    	  		gc.strokeLine(route.get(i-1).getX(), route.get(i-1).getY(), route.get(i).getX(),route.get(i).getY());
    		}
    		
    	}
    	for(int i = 1; i < route.size(); i ++){
    		if((!route.get(i-1).getType().equals("Transition Point")&&!route.get(i-1).getType().equals("Staircase"))
    				||(!route.get(i).getType().equals("Transition Point")&&!route.get(i).getType().equals("Staircase"))){
    			gc.setLineWidth(3*buttonRescale);
                gc.setStroke(customBlue);
    	  		gc.strokeLine(route.get(i - 1).getX(), route.get(i - 1).getY(), route.get(i).getX(), route.get(i).getY());
    		}
    		

    	}
    }

    private LinkedList<Edge> convertEdgeData(LinkedList<EdgeDataConversion> edgeData) {
    	LinkedList<Edge> edgeList = new LinkedList<Edge>();
    	int from = 0, to = 0;

    	//iterate through the edges
    	for(int i = 0; i < edgeData.size(); i ++){
    		//System.out.println("Edge Iterator: " + i);
    		//iterate throught he nodelist to find the matching node
    		for(int j = 0; j < globalNodeList.size(); j ++){
    			//System.out.println("NodeSize: "+nodeList.size());
    			//System.out.println("Node: "+nodeList.get(j)+", i: "+i+" , j: "+j);
    			if(edgeData.get(i).getFrom().equals((globalNodeList.get(j)).getName())){
					from = j;
				}
				if(edgeData.get(i).getTo().equals((globalNodeList.get(j)).getName())){
					to = j;
				}

    		}
    		Edge newEdge = new Edge(globalGraph.getNodes().get(from), globalGraph.getNodes().get(to), edgeData.get(i).getDistance());
			edgeList.add(newEdge);
    	}

    	return edgeList;
    }

    public void handleSearchByKeyStart(String oldVal, String newVal) {
    	// If the number of characters in the text box is less than last time
        // it must be because the user pressed delete
        if ( oldVal != null && (newVal.length() < oldVal.length()) ) {
            // Restore the lists original set of entries
            // and start from the beginning
        	StartList.setItems(LocationOptions);
        }
        StartList.setOpacity(100);


        // Break out all of the parts of the search text
        // by splitting on white space
        String[] parts = newVal.toUpperCase().split(" ");

        // Filter out the entries that don't contain the entered text
        ObservableList<String> subentries = FXCollections.observableArrayList();
        for ( Object entry: StartList.getItems() ) {
            boolean match = true;
            String entryText = (String)entry;
            for ( String part: parts ) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                if ( ! entryText.toUpperCase().contains(part) ) {
                    match = false;
                    break;
                }
            }

            if ( match ) {
                subentries.add(entryText);
            }
            if(subentries.size() *25 < 75)
            	StartList.setMaxHeight(subentries.size() *25);
            else
            	StartList.setMaxHeight(75);
        }
        StartList.setItems(subentries);

        	if(subentries.isEmpty() || subentries.get(0).equals(StartText.getText()))
        		StartList.setOpacity(0);

    }

    public void handleSearchByKeyDest(String oldVal, String newVal) {
        // If the number of characters in the text box is less than last time
        // it must be because the user pressed delete
        if ( oldVal != null && (newVal.length() < oldVal.length()) ) {
            // Restore the lists original set of entries
            // and start from the beginning
        	DestList.setItems(LocationOptions);
        }
        DestList.setOpacity(100);


        // Break out all of the parts of the search text
        // by splitting on white space
        String[] parts = newVal.toUpperCase().split(" ");

        // Filter out the entries that don't contain the entered text
        ObservableList<String> subentries = FXCollections.observableArrayList();
        String entryText = "";
        for ( Object entry: DestList.getItems() ) {
            boolean match = true;
            entryText = (String)entry;
            for ( String part: parts ) {
                // The entry needs to contain all portions of the
                // search string *but* in any order
                if ( ! entryText.toUpperCase().contains(part) ) {
                    match = false;
                    break;
                }
            }

            if ( match ) {
                subentries.add(entryText);
            }
            if(subentries.size() *25 < 75)
            	DestList.setMaxHeight(subentries.size() *25);
            else
            	DestList.setMaxHeight(75);
        }
        DestList.setItems(subentries);


        	if(subentries.isEmpty() || subentries.get(0).equals(DestText.getText()))
        		DestList.setOpacity(0);

    }

    private Parent createZoomPane(final Group group) {
	    final double SCALE_DELTA = 1.001;
	    final StackPane zoomPane = new StackPane();
	    scrollPane = new ScrollPane();
	    scrollPane.setPrefViewportWidth(800 + stageInitialWidthDifference);
  	  	scrollPane.setPrefViewportHeight(605 + stageInitialHeightDifference);

	    zoomPane.getChildren().add(group);


	    final Group scrollContent = new Group(zoomPane);
	    scrollPane.setContent(scrollContent);
	    //Removes Scroll bars
	    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
	    scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);

	    scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
	      @Override
	      public void changed(ObservableValue<? extends Bounds> observable,
	          Bounds oldValue, Bounds newValue) {
	    	  scrollPane.setPrefViewportWidth(800 + stageInitialWidthDifference);
	    	  scrollPane.setPrefViewportHeight(605 + stageInitialHeightDifference);
	    	  zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
	        
	      }
	    });
	    
	   

	    //scrollPane.setPrefViewportWidth(stageInitialWidth);
	    //scrollPane.setPrefViewportHeight(stageInitialHeight);
	    zoomPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
        		mouseYposition = event.getY();
        		mouseXposition = event.getX();
        		System.out.println(mouseXposition + ":" + mouseYposition);
        		
        	}
        });

	    zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
	      @Override
	      public void handle(ScrollEvent event) {
	        event.consume();
	        

	        if (event.getDeltaY() == 0) {
	          return;
	        }

	        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

	        if(scaleFactor < 1 && k > -1) {
	        	k--;
	        	//System.out.println(k);
		        // amount of scrolling in each direction in scrollContent coordinate
		        // units
		        Point2D scrollOffset = figureScrollOffset(scrollContent, scrollPane);
		        
		        for(int i = 0; i < 50; i++) {
		        	group.setScaleX(group.getScaleX() * scaleFactor);
			        group.setScaleY(group.getScaleY() * scaleFactor);
		        }

		        

		        // move viewport so that old center remains in the center after the
		        // scaling
		        repositionScroller(scrollContent, scrollPane, scaleFactor, scrollOffset);
	        }
	        if(scaleFactor > 1 && k < 8) {
	        	k++;
		        // amount of scrolling in each direction in scrollContent coordinate
		        // units
		        Point2D scrollOffset = figureScrollOffset(scrollContent, scrollPane);

		        for(int i = 0; i < 50; i++) {
		        	group.setScaleX(group.getScaleX() * scaleFactor);
		        	group.setScaleY(group.getScaleY() * scaleFactor);
		        }

		        // move viewport so that old center remains in the center after the
		        // scaling
		        repositionScroller(scrollContent, scrollPane, scaleFactor, scrollOffset);
	        }

	      }
	    });

	    // Panning via drag....
	    final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
	    scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
	      @Override
	      public void handle(MouseEvent event) {
	        lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
	      }
	    });

	    scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - lastMouseCoordinates.get().getX();
                double extraWidth = scrollContent.getLayoutBounds().getWidth() - scrollPane.getViewportBounds().getWidth();
                double deltaH = deltaX * (scrollPane.getHmax() - scrollPane.getHmin()) / extraWidth;
                double desiredH = scrollPane.getHvalue() - deltaH;
                scrollPane.setHvalue(Math.max(0, Math.min(scrollPane.getHmax(), desiredH)));

                double deltaY = event.getY() - lastMouseCoordinates.get().getY();
                double extraHeight = scrollContent.getLayoutBounds().getHeight() - scrollPane.getViewportBounds().getHeight();
                double deltaV = deltaY * (scrollPane.getHmax() - scrollPane.getHmin()) / extraHeight;
                double desiredV = scrollPane.getVvalue() - deltaV;
                scrollPane.setVvalue(Math.max(0, Math.min(scrollPane.getVmax(), desiredV)));
            }
        });

	    return scrollPane;
	}

    private void repositionScroller(Group scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
          double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
          double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
          scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
          scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
          double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
          double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
          scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
          scroller.setHvalue(scroller.getHmin());
        }
      }

    private Point2D figureScrollOffset(Group scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        	
        return new Point2D(mouseXposition, mouseYposition);
      }

    private void loadMap(Pane root, ImageView imageView){

    	k = 0; // Reset Zoom Variable
    	root.getChildren().remove(BackButton);
	    root.getChildren().remove(zoomPane);
	    root.getChildren().remove(canvas);
	    imageView.setScaleX(1);
	    imageView.setScaleY(1);
	   // NodePane.setScaleX(1);
	   // NodePane.setScaleY(1);

    	nodeList.clear();
   		//edgeList.clear();
        StartList.setOpacity(0);
        DestList.setOpacity(0);
        System.out.println("Graphs/Nodes/" + mapSelector.getValue() + ".json");
    	nodeList = JsonParser.getJsonContent("Graphs/Nodes/" + mapSelector.getValue() + ".json");
    	edgeListConversion = JsonParser.getJsonContentEdge("Graphs/Edges/" + mapSelector.getValue() + "Edges.json");
    	//edgeList = convertEdgeData(edgeListConversion);

    	//graph = createGraph(new Graph(), nodeList, edgeList);

    	File newMapFile = new File("CS3733_Graphics/" + (String) mapSelector.getValue() + ".png"); //MUST ADD png extension!
    	Image mapImage = new Image(newMapFile.toURI().toString());
        imageView.setImage(mapImage);

        //add node buttons to the screen and populates the drop down menus
        /*LocationOptions.clear();
        for(int i = 0; i < nodeList.size() - 1; i ++){
        	if(nodeList.get(i).getIsPlace())
        		LocationOptions.add(nodeList.get(i).getName());
        }
        StartList.setItems(LocationOptions);
        DestList.setItems(LocationOptions);*/

        //graph = createGraph(graph, nodeList, edgeList);
        NodePane.getChildren().clear();
        gc.clearRect(0, 0, 8000, 6000);
        NodePane.setPrefSize(2450, 1250);
        canvas = new Canvas(2450, 1250);
        gc = canvas.getGraphicsContext2D();

        switch (mapSelector.getValue()) {
    	case "CampusMap": 	canvas = new Canvas(3000,2000);
        					gc = canvas.getGraphicsContext2D();
        					NodePane.setPrefSize(3000, 2000);
    						imageView.setScaleX(0.75);
							imageView.setScaleY(0.75);
    						imageView.relocate(-1000, -600);
    						highLight(NodePane, imageView, root, keyText);
    						NodePane.setScaleX(0.75);
    						NodePane.setScaleY(0.75);
    						NodePane.relocate(-965, -643);
    						canvas.setScaleX(0.75);
    						canvas.setScaleY(0.75);
    						canvas.relocate(-965, -643);
    						buttonRescale = 1/0.75;
							break;
    	case "AKB": 		imageView.setScaleX(0.6536);
							imageView.setScaleY(0.6536);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6536);
							NodePane.setScaleY(0.6536);
							NodePane.relocate(-212, -88);
							canvas.setScaleX(0.6536);
							canvas.setScaleY(0.6536);
							canvas.relocate(-212, -88);
    						buttonRescale = 1/0.6536;
							break;
    	case "AK1":			imageView.setScaleX(0.5161);
							imageView.setScaleY(0.5161);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5161);
							NodePane.setScaleY(0.5161);
							NodePane.relocate(-218, -22);
							canvas.setScaleX(0.5161);
							canvas.setScaleY(0.5161);
							canvas.relocate(-218, -22);
    						buttonRescale = 1/0.5161;
							break;
    	case "AK2":			imageView.setScaleX(0.6706);
							imageView.setScaleY(0.6706);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6706);
							NodePane.setScaleY(0.6706);
							NodePane.relocate(-206, -57);
							canvas.setScaleX(0.6706);
							canvas.setScaleY(0.6706);
							canvas.relocate(-206, -57);
    						buttonRescale = 1/0.6706;
							break;
    	case "AK3":			imageView.setScaleX(0.6536);
							imageView.setScaleY(0.6536);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6536);
							NodePane.setScaleY(0.6536);
							NodePane.relocate(-212, -70);
							canvas.setScaleX(0.6536);
							canvas.setScaleY(0.6536);
							canvas.relocate(-212, -70);
    						buttonRescale = 1/0.6536;
							break;
    	case "BHB":			imageView.setScaleX(0.5427);
							imageView.setScaleY(0.5427);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5427);
							NodePane.setScaleY(0.5427);
							NodePane.relocate(-200, -90);
							canvas.setScaleX(0.5427);
							canvas.setScaleY(0.5427);
							canvas.relocate(-200, -90);
    						buttonRescale = 1/0.5427;
							break;
    	case "BH1":			imageView.setScaleX(0.5476);
							imageView.setScaleY(0.5476);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5476);
							NodePane.setScaleY(0.5476);
							NodePane.relocate(-220, -86);
							canvas.setScaleX(0.5476);
							canvas.setScaleY(0.5476);
							canvas.relocate(-220, -86);
    						buttonRescale = 1/0.5476;
							break;
    	case "BH2":			imageView.setScaleX(0.5438);
							imageView.setScaleY(0.5438);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5438);
							NodePane.setScaleY(0.5438);
							NodePane.relocate(-220, -99);
							canvas.setScaleX(0.5438);
							canvas.setScaleY(0.5438);
							canvas.relocate(-220, -99);
    						buttonRescale = 1/0.5438;
							break;
    	case "BH3":			imageView.setScaleX(0.5358);
							imageView.setScaleY(0.5358);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5358);
							NodePane.setScaleY(0.5358);
							NodePane.relocate(-220, -110);
							canvas.setScaleX(0.5358);
							canvas.setScaleY(0.5358);
							canvas.relocate(-220, -110);
    						buttonRescale = 1/0.5358;
							break;
    	case "CC1":			imageView.setScaleX(0.6107);
							imageView.setScaleY(0.6107);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6107);
							NodePane.setScaleY(0.6107);
							NodePane.relocate(-222, -59);
							canvas.setScaleX(0.6107);
							canvas.setScaleY(0.6107);
							canvas.relocate(-222, -59);
    						buttonRescale = 1/0.6107;
							break;
    	case "CC2":			imageView.setScaleX(0.6127);
							imageView.setScaleY(0.6127);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6127);
							NodePane.setScaleY(0.6127);
							NodePane.relocate(-222, -59);
							canvas.setScaleX(0.6127);
							canvas.setScaleY(0.6127);
							canvas.relocate(-222, -59);
    						buttonRescale = 1/0.6127;
							break;
    	case "CC3":			imageView.setScaleX(0.6061);
							imageView.setScaleY(0.6061);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6061);
							NodePane.setScaleY(0.6061);
							NodePane.relocate(-222, -59);
							canvas.setScaleX(0.6061);
							canvas.setScaleY(0.6061);
							canvas.relocate(-222, -59);
    						buttonRescale = 1/0.6061;
							break;
    	case "GLSB":		imageView.setScaleX(0.5686);
							imageView.setScaleY(0.5686);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5686);
							NodePane.setScaleY(0.5686);
							NodePane.relocate(-225, -42);
							canvas.setScaleX(0.5686);
							canvas.setScaleY(0.5686);
							canvas.relocate(-225, -42);
    						buttonRescale = 1/0.5686;
							break;
    	case "GLB":			imageView.setScaleX(0.5409);
    						imageView.setScaleY(0.5409);
    						imageView.relocate(0, 0);
    						NodePane.setScaleX(0.5409);
    						NodePane.setScaleY(0.5409);
    						NodePane.relocate(-225, -42);
    						canvas.setScaleX(0.5409);
    						canvas.setScaleY(0.5409);
    						canvas.relocate(-225, -42);
    						buttonRescale = 1/0.5409;
    						break;
    	case "GL1":			imageView.setScaleX(0.5678);
							imageView.setScaleY(0.5678);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5678);
							NodePane.setScaleY(0.5678);
							NodePane.relocate(-225, -42);
							canvas.setScaleX(0.5678);
							canvas.setScaleY(0.5678);
							canvas.relocate(-225, -42);
    						buttonRescale = 1/0.5678;
							break;
    	case "GL2":			imageView.setScaleX(0.5638);
							imageView.setScaleY(0.5638);
							imageView.relocate(-0, 0);
							NodePane.setScaleX(0.5638);
							NodePane.setScaleY(0.5638);
							NodePane.relocate(-225, -42);
							canvas.setScaleX(0.5638);
							canvas.setScaleY(0.5638);
							canvas.relocate(-225, -42);
    						buttonRescale = 1/0.5638;
							break;
    	case "GL3":			imageView.setScaleX(0.6119);
							imageView.setScaleY(0.6119);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6119);
							NodePane.setScaleY(0.6119);
							NodePane.relocate(-225, -42);
							canvas.setScaleX(0.6119);
							canvas.setScaleY(0.6119);
							canvas.relocate(-225, -42);
    						buttonRescale = 1/0.6119;
							break;
    	case "HHB":			imageView.setScaleX(0.5181);
    						imageView.setScaleY(0.5181);
    						imageView.relocate(0, 0);
    						NodePane.setScaleX(0.5181);
    						NodePane.setScaleY(0.5181);
    						NodePane.relocate(-360, -22);
    						canvas.setScaleX(0.5181);
    						canvas.setScaleY(0.5181);
    						canvas.relocate(-360, -22);
    						buttonRescale = 1/0.5181;
    						break;
    	case "HH1":			imageView.setScaleX(0.5535);
    						imageView.setScaleY(0.5535);
    						imageView.relocate(0, 0);
    						NodePane.setScaleX(0.5535);
    						NodePane.setScaleY(0.5535);
    						NodePane.relocate(-338, -37);
    						canvas.setScaleX(0.5535);
    						canvas.setScaleY(0.5535);
    						canvas.relocate(-338, -37);
    						buttonRescale = 1/0.5535;
    						break;
    	case "HH2":			imageView.setScaleX(0.6067);
							imageView.setScaleY(0.6067);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6067);
							NodePane.setScaleY(0.6067);
							NodePane.relocate(-298, -50);
							canvas.setScaleX(0.6067);
							canvas.setScaleY(0.6067);
							canvas.relocate(-298, -50);
    						buttonRescale = 1/0.6067;
							break;
    	case "HH3":			imageView.setScaleX(0.5917);
							imageView.setScaleY(0.5917);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5917);
							NodePane.setScaleY(0.5917);
							NodePane.relocate(-310, -48);
							canvas.setScaleX(0.5917);
							canvas.setScaleY(0.5917);
							canvas.relocate(-310, -48);
    						buttonRescale = 1/0.5917;
							break;
    	case "HHAPT":		imageView.setScaleX(0.8197);
							imageView.setScaleY(0.8197);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.8197);
							NodePane.setScaleY(0.8197);
							NodePane.relocate(-130, -50);
							canvas.setScaleX(0.8197);
							canvas.setScaleY(0.8197);
							canvas.relocate(-130, -50);
    						buttonRescale = 1/0.8197;
							break;
    	case "HHGAR":		imageView.setScaleX(0.8172);
    						imageView.setScaleY(0.8172);
    						imageView.relocate(0, 0);
    						NodePane.setScaleX(0.8172);
    						NodePane.setScaleY(0.8172);
    						NodePane.relocate(-133, -53);
    						canvas.setScaleX(0.8172);
    						canvas.setScaleY(0.8172);
    						canvas.relocate(-133, -53);
    						buttonRescale = 1/0.8172;
    						break;
    	case "PC1":			imageView.setScaleX(0.6764);
							imageView.setScaleY(0.6764);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6764);
							NodePane.setScaleY(0.6764);
							NodePane.relocate(-208, -58);
							canvas.setScaleX(0.6764);
							canvas.setScaleY(0.6764);
							canvas.relocate(-208, -58);
    						buttonRescale = 1/0.6764;
							break;
    	case "PC2":			imageView.setScaleX(0.6006);
							imageView.setScaleY(0.6006);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.6006);
							NodePane.setScaleY(0.6006);
							NodePane.relocate(-222, -48);
							canvas.setScaleX(0.6006);
							canvas.setScaleY(0.6006);
							canvas.relocate(-222, -48);
    						buttonRescale = 1/0.6006;
							break;
    	case "SHB":			imageView.setScaleX(0.5464);
							imageView.setScaleY(0.5464);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5464);
							NodePane.setScaleY(0.5464);
							NodePane.relocate(-224, -88);
							canvas.setScaleX(0.5464);
							canvas.setScaleY(0.5464);
							canvas.relocate(-224, -88);
    						buttonRescale = 1/0.5464;
							break;
    	case "SH1":			imageView.setScaleX(0.5583);
							imageView.setScaleY(0.5583);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5583);
							NodePane.setScaleY(0.5583);
							NodePane.relocate(-224, -82);
							canvas.setScaleX(0.5583);
							canvas.setScaleY(0.5583);
							canvas.relocate(-224, -82);
    						buttonRescale = 1/0.5583;
							break;
    	case "SH2":			imageView.setScaleX(0.5556);
							imageView.setScaleY(0.5556);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5556);
							NodePane.setScaleY(0.5556);
							NodePane.relocate(-224, -86);
							canvas.setScaleX(0.5556);
							canvas.setScaleY(0.5556);
							canvas.relocate(-224, -86);
    						buttonRescale = 1/0.5556;
							break;
    	case "SH3":			imageView.setScaleX(0.5544);
							imageView.setScaleY(0.5544);
							imageView.relocate(0, 0);
							NodePane.setScaleX(0.5544);
							NodePane.setScaleY(0.5544);
							NodePane.relocate(-224, -83);
							canvas.setScaleX(0.5544);
							canvas.setScaleY(0.5544);
							canvas.relocate(-224, -83);
    						buttonRescale = 1/0.5544;
							break;
		}
        gc.clearRect(0, 0, 8000, 6000);
        drawNodes(nodeList, NodePane,root, StartText, DestText,imageView);
        
        	
        final Group group = new Group(imageView, canvas, NodePane);
        zoomPane = createZoomPane(group);
	    root.getChildren().add(zoomPane);

    }

    public void highLight(Pane NodePane, ImageView imageView, Pane root, Text keyText){

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.GRAY);

        Color BuildingName = new Color(1,0,0,1);
        Color key = new Color(1,1,1,0.5);
        Polygon cc = new Polygon();
        double xOffset = 0.0;
        double yOffset = 0.0;
        cc.getPoints().addAll(new Double[]{
        	    1261.0 - xOffset, 649.0 - yOffset,
        	    1272.0 - xOffset, 656.0 - yOffset,
        	    1273.0 - xOffset, 668.0 - yOffset,
        	    1267.0 - xOffset, 677.0 - yOffset,
        	    1254.0 - xOffset, 680.0 - yOffset,
        	    1245.0 - xOffset, 673.0 - yOffset,
        	    1242.0 - xOffset, 662.0 - yOffset,
        	    1239.0 - xOffset, 677.0 - yOffset,
        	    1175.0 - xOffset, 667.0 - yOffset,
        	    1184.0 - xOffset, 617.0 - yOffset,
        	    1197.0 - xOffset, 620.0 - yOffset,
        	    1213.0 - xOffset, 630.0 - yOffset,
        	    1220.0 - xOffset, 623.0 - yOffset,
        	    1214.0 - xOffset, 617.0 - yOffset,
        	    1223.0 - xOffset, 604.0 - yOffset,
        	    1219.0 - xOffset, 601.0 - yOffset,
        	    1218.0 - xOffset, 591.0 - yOffset,
        	    1222.0 - xOffset, 582.0 - yOffset,
        	    1234.0 - xOffset, 580.0 - yOffset,
        	    1238.0 - xOffset, 584.0 - yOffset,
        	    1248.0 - xOffset, 573.0 - yOffset,
        	    1235.0 - xOffset, 565.0 - yOffset,
        	    1249.0 - xOffset, 543.0 - yOffset,
        	    1252.0 - xOffset, 537.0 - yOffset,
        	    1302.0 - xOffset, 546.0 - yOffset,
        	    1285.0 - xOffset, 652.0 - yOffset});

        cc.setFill(Color.TRANSPARENT);

        cc.setStroke(Color.TRANSPARENT);
        cc.setStrokeWidth(1.0);
        cc.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                cc.setFill(new Color(1.0, 1.0, 0.0, 0.2));
                keyText.setText("Campus Center");
                keyText.setFill(BuildingName);
                BuildingRolledOver = CampusCenter;
                pause.play(); //At the end of play, pause runs a method to show layered maps
               
        	}
        });
        cc.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
                cc.setFill(Color.TRANSPARENT);
                BuildingRolledOver = null;
        	}
        });
        
        NodePane.getChildren().add(cc);


        Polygon olin = new Polygon();
        olin.getPoints().addAll(new Double[]{

        	    1334.0 - xOffset, 510.0 - yOffset,
        	    1373.0 - xOffset, 516.0 - yOffset,
        	    1350.0 - xOffset, 662.0 - yOffset,
        	    1311.0 - xOffset, 656.0 - yOffset});

        olin.setFill(Color.TRANSPARENT);

        olin.setStroke(Color.TRANSPARENT);
        olin.setStrokeWidth(1.0);
        olin.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Olin Hall");
                keyText.setFill(BuildingName);
                //BuildingRolledOver = olinHall;
                //olin.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		//System.out.println("I'm here");
        	}
        });
        olin.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
                olin.setFill(Color.TRANSPARENT);
        	}
        });
        olin.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			//getMapSelector(OlinHall, root, zoomPane, imageView);
        		}
        	}
        });

        NodePane.getChildren().add(olin);

        Polygon stratton = new Polygon();
        stratton.getPoints().addAll(new Double[]{

        	    1377.0 - xOffset, 813.0 - yOffset,
        	    1416.0 - xOffset, 820.0 - yOffset,
        	    1403.0 - xOffset, 903.0 - yOffset,
        	    1363.0 - xOffset, 896.0 - yOffset});

        stratton.setFill(Color.TRANSPARENT);

        stratton.setStroke(Color.TRANSPARENT);
        stratton.setStrokeWidth(1.0);
        stratton.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		stratton.setFill(new Color(1.0, 1.0, 0.0, 0.2));
                keyText.setText("Stratton Hall");
                keyText.setFill(BuildingName);
                BuildingRolledOver = StrattonHall;
        		//System.out.println("I'm here");
                getMapSelector(StrattonHall, root, imageView);// <- Not working for all buildings
        	}
        });
        stratton.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
        		stratton.setFill(Color.TRANSPARENT);
        	}
        });
        stratton.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(StrattonHall, root, imageView);
        		}
        	}
        });

        NodePane.getChildren().add(stratton);

        Polygon library = new Polygon();
        library.getPoints().addAll(new Double[]{

        	    1607.0 - xOffset, 712.0 - yOffset,
        	    1667.0 - xOffset, 725.0 - yOffset,
        	    1664.0 - xOffset, 742.0 - yOffset,
        	    1661.0 - xOffset, 742.0 - yOffset,
        	    1660.0 - xOffset, 769.0 - yOffset,
        	    1658.0 - xOffset, 782.0 - yOffset,
        	    1655.0 - xOffset, 799.0 - yOffset,
        	    1645.0 - xOffset, 824.0 - yOffset,
        	    1648.0 - xOffset, 825.0 - yOffset,
        	    1644.0 - xOffset, 841.0 - yOffset,
        	    1584.0 - xOffset, 829.0 - yOffset,
        	    1585.0 - xOffset, 794.0 - yOffset,
        	    1588.0 - xOffset, 773.0 - yOffset,
        	    1593.0 - xOffset, 750.0 - yOffset});

        library.setFill(Color.TRANSPARENT);

        library.setStroke(Color.TRANSPARENT);
        library.setStrokeWidth(1.0);
        library.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Gordon Library");
                keyText.setFill(BuildingName);
        		library.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		BuildingRolledOver = GordonLibrary;
        		getMapSelector(GordonLibrary, root, imageView);
        	}
        });
        library.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
        		library.setFill(Color.TRANSPARENT);
        	}
        });
        library.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(GordonLibrary, root, imageView);
        		}
        	}
        });


        NodePane.getChildren().add(library);


        Polygon ak = new Polygon();
        ak.getPoints().addAll(new Double[]{

        	    1471.0 - xOffset, 439.0 - yOffset,
        	    1508.0 - xOffset, 460.0 - yOffset,
        	    1491.0 - xOffset, 490.0 - yOffset,
        	    1540.0 - xOffset, 518.0 - yOffset,
        	    1557.0 - xOffset, 489.0 - yOffset,
        	    1594.0 - xOffset, 510.0 - yOffset,
        	    1553.0 - xOffset, 581.0 - yOffset,
        	    1530.0 - xOffset, 569.0 - yOffset,
        	    1522.0 - xOffset, 582.0 - yOffset,
        	    1445.0 - xOffset, 537.0 - yOffset,
        	    1452.0 - xOffset, 537.0 - yOffset,
        	    1452.0 - xOffset, 525.0 - yOffset,
        	    1429.0 - xOffset, 512.0 - yOffset});

        ak.setFill(Color.TRANSPARENT);

        ak.setStroke(Color.TRANSPARENT);
        ak.setStrokeWidth(1.0);
        ak.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Atwater Kent");
                keyText.setFill(BuildingName);
        		ak.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		BuildingRolledOver = AtwaterKent;
        		getMapSelector(AtwaterKent, root, imageView);        	}
        });
        ak.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
        		ak.setFill(Color.TRANSPARENT);
        	}
        });
        ak.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(AtwaterKent, root, imageView);
        		}
        	}
        });


        NodePane.getChildren().add(ak);

        Polygon cdc = new Polygon();
        cdc.getPoints().addAll(new Double[]{

        	    1391.0 - xOffset, 732.0 - yOffset,
        	    1430.0 - xOffset, 738.0 - yOffset,
        	    1420.0 - xOffset, 804.0 - yOffset,
        	    1380.0 - xOffset, 797.0 - yOffset});

        cdc.setFill(Color.TRANSPARENT);

        cdc.setStroke(Color.TRANSPARENT);
        cdc.setStrokeWidth(1.0);
        cdc.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Project Center");
                keyText.setFill(BuildingName);
        		cdc.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		BuildingRolledOver = ProjectCenter;
        		getMapSelector(ProjectCenter, root, imageView);        	}
        });
        cdc.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
        		cdc.setFill(Color.TRANSPARENT);
        	}
        });
        cdc.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(ProjectCenter, root, imageView);
        		}
        	}
        });

        NodePane.getChildren().add(cdc);

        Polygon higginsHouse = new Polygon();
        higginsHouse.getPoints().addAll(new Double[]{

        	    1130.0 - xOffset, 435.0 - yOffset,
        	    1154.0 - xOffset, 451.0 - yOffset,
        	    1159.0 - xOffset, 443.0 - yOffset,
        	    1165.0 - xOffset, 446.0 - yOffset,
        	    1161.0 - xOffset, 441.0 - yOffset,
        	    1165.0 - xOffset, 435.0 - yOffset,
        	    1172.0 - xOffset, 435.0 - yOffset,
        	    1176.0 - xOffset, 433.0 - yOffset,
        	    1197.0 - xOffset, 448.0 - yOffset,
        	    1209.0 - xOffset, 431.0 - yOffset,
        	    1225.0 - xOffset, 441.0 - yOffset,
        	    1212.0 - xOffset, 459.0 - yOffset,
        	    1200.0 - xOffset, 452.0 - yOffset,
        	    1192.0 - xOffset, 464.0 - yOffset,
        	    1196.0 - xOffset, 466.0 - yOffset,
        	    1189.0 - xOffset, 476.0 - yOffset,
        	    1185.0 - xOffset, 473.0 - yOffset,
        	    1163.0 - xOffset, 505.0 - yOffset,
        	    1137.0 - xOffset, 487.0 - yOffset,
        	    1149.0 - xOffset, 471.0 - yOffset,
        	    1120.0 - xOffset, 450.0 - yOffset});

        higginsHouse.setFill(Color.TRANSPARENT);

        higginsHouse.setStroke(Color.TRANSPARENT);
        higginsHouse.setStrokeWidth(1.0);
        higginsHouse.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Higgins House");
                keyText.setFill(BuildingName);
        		higginsHouse.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		BuildingRolledOver = HigginsHouse;
        		getMapSelector(HigginsHouse, root, imageView);        	}
        });
        higginsHouse.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
        		higginsHouse.setFill(Color.TRANSPARENT);
        	}
        });
        higginsHouse.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(HigginsHouse, root, imageView);
        		}
        	}
        });


        NodePane.getChildren().add(higginsHouse);
        
        
        Polygon higginsHouseGAR = new Polygon();
        higginsHouseGAR.getPoints().addAll(new Double[]{

        	    1231.0 - xOffset, 404.0 - yOffset,
        	    1216.0 - xOffset, 394.0 - yOffset,
        	    1236.0 - xOffset, 367.0 - yOffset,
        	    1251.0 - xOffset, 377.0 - yOffset});

        higginsHouseGAR.setFill(Color.TRANSPARENT);

        higginsHouseGAR.setStroke(Color.TRANSPARENT);
        higginsHouseGAR.setStrokeWidth(1.0);
        higginsHouseGAR.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Higgins House Garage");
                keyText.setFill(BuildingName);
                higginsHouseGAR.setFill(new Color(1.0, 1.0, 0.0, 0.2));
                getMapSelector(HigginsHouseGarage, root, imageView);        	}
        });
        higginsHouseGAR.setOnMouseExited(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText(" ");
                keyText.setFill(key);
                higginsHouseGAR.setFill(Color.TRANSPARENT);
        	}
        });
        higginsHouseGAR.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(HigginsHouseGarage, root, imageView);
        		}
        	}
        });


        NodePane.getChildren().add(higginsHouseGAR);



        Polygon boyntonHall = new Polygon();
        boyntonHall.getPoints().addAll(new Double[]{

        	    1406.0 - xOffset, 932.0 - yOffset,
        	    1435.0 - xOffset, 937.0 - yOffset,
        	    1434.0 - xOffset, 943.0 - yOffset,
        	    1501.0 - xOffset, 954.0 - yOffset,
        	    1497.0 - xOffset, 984.0 - yOffset,
        	    1492.0 - xOffset, 984.0 - yOffset,
        	    1491.0 - xOffset, 988.0 - yOffset,
        	    1480.0 - xOffset, 987.0 - yOffset,
        	    1480.0 - xOffset, 981.0 - yOffset,
        	    1429.0 - xOffset, 973.0 - yOffset,
        	    1428.0 - xOffset, 980.0 - yOffset,
        	    1399.0 - xOffset, 975.0 - yOffset});

        boyntonHall.setFill(Color.TRANSPARENT);

        boyntonHall.setStroke(Color.TRANSPARENT);
        boyntonHall.setStrokeWidth(1.0);
        boyntonHall.setOnMouseEntered(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
                keyText.setText("Boynton Hall");
                keyText.setFill(BuildingName);

        		boyntonHall.setFill(new Color(1.0, 1.0, 0.0, 0.2));
        		BuildingRolledOver = BoyntonHall;
        		getMapSelector(BoyntonHall, root, imageView);        	}
        });
        boyntonHall.setOnMouseExited(new EventHandler <MouseEvent>(){

            public void handle (MouseEvent event){

                keyText.setText(" ");
                keyText.relocate(850,650);
                root.getChildren().remove(keyText);
                root.getChildren().add(keyText);

                keyText.setFill(key);
                boyntonHall.setFill(Color.TRANSPARENT);
        	}
        });
        boyntonHall.setOnMouseClicked(new EventHandler <MouseEvent>(){
        	public void handle (MouseEvent event){
        		if (event.isStillSincePress()) {
        			getMapSelector(BoyntonHall, root, imageView);
        		}
        	}
        });

        NodePane.getChildren().add(boyntonHall);
        
        pause.setOnFinished(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
            	if(BuildingRolledOver.equals(CampusCenter))
    		    	getMapSelector(CampusCenter, root, imageView);
            }
        });
    }
    
    public static double round(double value, int places) {
        if (places < 0) {
        	throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

