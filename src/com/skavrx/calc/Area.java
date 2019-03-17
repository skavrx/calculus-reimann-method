package com.skavrx.calc;

import java.util.HashMap;
import java.util.Map;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Utility class for the area under a function
 * 
 * @see Area()
 * @version 0.3
 * @author skavrx
 *
 */
public class Area {

	// Defining the variables for the area class
	private Type type;
	private String function;
	private double lower;
	private double upper;
	private int rect;

	// The function delta is what delta will be used for mapping the original
	// function to the graph.
	// If the class will be printing out debugging messages
	private boolean DEBUG = false;
	private double funcDelta = 0.01;

	/**
	 * Area of a function can be estimated uses four different methods which this
	 * class can use. This class uses Reimann Sums to find the area. The different
	 * types it uses can be selected with the {@code Type} class. There is the left
	 * (lower) sum and the right (upper) sum. The midpoint and another method using
	 * trapezoids. The number of rectangles determines the delta for each rectangle.
	 * 
	 * @param type     From class {@code App.Type} Determines what area will be used
	 *                 when default functions {@code getArea()} are called. If only
	 *                 {@code getArea(Area.Type)} will be used, {@code Type.DEFAULT}
	 *                 can be set for the instance.
	 * @param function String of the function that the area will be given for. Uses
	 *                 {@code x} as a variable.
	 * @param lower    Double of the lower bound of the area to be found. <i>Cannot
	 *                 be greater than upper bound!</i>
	 * @param upper    Double of the upper bound of the area to be found. <i>Cannot
	 *                 be less than lower bound!</i>
	 * @param rect     An integer of the number of rectangles that will be used to
	 *                 find the area of the function.
	 * 
	 * @throws ArithmeticException If the upper bound is greater than the lower
	 *                             bound or the lower bound is greater than the
	 *                             upper bound.
	 */
	public Area(Type type, String function, String lower, String upper, String rect) {
		// Setting class variables
		this.type = type;
		this.setFunction(function);
		// Checking the upper and lower bounds don't conflict
		if (eval(upper) < eval(lower))
			throw new ArithmeticException("Upper bound cannot be less than lower bound!");
		else if (eval(lower) > eval(upper))
			throw new ArithmeticException("Lower bound cannot be more than upper bound!");
		this.setLower(eval(lower));
		this.setUpper(eval(upper));
		this.setRect((int) Math.round(eval(rect)));
	}

	/**
	 * This will set the debug state to true and print out debug messages
	 * 
	 * @param debug Set the debug state
	 */
	public void debug(boolean debug) {
		this.DEBUG = debug;
	}

	/**
	 * 
	 * @return The delta of the rectangles. Determined by subtracting the upper and
	 *         lower bounds and dividing that by the number of rectangles.
	 */
	public double getDelta() {
		double domain = upper - lower;
		return domain / rect;
	}

	/**
	 * This is for creating a graph.
	 * 
	 * @return {@code Map<Double, Double>} A Map which contains the (x, y) values of
	 *         the function
	 */
	public Map<Double, Double> getDataSet() {
		return getDataSet(type);
	}

	/**
	 * This is for creating a graph by defined area type.
	 * 
	 * @param type The area type that the data set will return with
	 * 
	 * @return {@code Map<Double, Double>} A Map which contains the (x, y) values of
	 *         the function
	 * @throws NullPointerException If the data set could not be set
	 */
	public Map<Double, Double> getDataSet(Type type) {
		if (DEBUG)
			System.out.println("Gettings data set " + type.name());
		switch (type) {
		case RIGHT:
			if (dataSetRight != null)
				return dataSetRight;
			else {
				getRightArea();
				if (dataSetRight != null)
					return dataSetRight;
				else
					throw new ArithmeticException("Could not instantiate the data set!");
			}
		case LEFT:
			if (dataSetLeft != null)
				return dataSetLeft;
			else {
				getLeftArea();
				if (dataSetLeft != null)
					return dataSetLeft;
				else
					throw new ArithmeticException("Could not instantiate the data set!");
			}
		case MID:
			if (dataSetMidpoint != null)
				return dataSetMidpoint;
			else {
				getMidpointArea();
				if (dataSetMidpoint != null)
					return dataSetMidpoint;
				else
					throw new ArithmeticException("Could not instantiate the data set!");
			}
		case SHAPE:
			if (dataSetShape != null)
				return dataSetShape;
			else {
				getShapeArea();
				if (dataSetShape != null)
					return dataSetShape;
				else
					throw new ArithmeticException("Could not instantiate the data set!");
			}
		case FUNCTION:
			if (dataSetFunction != null)
				return dataSetFunction;
			else {
				getFunctionData();
				if (dataSetFunction != null)
					return dataSetFunction;
				else
					throw new ArithmeticException("Could not instantiate the data set!");
			}
		default:
			System.out.println("Default value returned: You may have to specify which area type!");
			return null;
		}
	}

	/**
	 * Called to prevent any data overlap
	 */
	public void resetDataSets() {
		dataSetRight = null;
		dataSetLeft = null;
		dataSetMidpoint = null;
		dataSetShape = null;
		dataSetFunction = null;
	}
	
	/**
	 * Get the area of the Area object with the given type Gets the type that was
	 * set on instance creation and uses {@code getArea(AreaType area)}
	 * 
	 * @return double Area in squared units of the function's units
	 */
	public double getArea() {
		return getArea(type);
	}

	/**
	 * Returns 0 if AreaType is not RIGHT, LEFT, MID, SHAPE
	 * 
	 * @param area AreaType for what area to find
	 * 
	 * @return double Area in squared units of the function's units
	 */
	public double getArea(Type area) {
		if (DEBUG)
			System.out.println(area.name());
		switch (area) {
		case RIGHT:
			return getRightArea();
		case LEFT:
			return getLeftArea();
		case MID:
			return getMidpointArea();
		case SHAPE:
			return getShapeArea();
		default:
			System.out.println("Default value returned: You may have to specify which area type!");
			return 0;
		}
	}

	/**
	 * @return The default type for the instance
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type The default type to be set for the instance
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return gets the number rectangles
	 */
	public int getRect() {
		return rect;
	}

	/**
	 * 
	 * @param rect sets the number of rectangles for the area
	 */
	public void setRect(int rect) {
		this.rect = rect;
	}

	/**
	 * 
	 * @return get the upper set bound
	 */
	public double getUpper() {
		return upper;
	}

	/**
	 * 
	 * @param upper set the upper bound
	 */
	public void setUpper(double upper) {
		this.upper = upper;
	}

	/**
	 * 
	 * @return get the lower bound
	 */
	public double getLower() {
		return lower;
	}

	/**
	 * 
	 * @param lower set the lower bound
	 */
	public void setLower(double lower) {
		this.lower = lower;
	}

	/**
	 * 
	 * @return get the string of the set function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * 
	 * @param function the function string to be set. x must be the variable.
	 * @return this instance
	 */
	public Area setFunction(String function) {
		this.function = function;
		return this;
	}

	/**
	 * The map of the left handed methods points that will be mapped.
	 */
	private Map<Double, Double> dataSetLeft;

	/**
	 * Sets the {@code dataSetLeft} and adds the values to it using the midpoint
	 * method
	 */
	private double getLeftArea() {
		double domain = upper - lower; // Getting the domain by subtracting the upper bound by the lower bound.
		double delta = domain / rect; // The distance between the rectangles determined by dividing the domain by
										// the number of rectangles

		dataSetLeft = new HashMap<Double, Double>();

		double area = 0; // Temporary area variable which is returned
		int rec_num = 1; // Debug test to check if the number of rectangles is
		// correct

		// Run through all the rectangles by setting the initial x value to the lower
		// bound
		// then each time adding the delta to x until x is less than or equal to the
		// upper bound
		// It stops when x is one below the upper bound.
		for (double x = lower; x <= upper - delta; x += delta) {
			if (DEBUG)
				System.out.println("Rectangle: " + rec_num + " " + "Current: x: " + x + " y: " + eval(function, x));

			if (DEBUG)
				System.out.println(String.format("Data Point Added: (%.2f, %.2f)", x, eval(function, x)));
			// add the delta (length) multiplied by the evaluated x value from the given
			// function (height) which is the area of the rectangle.
			area += Math.abs((delta * eval(function, x)));
			dataSetLeft.put(x, eval(function, x));
			if (DEBUG)
				rec_num++;
		}

		return area;
	}

	/**
	 * The map of the right handed methods points that will be mapped.
	 */
	private Map<Double, Double> dataSetRight;

	/**
	 * Sets the {@code dataSetRight} and adds the values to it using the midpoint
	 * method
	 */
	private double getRightArea() {
		double domain = upper - lower; // Getting the domain by subtracting the upper bound by the lower bound.
		double delta = domain / rect; // The distance between the rectangles determined by dividing the domain by
										// the number of rectangles
		
		dataSetRight = new HashMap<Double, Double>();
		
		double area = 0; // Temporary area variable which is returned
		int rec_num = 1; // Debug test num

		// Iterates starting with x equals the upper bound. Each time through it
		// subtracts the delta. It stops when x is one above the lower bound
		for (double x = upper; x >= lower + delta; x -= delta) {
			if (DEBUG)
				System.out.println("Rectangle: " + rec_num + " " + "Current: x: " + x + " y: " + eval(function, x));
			// add the delta (length) multiplied by the evaluated x value from the given
			// function (height) which is the area of the rectangle.
			dataSetRight.put(x, eval(function, x));
			area += Math.abs((delta * eval(function, x)));
			if (DEBUG)
				rec_num++;
		}
		return area;
	}

	/**
	 * The map of the midpoint methods points that will be mapped.
	 */
	private Map<Double, Double> dataSetMidpoint;

	/**
	 * Sets the {@code dataSetMidpoint} and adds the values to it using the midpoint
	 * method
	 */
	private double getMidpointArea() {

		double domain = upper - lower; // Getting the domain by subtracting the upper bound by the lower bound.
		double delta = domain / rect; // The distance between the rectangles determined by dividing the domain by
										// the number of rectangles

		dataSetMidpoint = new HashMap<Double, Double>();

		double area = 0; // Temporary area variable which is returned
		int rec_num = 1; // Debug test num

		// Iterates starting with x equals the upper bound. Each time through it
		// subtracts the delta. It stops when x is one above the lower bound
		for (double x = lower; x <= upper - delta; x += delta) {
			double midpoint = (x + (x + delta)) / 2;
			area += Math.abs((delta * eval(function, midpoint)));
			dataSetMidpoint.put(midpoint, eval(function, midpoint));
			if (DEBUG)
				System.out.println("Rectangle: " + rec_num + " " + "Current: mid: " + midpoint + " y: "
						+ eval(function, midpoint));
			if (DEBUG)
				rec_num++;
		}

		return area;
	}

	/**
	 * The map of the trapezoid methods points that will be mapped.
	 */
	private Map<Double, Double> dataSetShape;

	/**
	 * Sets the {@code dataSetShape} and adds the values to it using the trapezoid
	 * method
	 */
	private double getShapeArea() {
		double domain = upper - lower; // Getting the domain by subtracting the upper bound by the lower bound.
		double delta = domain / rect; // The distance between the rectangles determined by dividing the domain by
										// the number of rectangles

		dataSetShape = new HashMap<Double, Double>();	
		
		double area = 0; // Temporary area variable which is returned
		int rec_num = 1; // Debug test num

		// Iterates starting with x equals the upper bound. Each time through it
		// subtracts the delta. It stops when x is one above the lower bound
		for (double x = lower; x <= upper - delta; x += delta) {
			double first = eval(function, x);
			double second = eval(function, (x + delta));
			double average = (first + second) / 2;
			area += Math.abs((delta * average));
			dataSetShape.put(x, first);
			if (DEBUG)
				System.out.println("Rectangle: " + rec_num + " " + "Current: avg: " + x + " y: " + average);
			if (DEBUG)
				rec_num++;
		}

		return area;
	}

	/**
	 * The map of the functions points that will be mapped.
	 */
	private Map<Double, Double> dataSetFunction;

	/**
	 * Sets the {@code dataSetFunction} and adds the values to it using the function
	 * delta and function
	 */
	private void getFunctionData() {

		dataSetFunction = new HashMap<Double, Double>();

		// Iterates starting with x equals the upper bound. Each time through it
		// subtracts the delta. It stops when x is one above the lower bound
		for (double x = lower; x <= upper + funcDelta; x += funcDelta) {
			// if (DEBUG)
			// System.out.println("Current: " + x);
			dataSetFunction.put(x, eval(function, x));
		}
	}

	/**
	 * 
	 * @param delta sets the functions delta
	 */
	public void setFuncDelta(double delta) {
		this.funcDelta = delta;
	}

	/**
	 * 
	 * @return gets the functions delta for graphing
	 */
	public double getFuncDelta() {
		return funcDelta;
	}

	/**
	 * Uses exp4j to parse the function as a string with variable x into a double
	 * value
	 * 
	 * @param function String of the function which will include x
	 * @param x        the variable x that will replace the variable x in the
	 *                 function
	 * @return the evaluated value of the function with variable x
	 */
	private double eval(String function, double x) {
		Expression e = new ExpressionBuilder(function).variables("x").build().setVariable("x", x);
		return e.evaluate();
	}
	
	/**
	 * 
	 * @param x the x point to evaluate
	 * @return double, y value of the function
	 */
	public double eval(double x) {
		return eval(function, x);
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
	
	/**
	 * The area type for when different methods are being used for finding the area.
	 * 
	 * @author skavrx
	 *
	 */
	public static enum Type {
		LEFT, RIGHT, MID, SHAPE, DEFAULT, FUNCTION
	}

}
