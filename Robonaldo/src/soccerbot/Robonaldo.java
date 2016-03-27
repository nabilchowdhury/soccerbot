package soccerbot;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
/**
 * Robonaldo instantiates all the threads and operations required at start-up of the program.
 * The initial parameters fed into the robot will be received in this class and be passed along to the rest of
 * the system. 
 */
public class Robonaldo {
	public static final TextLCD t = LocalEV3.get().getTextLCD();
	
	public static final EV3LargeRegulatedMotor loadMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	//public static final EV3LargeRegulatedMotor launchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	
	//US
	public static final Port usPortL = LocalEV3.get().getPort("S2");
	public static final Port usPortR = LocalEV3.get().getPort("S1");
	
	//Colorsensor Middle
	public static final Port colorPortM = LocalEV3.get().getPort("S3");	
	
	//Colorsensor T
	//public static final Port colorPortT = LocalEV3.get().getPort("S4");	
	
	public static double WIDTH = 19.05;
	public static double RADIUS = 2.072;
	
	private final static int FAST = 200, SLOW = 100, REGULAR = 140, SMOOTH = 500, DEFAULT = 6000; 
	
	//MAIN METHOD
	public static void main(String[] args){
	
		Odometer odo = new Odometer(leftMotor, rightMotor); 
		Screen lcd = new Screen(odo);
		odo.start();
		lcd.start();
		Navigation navigate = new Navigation(odo, leftMotor, rightMotor);
		
		USPoller leftPoller = new USPoller(usPortL);
		USPoller rightPoller = new USPoller(usPortR);
		leftPoller.start(); rightPoller.start();
		
		USLocalizer usLocalizer = new USLocalizer(odo, navigate, leftPoller, rightPoller);
		LightLocalizer lLocalizer = new LightLocalizer(odo, navigate, colorPortM);
		
		usLocalizer.localize();
		lLocalizer.start();
		odo.setTheta(90.0);
		odo.setY(0.0);
		odo.setX(0.0);
		
		//do square
		/*
		navigate.travelTo(0, 4);
		navigate.travelTo(4, 4);
		navigate.travelTo(4, 0);
		navigate.travelTo(0, 0);*/
		
		
		
		
	}
	
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, width * angle / 2);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	public static EV3LargeRegulatedMotor[] getMotors(){
		EV3LargeRegulatedMotor[] m = {leftMotor, rightMotor};
		return m;
	}
	
}
