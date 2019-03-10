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

public class Main extends Application {

	private BorderPane root;
	private final int SIZE = 60;
	private Area area;

	private NumberAxis xAxis;
	private NumberAxis yAxis;
	private LineChart<Number, Number> ac;

	@Override
	public void start(Stage primaryStage) {
		initUI(primaryStage);
	}

	@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
	private void initUI(Stage primaryStage) {

		try {
			area = new Area(Area.Type.DEFAULT, "x^3", -5, 5, 10);
			xAxis = new NumberAxis();
			yAxis = new NumberAxis();
			ac = new LineChart<Number, Number>(xAxis, yAxis);
			ac.setLegendVisible(false);
			
			area.debug(false);

			primaryStage.setTitle(area.getFunction());
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(800);

			final Menu menu1 = new Menu("File");
			final Menu menu2 = new Menu("Options");
			final Menu menu3 = new Menu("Help");

			MenuItem m1 = new MenuItem("Info");
			MenuItem m3 = new MenuItem("Github");

			MenuItem m4 = new MenuItem("Save image...");

			EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("CRAC Information");
					alert.setHeaderText("CRAC Information");
					alert.setContentText(
							"Written by skavrx (John Monsen)\nWebsite: https://skavrx.com\nContact: contact@skavrx.com\n\nMIT License 2019");
					alert.showAndWait();
				}
			};

			EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Github");
					alert.setHeaderText("Github Repo");
					alert.setContentText(
							"Are you sure you want to go to Github?\n(This will open in your default brower.)");
					ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
					ButtonType noButton = new ButtonType("No", ButtonData.NO);
					alert.getButtonTypes().setAll(okButton, noButton);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.orElse(noButton) == okButton) {
						getHostServices().showDocument("https://github.com/skavrx/calculus-reimann-method");
					}
				}
			};

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

			// add event
			m1.setOnAction(event);
			m3.setOnAction(event2);
			m4.setOnAction(event3);

			// add menu items to menu
			menu3.getItems().add(m1);
			menu3.getItems().add(m3);

			menu1.getItems().add(m4);

			MenuBar menuBar = new MenuBar();
			menuBar.getMenus().addAll(menu1, menu2, menu3);

			root = new BorderPane();

			root.setTop(menuBar);
			root.setBottom(getBottomLabel());
			//root.setLeft(getLeftLabel());
			//root.setRight(getRightLabel());

			VBox info = new VBox(5);
			info.setAlignment(Pos.CENTER_RIGHT);
			HBox buttonbox = new HBox(5);
			HBox buttonBoxFunction = new HBox(5);
			HBox buttonBoxLowerBound = new HBox(5);
			HBox buttonBoxUpperBound = new HBox(5);
			HBox buttonBoxRectangles = new HBox(5);
			HBox areaInfo = new HBox(5);
			HBox updateBox = new HBox(5);

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


			ToggleButton leftBtn = new ToggleButton("Left");
			ToggleButton rightBtn = new ToggleButton("Right");
			ToggleButton midBtn = new ToggleButton("Midpoint");
			ToggleButton shapeBtn = new ToggleButton("Shape");

			Label functionLabel = new Label("Function");
			TextField functionField = new TextField();
			functionField.setText(area.getFunction());
			functionField.setPromptText("Function");
			functionField.setTooltip(new Tooltip("Use x as the variable"));
			Button functionButton = new Button("Set");

			Label lowerBoundLabel = new Label("Lower Bound");
			TextField lowerBoundField = new TextField();
			lowerBoundField.setText(String.valueOf(area.getLower()));
			lowerBoundField.setPromptText("Lower Bound");
			lowerBoundField.setTooltip(new Tooltip("Cannot be greater than upper bound"));
			Button lowerBoundButton = new Button("Set");

			Label upperBoundLabel = new Label("Upper Bound");
			TextField upperBoundField = new TextField();
			upperBoundField.setText(String.valueOf(area.getUpper()));
			upperBoundField.setPromptText("Upper Bound");
			upperBoundField.setTooltip(new Tooltip("Cannot be lower than lower bound"));
			Button upperBoundButton = new Button("Set");

			Label rectangleLabel = new Label("Rectangles");
			TextField rectangleField = new TextField();
			rectangleField.setText(String.valueOf(area.getRect()));
			rectangleField.setPromptText("Rectangles");
			rectangleField.setTooltip(new Tooltip("Must be a whole number"));
			Button rectangleButton = new Button("Set");

			buttonbox.getChildren().addAll(leftBtn, rightBtn, midBtn, shapeBtn);
			buttonBoxFunction.getChildren().addAll(functionLabel, functionField, functionButton);
			buttonBoxLowerBound.getChildren().addAll(lowerBoundLabel, lowerBoundField, lowerBoundButton);
			buttonBoxUpperBound.getChildren().addAll(upperBoundLabel, upperBoundField, upperBoundButton);
			buttonBoxRectangles.getChildren().addAll(rectangleLabel, rectangleField, rectangleButton);

			Label areaTotal = new Label("Area: " + area.getArea(Type.SHAPE));
			areaInfo.getChildren().addAll(areaTotal);

			Label selectedMethod = new Label("Selected Method: " + area.getType().toString());
			areaInfo.getChildren().addAll(selectedMethod);

			Button setAllButton = new Button("Update All");
			updateBox.getChildren().addAll(setAllButton);
			
			info.getChildren().addAll(buttonbox, buttonBoxFunction, buttonBoxLowerBound, buttonBoxUpperBound,
					buttonBoxRectangles, updateBox, areaInfo);
			root.setRight(info);
			root.setCenter(ac);

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
						
						selectedMethod.setText("Selected Method: " + area.getType().toString());
						updateLineChart();
						areaTotal.setText("Area: " + area.getArea());

					} catch (Exception e2) {
						new Alert(AlertType.ERROR, "Error with function!").showAndWait();
					}
				}
			};

			leftBtn.setOnAction(selectionEvent);
			rightBtn.setOnAction(selectionEvent);
			midBtn.setOnAction(selectionEvent);
			shapeBtn.setOnAction(selectionEvent);

			EventHandler<ActionEvent> functionEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setFunction(functionField.getText());
						areaTotal.setText("Area: " + area.getArea());
						updateLineChart();
					} catch (Exception e2) {
						new Alert(AlertType.ERROR, "Error with function!").showAndWait();
						lowerBoundField.setText(area.getFunction());
					}
				}
			};

			functionButton.setOnAction(functionEvent);

			EventHandler<ActionEvent> lowerEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setLower(Double.valueOf(lowerBoundField.getText()));
						areaTotal.setText("Area: " + area.getArea());
						updateLineChart();
					} catch (Exception e2) {
						new Alert(AlertType.ERROR, "Must be a number!").showAndWait();
						lowerBoundField.setText(String.valueOf(area.getLower()));
					}
				}
			};

			lowerBoundButton.setOnAction(lowerEvent);

			EventHandler<ActionEvent> upperEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setUpper(Double.valueOf(upperBoundField.getText()));
						areaTotal.setText("Area: " + area.getArea());
						updateLineChart();
					} catch (Exception e2) {
						new Alert(AlertType.ERROR, "Must be a number!").showAndWait();
						upperBoundField.setText(String.valueOf(area.getUpper()));
					}
				}
			};

			upperBoundButton.setOnAction(upperEvent);

			EventHandler<ActionEvent> rectEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					area.resetDataSets();
					try {
						area.setRect(Integer.valueOf(rectangleField.getText()));
						areaTotal.setText("Area: " + area.getArea());
						updateLineChart();
					} catch (Exception e2) {
						new Alert(AlertType.ERROR, "Must be a integer!").showAndWait();
						rectangleField.setText(String.valueOf(area.getUpper()));
					}
				}
			};

			rectangleButton.setOnAction(rectEvent);
			
			EventHandler<ActionEvent> allEvent = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					rectEvent.handle(e);
					upperEvent.handle(e);
					lowerEvent.handle(e);
					functionEvent.handle(e);
				}
			};

			setAllButton.setOnAction(allEvent);
			
			updateLineChart();

			Scene scene = new Scene(root, 350, 300);
			scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());

			primaryStage.setTitle("CRAC - Calculus Reimann Area Calculator");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Oh look, an error.");
			alert.setContentText(e.getLocalizedMessage());

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
		for (Entry<Double, Double> ne : area.getDataSet(Type.FUNCTION).entrySet())
			series4.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

		ac.setTitle("THE AREA CALCULATOR");

		ac.getData().addAll(series1, series2, series3, series4, series5);
		Legend legend = (Legend) ac.lookup(".chart-legend");
		AtomicInteger count = new AtomicInteger();
		legend.getItems().forEach(item -> {
			if (count.get() == 0) {
				item.setText("Left");
			} else if (count.get() == 1) {
				item.setText("Right");
			} else if (count.get() == 2) {
				item.setText("Midpoint");
			} else if (count.get() == 3) {
				item.setText("Function");
			} else {
				item.setText("Remove");
			}
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

	private Label getBottomLabel() {

		Label lbl = new MyLabel("Bottom");
		lbl.setPrefHeight(SIZE);
		lbl.prefWidthProperty().bind(root.widthProperty());
		lbl.setStyle("-fx-border-style: dotted; -fx-border-width: 1 0 0 0;"
				+ "-fx-border-color: gray; -fx-font-weight: bold");

		return lbl;
	}

	private Label getLeftLabel() {

		Label lbl = new MyLabel("Left");
		lbl.setPrefWidth(SIZE);
		lbl.prefHeightProperty().bind(root.heightProperty().subtract(2 * SIZE));
		lbl.setStyle("-fx-border-style: dotted; -fx-border-width: 0 1 0 0;"
				+ "-fx-border-color: gray; -fx-font-weight: bold");

		return lbl;
	}

	private Label getRightLabel() {

		Label lbl = new MyLabel("Right");
		lbl.setPrefWidth(SIZE);
		lbl.prefHeightProperty().bind(root.heightProperty().subtract(2 * SIZE));
		lbl.setStyle("-fx-border-style: dotted; -fx-border-width: 0 0 0 1;"
				+ "-fx-border-color: gray; -fx-font-weight: bold");

		return lbl;
	}

	private Label getCenterLabel() {

		Label lbl = new MyLabel("Center");
		lbl.setStyle("-fx-font-weight: bold");
		lbl.prefHeightProperty().bind(root.heightProperty().subtract(2 * SIZE));
		lbl.prefWidthProperty().bind(root.widthProperty().subtract(2 * SIZE));

		return lbl;
	}

	public ArrayList<XYChart.Series> drawTrapezoidToZero(double x, double y, double x2, double y2) {
		ArrayList<XYChart.Series> lines = new ArrayList<XYChart.Series>();

		XYChart.Series line1 = new XYChart.Series();
		XYChart.Series line2 = new XYChart.Series();
		XYChart.Series line3 = new XYChart.Series();

		line1.getData().add(new XYChart.Data(x, 0));
		line1.getData().add(new XYChart.Data(x, y));

		line2.getData().add(new XYChart.Data(x, y));
		line2.getData().add(new XYChart.Data(x2, y2));

		line3.getData().add(new XYChart.Data(x2, y2));
		line3.getData().add(new XYChart.Data(x2, 0));

		lines.add(line1);
		lines.add(line2);
		lines.add(line3);

		return lines;
	}

	public ArrayList<XYChart.Series> drawRectangleToZero(double x, double y, double x2) {

		return drawTrapezoidToZero(x, y, x2, y);

	}

	public static void main(String[] args) {
		launch(args);
	}

	class MyLabel extends Label {

		public MyLabel(String text) {
			super(text);

			setAlignment(Pos.BASELINE_CENTER);
		}
	}

}
