package com.skavrx.calc;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.skavrx.calc.Area.Type;
import com.sun.javafx.charts.Legend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private BorderPane root;
	private final int SIZE = 60;

	@Override
	public void start(Stage primaryStage) {
		initUI(primaryStage);
	}

	private void initUI(Stage primaryStage) {

		Area area = new Area(Area.Type.DEFAULT, "x^3", -5, 5, 10);
		area.debug(false);

		primaryStage.setTitle(area.getFunction());
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);

		final Menu menu1 = new Menu("File");
		final Menu menu2 = new Menu("Options");
		final Menu menu3 = new Menu("Help");

		MenuItem m1 = new MenuItem("Info");
		MenuItem m3 = new MenuItem("Github...");

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
				alert.setContentText("Are you sure you want to go to Github?\n(This will open in your default brower.)");
				ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
				ButtonType noButton = new ButtonType("No", ButtonData.NO);
				alert.getButtonTypes().setAll(okButton, noButton);
				Optional<ButtonType> result = alert.showAndWait();

				if (result.orElse(noButton) == okButton) {
					getHostServices().showDocument("https://github.com/skavrx/calculus-reimann-method");
				}
			}
		};

		// add event
		m1.setOnAction(event);
		m3.setOnAction(event2);
		// add menu items to menu
		menu3.getItems().add(m1);
		menu3.getItems().add(m3);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menu1, menu2, menu3);

		root = new BorderPane();

		root.setTop(menuBar);
		root.setBottom(getBottomLabel());
		root.setLeft(getLeftLabel());
		root.setRight(getRightLabel());

		try {

			final NumberAxis xAxis = new NumberAxis(area.getLower() - 1, area.getUpper() + 1, 1);
			final NumberAxis yAxis = new NumberAxis();
			final LineChart<Number, Number> ac = new LineChart<Number, Number>(xAxis, yAxis);

			XYChart.Series series1 = new XYChart.Series();
			series1.setName("Left");
			// for (Entry<Double, Double> ne : area.getDataSet(Type.LEFT).entrySet())
			// series1.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

			/*
			 * for (Entry<Double, Double> x : area.getDataSet(Type.LEFT).entrySet()) { for
			 * (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() +
			 * area.getDelta())) { ac.getData().add(x2); } }
			 */

			XYChart.Series series2 = new XYChart.Series();
			series2.setName("Right");

			// for (Entry<Double, Double> ne : area.getDataSet(Type.RIGHT).entrySet())
			// series2.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

			/*
			 * for (Entry<Double, Double> x : area.getDataSet(Type.RIGHT).entrySet()) { for
			 * (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() -
			 * area.getDelta())) { ac.getData().add(x2); } }
			 */

			XYChart.Series series3 = new XYChart.Series();
			series3.setName("Midpoint");
			// for (Entry<Double, Double> ne : area.getDataSet(Type.MID).entrySet())
			// series3.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

			/*
			 * for (Entry<Double, Double> x : area.getDataSet(Type.MID).entrySet()) { for
			 * (Series x2 : drawRectangleToZero(x.getKey() + (area.getDelta() / 2),
			 * x.getValue(), x.getKey() - (area.getDelta() / 2))) { ac.getData().add(x2); }
			 * }
			 */

			XYChart.Series series5 = new XYChart.Series();
			series5.setName("Shape");

			// for (Entry<Double, Double> ne : area.getDataSet(Type.SHAPE).entrySet())
			// series5.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

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

			VBox info = new VBox(5);
			HBox buttonbox = new HBox(5);
			buttonbox.setPadding(new Insets(10));
			buttonbox.setAlignment(Pos.BASELINE_RIGHT);

			Label label1 = new Label("Name:");
			TextField textField = new TextField();
			HBox hb = new HBox();
			hb.getChildren().addAll(label1, textField);
			hb.setSpacing(10);

			Button prevBtn = new Button("Previous");
			Button nextBtn = new Button("Next");
			Button cancBtn = new Button("Cancel");
			Button helpBtn = new Button("Help");

			buttonbox.getChildren().addAll(prevBtn, nextBtn, cancBtn, helpBtn);
			info.getChildren().add(buttonbox);
			root.setRight(info);
			root.setCenter(ac);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(root, 350, 300);
		scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());

		primaryStage.setTitle("CRAC - Calculus Reimann Area Calculator");
		primaryStage.setScene(scene);
		primaryStage.show();
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

	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
}
