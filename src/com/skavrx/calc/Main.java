package com.skavrx.calc;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.skavrx.calc.Area.Type;
import com.sun.javafx.charts.Legend;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class Main extends Application {

	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	@Override
	public void start(Stage primaryStage) {
		try {
			Area area = new Area(Area.Type.DEFAULT, "x^2", 0, 2, 10);
			
			primaryStage.setTitle(area.getFunction());

			System.out.println(area.getArea());
			area.debug(true);


			final NumberAxis xAxis = new NumberAxis(area.getLower() - 1, area.getUpper() + 1, 1);
			final NumberAxis yAxis = new NumberAxis();
			final LineChart<Number, Number> ac = new LineChart<Number, Number>(xAxis, yAxis);
			
			XYChart.Series series1 = new XYChart.Series();
			series1.setName("Left");
			/*for (Entry<Double, Double> ne : area.getDataSet(Type.LEFT).entrySet())
				series1.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
			
			
			for (Entry<Double, Double> x : area.getDataSet(Type.LEFT).entrySet()) {
				//series1.getData().add(new XYChart.Data(x.getKey(), x.getValue()));
				for (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() + area.getDelta())) { 
					ac.getData().add(x2);
				}
			}
*/
			XYChart.Series series2 = new XYChart.Series();
			series2.setName("Right");
			/*
			for (Entry<Double, Double> ne : area.getDataSet(Type.RIGHT).entrySet())
				series2.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
			
			
			for (Entry<Double, Double> x : area.getDataSet(Type.RIGHT).entrySet()) {
				//series1.getData().add(new XYChart.Data(x.getKey(), x.getValue()));
				for (Series x2 : drawRectangleToZero(x.getKey(), x.getValue(), x.getKey() - area.getDelta())) { 
					ac.getData().add(x2);
				}
			}
			
			*/
			XYChart.Series series3 = new XYChart.Series();
			series3.setName("Midpoint");
			for (Entry<Double, Double> ne : area.getDataSet(Type.MID).entrySet())
				series3.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
			
			
			for (Entry<Double, Double> x : area.getDataSet(Type.MID).entrySet()) {
				//series3.getData().add(new XYChart.Data(x.getKey(), x.getValue()));
				for (Series x2 : drawRectangleToZero(x.getKey() + (area.getDelta() / 2), x.getValue(), x.getKey() - (area.getDelta() / 2))) { 
					ac.getData().add(x2);
				}
			}
			
			XYChart.Series series5 = new XYChart.Series();
			series5.setName("Shape");
			/*
			for (Entry<Double, Double> ne : area.getDataSet(Type.SHAPE).entrySet())
				series5.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));
			
			
			for (Entry<Double, Double> x : area.getDataSet(Type.SHAPE).entrySet()) {
				//series5.getData().add(new XYChart.Data(x.getKey(), x.getValue()));
				for (Series x2 : drawTrapezoidToZero(x.getKey(), x.getValue(), x.getKey() + area.getDelta(), x.getValue())) { 
					ac.getData().add(x2);
				}
			}
			
			*/
			XYChart.Series series4 = new XYChart.Series();
			series4.setName("Function");
			for (Entry<Double, Double> ne : area.getDataSet(Type.FUNCTION).entrySet())
				series4.getData().add(new XYChart.Data(ne.getKey(), ne.getValue()));

			ac.setTitle("THE AREA CALCULATOR");

			Scene scene = new Scene(ac, 800, 600);
			
			ac.getData().addAll(series1, series2, series3, series4);
			
			scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());

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
			                                    d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
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
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<XYChart.Series> lines;
	
	public ArrayList<XYChart.Series> drawTrapezoidToZero(double x, double y, double x2, double y2) {
		lines = new ArrayList<XYChart.Series>();
		
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
		lines = new ArrayList<XYChart.Series>();
		
		XYChart.Series line1 = new XYChart.Series();
		XYChart.Series line2 = new XYChart.Series();
		XYChart.Series line3 = new XYChart.Series();

		line1.getData().add(new XYChart.Data(x, 0));
		line1.getData().add(new XYChart.Data(x, y)); 
		
		line2.getData().add(new XYChart.Data(x, y)); 
		line2.getData().add(new XYChart.Data(x2, y)); 
		
		line3.getData().add(new XYChart.Data(x2, y)); 
		line3.getData().add(new XYChart.Data(x2, 0));
		
		lines.add(line1);
		lines.add(line2);
		lines.add(line3);
		
		return lines;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
