package miniproject;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;	
import javafx.event.ActionEvent; 

import java.util.Timer;
import java.util.TimerTask;

public class BusTransitGUI extends Application {
    private static final int UPDATE_INTERVAL = 5000; // Update every 5 seconds

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bus Transit Management");

        // Create a VBox for the main layout
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);

        // Create and add a label for Date & Time
        Label dateTimeLabel = new Label("Date & Time");
        mainLayout.getChildren().add(dateTimeLabel);

        // Create instances of existing classes - develop further
        BusRoute route1 = createSampleRoute("Bus Route 1", "Driver A", "Stop 1", "Stop 2", "Stop 3");
        BusRoute route2 = createSampleRoute("Bus Route 2", "Driver B", "Stop 1", "Stop 2", "Stop 3");

        // Create and add UI components for each bus route
        addBusRouteUI(mainLayout, route1);
        addBusRouteUI(mainLayout, route2);

        // Set up the scene
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();

        // Set up a timer to simulate route updates
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update the UI (simulate route progression)
                updateRouteUI(route1);
                updateRouteUI(route2);
            }
        }, 0, UPDATE_INTERVAL);
    }

    private BusRoute createSampleRoute(String title, String driverName, String... stopNames) {
        Driver driver = new Driver(driverName);
        Stop[] stops = new Stop[stopNames.length];
        for (int i = 0; i < stopNames.length; i++) {
            stops[i] = new Stop(stopNames[i]);
        }
        return new BusRoute(title, driver, stops);
    }

    private void addBusRouteUI(VBox mainLayout, BusRoute route) {
        // Create an HBox for each bus route
        HBox routeLayout = new HBox();
        routeLayout.setAlignment(Pos.CENTER);
        routeLayout.setSpacing(10);

        // Create and add a label for the bus route title
        Label routeTitleLabel = new Label(route.getTitle());
        routeLayout.getChildren().add(routeTitleLabel);

        // Create a toggle group for radio buttons
        ToggleGroup toggleGroup = new ToggleGroup();

        // Create and add radio buttons for each stop in the route
        for (Stop stop : route.getStops()) {
            RadioButton radioButton = new RadioButton(stop.getName());
            radioButton.setToggleGroup(toggleGroup);
            routeLayout.getChildren().add(radioButton);
        }

        // Add the route layout to the main layout
        mainLayout.getChildren().add(routeLayout);

        // Set up an event handler for clicking on the route title
        routeTitleLabel.setOnMouseClicked(event -> {
            // Show side menu with route information (develop further) 
            showSideMenu(route);
        });
    }

    private void showSideMenu(BusRoute route) {
        // Develop further to show a side menu
        System.out.println("Bus Route Title: " + route.getTitle());
        System.out.println("Driver: " + route.getDriver().getName());
        System.out.println("Departed: " + route.getDepartedTime());
        System.out.println("Status: " + route.getStatus());
    }

    private void updateRouteUI(BusRoute route) {
        // Simulate updating the route UI (develop further)
        System.out.println("Updating route: " + route.getTitle());
    }

    public static void main(String[] args) {
        launch(args);
        //instances of other classes
    }

}