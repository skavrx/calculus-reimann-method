package com.skavrx.calc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.skavrx.calc.Area.Type;
import com.sun.javafx.charts.Legend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Main class for the application
 * 
 * @author skavrx
 *
 */
public class Main extends Application {

	// Defining the variables so that they can be used in different methods in the
	// class
	private BorderPane root;
	// Area label font size
	private final int SIZE = 60;
	// My utility class for defining the area of the function
	private Area area;

	// the xAxis object for the line chart
	private NumberAxis xAxis;
	private NumberAxis yAxis;
	private LineChart<Number, Number> ac;

	// This method is what gets run when the application starts
	@Override
	public void start(Stage primaryStage) {
		initUI(primaryStage);
	}

	/**
	 * The initialization of the UI (I also dumped like everything else in here to
	 * no confuse myself.
	 * 
	 * @param primaryStage default
	 */
	private void initUI(Stage primaryStage) {

		// Try catch method so if there is any error starting up, a dialogue pops up
		try {
			// Using my utility class to define a default function that will be shown when
			// the UI initializes
			area = new Area(Area.Type.DEFAULT, "x^3", "-5", "5", "10");
			root = new BorderPane();

			// Initializing the objects for the UI
			xAxis = new NumberAxis();
			yAxis = new NumberAxis();

			ac = new LineChart<Number, Number>(xAxis, yAxis);
			ac.setLegendVisible(false);
			ac.setCreateSymbols(false);

			// Setting debug to false to prevent any debugging messages from appearing
			area.debug(false);

			// Setting the defaults for the state such as the title that will be displayed
			// along with the default height and width and the minimum size that it can be
			// resized to
			primaryStage.setTitle(area.getFunction());
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(800);
			primaryStage.setHeight(600);
			primaryStage.setWidth(800);

			// Defining menu options for the menu bar
			final Menu menu1 = new Menu("File");
			final Menu menu3 = new Menu("Help");

			// Defining menu items for each of the menus on the menu bar
			MenuItem m1 = new MenuItem("Info");
			MenuItem m3 = new MenuItem("Github");

			MenuItem m4 = new MenuItem("Save image...");
			MenuItem m5 = new MenuItem("Exit");

			// defining an event for the information button
			EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("CRAC Information");
					alert.setHeaderText("CRAC Information");
					alert.setContentText(
							"Written by skavrx (John Monsen)\nWebsite: https://skavrx.com\nContact: contact@skavrx.com\n\nMIT License 2019");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
					alert.showAndWait();
				}
			};

			// defining an event for the exit button under file
			EventHandler<ActionEvent> exitEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Exit");
					// alert.setHeaderText("Close Calculator?");
					alert.setContentText("Are you sure you want exit?");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
					ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
					ButtonType noButton = new ButtonType("No", ButtonData.NO);
					alert.getButtonTypes().setAll(okButton, noButton);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.orElse(noButton) == okButton) {
						Platform.exit();
					}
				}
			};

			// defining an event for the github button
			EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Github");
					alert.setHeaderText("Github Repo");
					alert.setContentText(
							"Are you sure you want to go to Github?\n(This will open in your default brower.)");
					ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
					ButtonType noButton = new ButtonType("No", ButtonData.NO);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
					alert.getButtonTypes().setAll(okButton, noButton);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.orElse(noButton) == okButton) {
						getHostServices().showDocument("https://github.com/skavrx/calculus-reimann-method");
					}
				}
			};

			// defining the event for the save graph as image button
			EventHandler<ActionEvent> event3 = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e1) {

					WritableImage image = ac.snapshot(new SnapshotParameters(), null);

					FileChooser chooser = new FileChooser();
					chooser.setInitialFileName("graph");
					chooser.getExtensionFilters().addAll(new ExtensionFilter("PNG", new String[] { "*.png" }));
					File file = chooser.showSaveDialog(null);

					if (file != null) {
						try {
							ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};

			// Defining the label for the bottom of the borderpane
			Label lbl = new Label("Area");
			lbl.setAlignment(Pos.BASELINE_CENTER);
			lbl.setPrefHeight(SIZE);

			lbl.prefWidthProperty().bind(root.widthProperty());
			lbl.setStyle("-fx-border-style: dotted; -fx-border-width: 1 0 0 0;"
					+ "-fx-border-color: gray; -fx-font-weight: bold; -fx-font: 24 arial;");

			// add events to the buttons
			m1.setOnAction(event);
			m3.setOnAction(event2);
			m4.setOnAction(event3);
			m5.setOnAction(exitEvent);

			// add menu items to menu
			menu3.getItems().add(m1);
			menu3.getItems().add(m3);

			menu1.getItems().add(m4);
			menu1.getItems().add(m5);

			MenuBar menuBar = new MenuBar();
			menuBar.getMenus().addAll(menu1, menu3);

			// Adding the menu bar and the label to the root pane
			root.setTop(menuBar);
			root.setBottom(lbl);

			// Creating a box for the buttons and input boxes to go into
			VBox info = new VBox(5);
			info.setAlignment(Pos.CENTER_RIGHT);

			// defining the horizontal boxes to be put into the vertical box
			HBox buttonbox = new HBox(5);
			HBox buttonBoxFunction = new HBox(5);
			HBox buttonBoxLowerBound = new HBox(5);
			HBox buttonBoxUpperBound = new HBox(5);
			HBox buttonBoxRectangles = new HBox(5);
			HBox areaInfo = new HBox(5);
			HBox updateBox = new HBox(5);

			// Setting the boxes alignment in the border pane and adding padding
			buttonbox.setPadding(new Insets(5));
			buttonbox.setAlignment(Pos.BASELINE_CENTER);

			buttonBoxFunction.setPadding(new Insets(5));
			buttonBoxFunction.setAlignment(Pos.BASELINE_RIGHT);

			buttonBoxLowerBound.setPadding(new Insets(5));
			buttonBoxLowerBound.setAlignment(Pos.BASELINE_RIGHT);

			buttonBoxUpperBound.setPadding(new Insets(5));
			buttonBoxUpperBound.setAlignment(Pos.BASELINE_RIGHT);

			buttonBoxRectangles.setPadding(new Insets(5));
			buttonBoxRectangles.setAlignment(Pos.BASELINE_RIGHT);

			updateBox.setPadding(new Insets(5));
			updateBox.setAlignment(Pos.BASELINE_CENTER);

			areaInfo.setPadding(new Insets(5));
			areaInfo.setAlignment(Pos.BASELINE_LEFT);

			// Creating the buttons for the methods
			ToggleButton leftBtn = new ToggleButton();
			leftBtn.setText("Left");
			ToggleButton rightBtn = new ToggleButton();
			rightBtn.setText("Right");
			ToggleButton midBtn = new ToggleButton();
			midBtn.setText("Midpoint");
			ToggleButton shapeBtn = new ToggleButton();
			shapeBtn.setText("Shape");

			// The function input box
			Label functionLabel = new Label("Function");
			TextField functionField = new TextField();
			functionField.setText(area.getFunction());
			functionField.setPromptText("Function");
			functionField.setTooltip(new Tooltip("Use x as the variable"));
			Button functionButton = new Button("Set");

			// Lower bound input box
			Label lowerBoundLabel = new Label("Lower Bound");
			TextField lowerBoundField = new TextField();
			lowerBoundField.setText(String.valueOf(area.getLower()));
			lowerBoundField.setPromptText("Lower Bound");
			lowerBoundField.setTooltip(new Tooltip("Cannot be greater than upper bound"));
			Button lowerBoundButton = new Button("Set");

			// Upper bound input box
			Label upperBoundLabel = new Label("Upper Bound");
			TextField upperBoundField = new TextField();
			upperBoundField.setText(String.valueOf(area.getUpper()));
			upperBoundField.setPromptText("Upper Bound");
			upperBoundField.setTooltip(new Tooltip("Cannot be lower than lower bound"));
			Button upperBoundButton = new Button("Set");

			// Rectangles input box
			Label rectangleLabel = new Label("Rectangles");
			TextField rectangleField = new TextField();
			rectangleField.setText(String.valueOf(area.getRect()));
			rectangleField.setPromptText("Rectangles");
			rectangleField.setTooltip(new Tooltip("Must be a whole number"));
			Button rectangleButton = new Button("Set");

			// adding all the buttons, labels and inputs to the boxes
			buttonbox.getChildren().addAll(leftBtn, rightBtn, midBtn, shapeBtn);
			buttonBoxFunction.getChildren().addAll(functionLabel, functionField, functionButton);
			buttonBoxLowerBound.getChildren().addAll(lowerBoundLabel, lowerBoundField, lowerBoundButton);
			buttonBoxUpperBound.getChildren().addAll(upperBoundLabel, upperBoundField, upperBoundButton);
			buttonBoxRectangles.getChildren().addAll(rectangleLabel, rectangleField, rectangleButton);

			// Label selectedMethod = new Label("Selected Method: " +
			// area.getType().toString());
			// areaInfo.getChildren().addAll(selectedMethod);

			// adding the button boxes to the root box
			Button setAllButton = new Button("Update All");
			updateBox.getChildren().addAll(setAllButton);

			info.getChildren().addAll(buttonbox, buttonBoxFunction, buttonBoxLowerBound, buttonBoxUpperBound,
					buttonBoxRectangles, updateBox);

			// setting the righthand borderpane to the info box
			root.setRight(info);

			// setting the center box to the linechart object
			root.setCenter(ac);

			// the event for when the method button is pushed
			EventHandler<ActionEvent> selectionEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						for (Type t : Type.values())
							if (e.getTarget().toString().toLowerCase().contains(t.name().toLowerCase())) {
								area.setType(t);
								if (!e.getTarget().toString().toLowerCase().contains(leftBtn.getText().toLowerCase()))
									leftBtn.setSelected(false);

								if (!e.getTarget().toString().toLowerCase().contains(rightBtn.getText().toLowerCase()))
									rightBtn.setSelected(false);

								if (!e.getTarget().toString().toLowerCase().contains(midBtn.getText().toLowerCase()))
									midBtn.setSelected(false);

								if (!e.getTarget().toString().toLowerCase().contains(shapeBtn.getText().toLowerCase()))
									shapeBtn.setSelected(false);
							}

						updateLineChart();
						lbl.setText("Area: " + area.getArea() + " sq. u.");

					} catch (Exception e2) {
						Alert alert = new Alert(AlertType.ERROR, "Error with function!");
						e2.printStackTrace();
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
						alert.showAndWait();
					}
				}
			};

			// setting the event for the buttons
			leftBtn.setOnAction(selectionEvent);
			rightBtn.setOnAction(selectionEvent);
			midBtn.setOnAction(selectionEvent);
			shapeBtn.setOnAction(selectionEvent);

			// the event for the function input box
			EventHandler<ActionEvent> functionEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setFunction(functionField.getText());
						lbl.setText("Area: " + area.getArea() + " sq. u.");
						updateLineChart();
					} catch (Exception e2) {
						Alert alert = new Alert(AlertType.ERROR, "Error with function!");
						e2.printStackTrace();
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
						alert.showAndWait();
						functionField.setText(area.getFunction());
					}
				}
			};

			functionButton.setOnAction(functionEvent);

			// the event for the lower input box
			EventHandler<ActionEvent> lowerEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setLower(eval(lowerBoundField.getText()));
						lbl.setText("Area: " + area.getArea() + " sq. u.");
						updateLineChart();
					} catch (Exception e2) {
						Alert alert = new Alert(AlertType.ERROR, "Must be a number!");
						e2.printStackTrace();
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
						alert.showAndWait();
						lowerBoundField.setText(String.valueOf(area.getLower()));
					}
				}
			};

			lowerBoundButton.setOnAction(lowerEvent);

			// the event for the upper input box
			EventHandler<ActionEvent> upperEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setUpper(eval(upperBoundField.getText()));
						lbl.setText("Area: " + area.getArea() + " sq. u.");
						updateLineChart();
					} catch (Exception e2) {
						Alert alert = new Alert(AlertType.ERROR, "Must be a number!");
						e2.printStackTrace();
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
						alert.showAndWait();
						upperBoundField.setText(String.valueOf(area.getUpper()));
					}
				}
			};

			upperBoundButton.setOnAction(upperEvent);

			// the event for the rectangle input box
			EventHandler<ActionEvent> rectEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						if (eval(rectangleField.getText()) > 100) {
							new Alert(AlertType.ERROR, "Must be less than 100!").showAndWait();
							rectangleField.setText(String.valueOf(area.getUpper()));
						} else {
							area.setRect((int) Math.round(eval(rectangleField.getText())));
							lbl.setText("Area: " + area.getArea() + " sq. u.");
							updateLineChart();
						}
					} catch (Exception e2) {
						Alert alert = new Alert(AlertType.ERROR, "Must be an integer!");
						e2.printStackTrace();
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
						alert.showAndWait();
						rectangleField.setText(String.valueOf(area.getUpper()));
					}
				}
			};

			rectangleButton.setOnAction(rectEvent);

			// the update all button which calls all the events for each of the buttons
			EventHandler<ActionEvent> allEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					rectEvent.handle(e);
					upperEvent.handle(e);
					lowerEvent.handle(e);
					functionEvent.handle(e);
				}
			};

			setAllButton.setOnAction(allEvent);

			// update the line chart
			updateLineChart();

			// defining the scene using the root borderpane
			Scene scene = new Scene(root, 350, 300);

			// setting the styling class
			scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());

			primaryStage.initStyle(StageStyle.DECORATED);

			primaryStage.setTitle("CRAC - Calculus Reimann Area Calculator");
			primaryStage.setScene(scene);

			// set the icon and show the scene
			primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toExternalForm()));
			primaryStage.show();

		} catch (Exception e) {

			// if an error happens while launching the javafx app, show an alert dialogue
			// with the error
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Oh look, an error.");
			alert.setContentText(e.getLocalizedMessage());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}

	}

	/**
	 * Updates the line chart by clear the data on it and generating the new points
	 * on it for the select type
	 * 
	 * @return the line chart with the updated points
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LineChart updateLineChart() {
		ac.getData().clear();

		Type type = area.getType();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Left");
		// for (Entry<Double, Double> ne : area.getDataSet(Type.LEFT).entrySet())
		// series1.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

		if (type.equals(Type.LEFT))
			for (Entry<Double, Double> x : area.getDataSet(Type.LEFT).entrySet()) {
				for (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() + area.getDelta())) {
					ac.getData().add(x2);
				}
			}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Right");

		// for (Entry<Double, Double> ne : area.getDataSet(Type.RIGHT).entrySet())
		// series2.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
		if (type.equals(Type.RIGHT))
			for (Entry<Double, Double> x : area.getDataSet(Type.RIGHT).entrySet()) {
				for (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() - area.getDelta())) {
					ac.getData().add(x2);
				}
			}

		XYChart.Series series3 = new XYChart.Series();
		series3.setName("Midpoint");
		// for (Entry<Double, Double> ne : area.getDataSet(Type.MID).entrySet())
		// series3.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
		if (type.equals(Type.MID))
			for (Entry<Double, Double> x : area.getDataSet(Type.MID).entrySet()) {
				for (Series x2 : drawRectangleToZero(x.getKey() + (area.getDelta() / 2), x.getValue(),
						x.getKey() - (area.getDelta() / 2))) {
					ac.getData().add(x2);
				}
			}

		XYChart.Series series5 = new XYChart.Series();
		series5.setName("Shape");

		// for (Entry<Double, Double> ne : area.getDataSet(Type.SHAPE).entrySet())
		// series5.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
		if (type.equals(Type.SHAPE))
			for (Entry<Double, Double> x : area.getDataSet(Type.SHAPE).entrySet()) {
				for (Series x2 : drawTrapezoidToZero(x.getKey(), x.getValue(), x.getKey() + area.getDelta(),
						area.eval(x.getKey() + area.getDelta()))) {
					ac.getData().add(x2);
				}
			}

		XYChart.Series series4 = new XYChart.Series();
		series4.setName("Function");
		for (Entry<Double, Double> ne : area.getDataSet(Type.FUNCTION).entrySet()) {
			Data aa = new XYChart.Data(ne.getKey(), ne.getValue());
			// Node lineSymbol1 = aa.getNode().lookup(".chart-line-symbol");
			// lineSymbol1.setStyle("-fx-background-color: transparent, transparent;");
			series4.getData().add(aa);
		}
		XYChart.Series touchup = new XYChart.Series();

		touchup.getData().add(new XYChart.Data(area.getLower(), 0));
		touchup.getData().add(new XYChart.Data(area.getUpper(), 0));

		ac.setTitle("THE AREA CALCULATOR");

		ac.getData().addAll(series1, series2, series3, series4, series5);
		Legend legend = (Legend) ac.lookup(".chart-legend");
		AtomicInteger count = new AtomicInteger();
		legend.getItems().forEach(item -> {
			if (count.get() == 0)
				item.setText("Left");
			else if (count.get() == 1)
				item.setText("Right");
			else if (count.get() == 2)
				item.setText("Midpoint");
			else if (count.get() == 3)
				item.setText("Function");
			else
				item.setText("Remove");

			count.getAndIncrement();
		});

		legend.getItems().removeIf(item -> item.getText().equals("Remove"));

		for (Node n : ac.getChildrenUnmodifiable()) {
			if (n instanceof Legend) {
				Legend l = (Legend) n;
				for (Legend.LegendItem li : l.getItems()) {
					for (XYChart.Series<Number, Number> s : ac.getData()) {
						if (s.getName() != null && s.getName().equals(li.getText())) {
							li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
							li.getSymbol().setOnMouseClicked(me -> {
								if (me.getButton() == MouseButton.PRIMARY) {
									s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
									for (XYChart.Data<Number, Number> d : s.getData()) {
										if (d.getNode() != null) {
											d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of
																								// every node in the
																								// series
										}
									}
								}
							});
							break;
						}
					}
				}
			}
		}

		return ac;

	}

	/**
	 * Gets the list of points that will generate a box by the given parameters
	 * 
	 * @param x  the x value
	 * @param y  the y value for the first x
	 * @param x2 the second x value
	 * @param y2 the y value of the second x
	 * @return an ArrayList of XYChart Series data points to be added to a chart
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<XYChart.Series> drawTrapezoidToZero(double x, double y, double x2, double y2) {
		ArrayList<XYChart.Series> lines = new ArrayList<XYChart.Series>();

		XYChart.Series line1 = new XYChart.Series();
		XYChart.Series line2 = new XYChart.Series();
		XYChart.Series line3 = new XYChart.Series();

		Data point1 = new XYChart.Data(x, 0);
		Data point2 = new XYChart.Data(x, y);

		line1.getData().add(point1);
		line1.getData().add(point2);

		Data point3 = new XYChart.Data(x, y);
		Data point4 = new XYChart.Data(x2, y2);

		line2.getData().add(point3);
		line2.getData().add(point4);

		Data point5 = new XYChart.Data(x2, y2);
		Data point6 = new XYChart.Data(x2, 0);

		line3.getData().add(point5);
		line3.getData().add(point6);

		lines.add(line1);
		lines.add(line2);
		lines.add(line3);

		return lines;
	}

	/**
	 * draws a rectangle instead of a trapezoid with less arguments
	 * 
	 * @param x  the x value
	 * @param y  the y value
	 * @param x2 the second x value
	 * @return an ArrayList of XYChart Series data points to be added to a chart
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<XYChart.Series> drawRectangleToZero(double x, double y, double x2) {

		return drawTrapezoidToZero(x, y, x2, y);

	}

	/**
	 * Main method for the program, launches the JavaFX UI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Uses exp4j to parse the function as a string
	 * 
	 * @param function String of the function which will include x
	 * 
	 * @return the evaluated value of the function with variable x
	 */
	private double eval(String function) {
		Expression e = new ExpressionBuilder(function).build();
		return e.evaluate();
	}

}
