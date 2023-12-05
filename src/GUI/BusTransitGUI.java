package src.GUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;	
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;

import src.classes.BusRoute;
import src.classes.Stop;

import javafx.util.Duration;
import src.util.GUIUtil;

import java.sql.Date;
import java.sql.Timestamp;

public class BusTransitGUI extends Application {

	private static final int UPDATE_INTERVAL = 5;
	
	private Date date;
	private HBox mainLayout;
	private VBox leftPanel;
	private VBox rightPanel;
	private String defaultRouteLabel = "Route: ";
	private String defaultDriverLabel = "Driver: ";
	private String defaultDepartedLabel = "Departed: ";
	private String defaultStatusLabel = "Status: ";
	

    @Override
    public void start(Stage primaryStage) {
    	GUIUtil guiUtil = new GUIUtil();
    	
        primaryStage.setTitle("Bus Transit Management");
        mainLayout = new HBox();
        
        leftPanel = new VBox();
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-border-color: black;");
        leftPanel.setSpacing(5);
        mainLayout.getChildren().add(leftPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        
        rightPanel = new VBox();
        rightPanel.setStyle("-fx-border-color: black;");
        rightPanel.setSpacing(20);
        rightPanel.setPrefWidth(200);
        mainLayout.getChildren().add(rightPanel);
        
        date = new Date((new Timestamp(System.currentTimeMillis()).getTime()));
        leftPanel.getChildren().add(new Label(date.toString()));
        
        // Add default label strings to UI
        rightPanel.getChildren().add(new Label(defaultRouteLabel));
        rightPanel.getChildren().add(new Label(defaultDriverLabel));
        rightPanel.getChildren().add(new Label(defaultDepartedLabel));
        rightPanel.getChildren().add(new Label(defaultStatusLabel));

        // Set up the scene and show the stage
        Scene scene = new Scene(mainLayout, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_INTERVAL), event -> {
        	ArrayList<BusRoute> newBusRoutes = guiUtil.getBusRoutes(date);
        	if (guiUtil.updates(newBusRoutes)) {
        		updateBusRoutes(newBusRoutes);
        	}
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    int i = 0;
    
    public void updateBusRoutes(ArrayList<BusRoute> updatedBusRoutes) {
	   	
	   	if(leftPanel != null) {
	    	leftPanel.getChildren().clear();
	    	leftPanel.getChildren().add(new Label(date.toString()));
	   	}
		
	   	for (BusRoute busRoute : updatedBusRoutes) {
	   		// Create and add a label for the bus route title
	   		Hyperlink routeTitleLabel = new Hyperlink(busRoute.getRoute().getName());
	   		routeTitleLabel.setOnAction(new EventHandler<ActionEvent>() {
	   		    @Override
	   		    public void handle(ActionEvent e) {
	   		        updateSideMenu(busRoute);
	   		    }
	   		});
	   		
	   		leftPanel.getChildren().add(routeTitleLabel);
	
	   		// Create an HBox for the stops in the route
	   		HBox stopsLayout = new HBox();
	   		stopsLayout.setAlignment(Pos.CENTER);
	   		stopsLayout.setSpacing(10);
	   		
	   		// Create and add labels for each stop in the route
	   		for (int i = 0; i < busRoute.getRoute().getStops().size(); i++) {
	   			Stop stop = busRoute.getRoute().getStops().get(i);
	   			Label stopLabel = new Label(stop.getLocation());
	   			if(busRoute.getCurrentStop().equals(stop)) {
	   				stopLabel.setTextFill(Color.BLUE);
	   			}
	   			stopsLayout.getChildren().add(stopLabel);
	   			if (i < busRoute.getRoute().getStops().size() - 1) {
	   				Label hLine = new Label("―――");
	   				stopsLayout.getChildren().add(hLine);
	   			}
	   			
	   		}
	   		leftPanel.getChildren().add(stopsLayout);
    	}
    }

    private void updateSideMenu(BusRoute busRoute) {
    	rightPanel.getChildren().clear();
        rightPanel.getChildren().add(new Label(defaultRouteLabel + " " + busRoute.getRoute().getName()));
        rightPanel.getChildren().add(new Label(defaultDriverLabel + " " + busRoute.getBus().getDriver().getName()));
        rightPanel.getChildren().add(new Label(defaultDepartedLabel + " " + busRoute.getDepartureTime()));
        rightPanel.getChildren().add(new Label(defaultStatusLabel + " " + busRoute.getOnTimeStatus()));
    	
    }

    public static void main(String[] args) {
        launch(args);
    }

}