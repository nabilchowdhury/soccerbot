package soccerbot;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread{
	private Odometer odo;
	private Navigation navigate;
	
	//color sensor middle
	private Port csPort;
	private SensorModes csSensor;
	private SampleProvider csValue;
	private float[] csData;
	
	
	/*
	//color sensor offset
	private Port csPortT;
	private SensorModes csSensorT;
	private SampleProvider csValueT;
	private float[] csDataT;*/
	
	// Class constants
//	private final int FILTER_COLOR = 1;
//	
//	private int filterValue = 0;
//	private int filterValueT = 0;
	
	//display variables, delete this
	public static double x;
	public static double y;
	public static double deltaT;
	public static double thetaX;
	public static double thetaY;
	
	//private Object lock;
	
	public LightLocalizer(Odometer odo, Navigation navigate, Port csPort1) {
		this.odo = odo;
		this.navigate = navigate;
		
		this.csPort = csPort1;
		this.csSensor = new EV3ColorSensor(csPort);
		this.csValue = csSensor.getMode("Red");
		csSensor.setCurrentMode("Red");
		this.csData = new float[csValue.sampleSize()];		
		
		//this.lock = new Object();
	}
	
	// Initiate light localization
	public void run() {
		navigate.goStraight(350,350, -30);
		navigate.setSpeeds(250, 250, true);
		
		while(true){
			if(getColorData() < 30 && getColorData() > 7){
				//filterValue++;
				//if(filterValue >= FILTER_COLOR){
					navigate.stopMotors();
					Sound.beep();
					//filterValue = 0;
					break;
				//}
			}
		}
		
		navigate.goStraight(250, 250, 4);
		navigate.turnTo(-Math.PI/2);
		navigate.goStraight(250, 250, -8);
		
		navigate.setSpeeds(250, 250, true);
		while(true){
			if(getColorData() < 40 && getColorData() > 7){
				//filterValue++;
				//if(filterValue >= FILTER_COLOR){
					navigate.stopMotors();
					Sound.beep();
					//filterValue = 0;
					break;
				//}
			}
		}
		
		navigate.goStraight(250, 250, 10.8);
		
		navigate.setSpeeds(250, -250, true);
		int count = 0;
		while(true){
			if(count == 2){
				navigate.stopMotors();
				break;
			}
			if(getColorData() < 40 && getColorData() > 7){
				count++;
				Sound.beep();
			}
		}
		navigate.setSpeeds(150, 150, false);
		navigate.turnTo(Math.toRadians(-13.1));
		
		navigate.goStraight(150, 150, 7);	
		
		odo.setTheta(0.0);
		odo.setY(0.0);
		odo.setX(0.0);
		
	}
	
	
	// Obtain color sensor readings
	private float getColorData() {
		csSensor.fetchSample(csData, 0);
		float color = csData[0]*100;	
		
		return color;
	}
	
}
