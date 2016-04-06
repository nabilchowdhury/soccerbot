package soccerbot;
import java.io.IOException;
import java.util.HashMap;

import wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
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
	public static final TextLCD LCD = LocalEV3.get().getTextLCD();
	
	private static Object lock;
	
	// WIFI
	private static final String SERVER_IP = "192.168.10.124";//"localhost";
	private static final int TEAM_NUMBER = 14;
	
	public static final EV3LargeRegulatedMotor loadMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor launchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	
	//US
	public static final Port usPortL = LocalEV3.get().getPort("S2");
	public static final Port usPortR = LocalEV3.get().getPort("S1");
	
	//Colorsensor Middle
	public static final Port colorPortM = LocalEV3.get().getPort("S3");	
	
	//Colorsensor T
	public static final Port colorPortT = LocalEV3.get().getPort("S4");	
	
	public static double WIDTH = 19.10;
	public static double RADIUS = 2.072;
	
	private final static int FAST = 200, SLOW = 100, REGULAR = 200, SMOOTH = 500, DEFAULT = 6000; 
	
	public static int BALL_LLX = 2, BALL_LLY = 2, BALL_URX = 3, BALL_URY = 3;
	
	//MAIN METHOD
	public static void main(String[] args){
		
		USPoller leftPoller = new USPoller(usPortL);
		USPoller rightPoller = new USPoller(usPortR);
		LSPoller lsPoller = new LSPoller(colorPortT);
		leftPoller.start(); rightPoller.start(); lsPoller.start();	//correctionPoller.start();
		
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			LCD.drawString("Connection failed", 0, 8);
		}
		
		//example use of StartData
		LCD.clear();
		if (conn != null){
			HashMap<String,Integer> t = conn.StartData;
			if (t == null) {
				LCD.drawString("Failed to read transmission", 0, 5);
			} else {
				LCD.drawString("Transmission read", 0, 5);
				LCD.drawString(t.toString(), 0, 6);		
			}
			
			Odometer odo = new Odometer(leftMotor, rightMotor); 
			Screen lcd = new Screen(odo);
			odo.start();
			lcd.start();
			
			Navigation navigate = new Navigation(odo,leftPoller, rightPoller, leftMotor, rightMotor);
			
			USLocalizer usLocalizer = new USLocalizer(odo, navigate, leftPoller, rightPoller);
			LightLocalizer lLocalizer = new LightLocalizer(odo, navigate, lsPoller, t.get("OSC"));
			
			usLocalizer.localize();
			lLocalizer.localize();
			
			//OdometryCorrection correct = new OdometryCorrection(odo, lsPoller);
			
			navigate.travelTo(BALL_LLX+0.15, 0, true);
			
			//correct.start();
			
			navigate.travelTo(BALL_LLX+0.15, BALL_LLY-1, true);
			navigate.setSpeeds(150, 150, false, 6000);
			navigate.turnTo(BALL_LLX+0.15, 0);
			navigate.preventTwitch();;
			navigate.goStraight(150, 150, -18);
			
			loadMotor.setSpeed(300);
			loadMotor.rotate(180, false);
			
			loadMotor.setSpeed(50);
			loadMotor.rotate(-120, false);
					
			
			for(int i=0; i<2; i++){
				navigate.preventTwitch();
				navigate.goStraight(150, 150, -7);
				
				loadMotor.setSpeed(200);
				loadMotor.rotate(130, false);
				
				loadMotor.setSpeed(50);
				loadMotor.rotate(-120, false);	
			}
			
			
			launchMotor.setAcceleration(6000); launchMotor.setSpeed(150);
			launchMotor.rotate(140, false);
			
			
			
			navigate.preventTwitch();
			navigate.goStraight(200, 200, 60);
			
			try{
				Thread.sleep(1500);
			}catch(Exception e){}
			
			navigate.preventTwitch();
			
			navigate.setSpeeds(150,150,false, 6000);
			
			navigate.turnTo(3,7);
			
			
			launchMotor.setAcceleration(20000000); launchMotor.setSpeed(600);
			launchMotor.rotate(-110, false); launchMotor.flt();
			
			
			
		} else {
			LCD.drawString("Connection failed", 0, 5);
		}
					
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
