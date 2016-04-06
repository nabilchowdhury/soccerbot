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

public class LightLocalizer{
	private Odometer odo;
	private Navigation navigate;
	
	private LSPoller lsPoller;
	
	private int startingCorner;
	
	
	public LightLocalizer(Odometer odo, Navigation navigate, LSPoller lsPoller, int startingCorner) {
		this.odo = odo;
		this.navigate = navigate;
		this.lsPoller = lsPoller;
		this.startingCorner = startingCorner;
		
	}
	
	// Initiate light localization
	public void localize() {
		navigate.goStraight(280,280, -23);
		
		preventTwitch();
		
		navigate.setSpeeds(200, 200, true, 2000);
		
		while(true){
			if(lsPoller.getDifferentialData() > 0.13){
				navigate.stopMotors();
				Sound.beep();
				break;
			}
		}
		
		preventTwitch();
		
		navigate.goStraight(200, 200, 2.5);
		
		preventTwitch();
		
		navigate.setSpeeds(200, 200, false, 2000);
		navigate.turnTo(Math.PI/2);
		
		preventTwitch();
		
		navigate.goStraight(200, 200, -8);
		
		preventTwitch();
		
		navigate.setSpeeds(220, 220, true, 2000);
		
		while(true){
			if(lsPoller.getDifferentialData() > 0.13){
				navigate.stopMotors();
				Sound.beep();
				break;
			}
		}
		
		preventTwitch();
		
		navigate.goStraight(220, 220, 9.6);
		
		preventTwitch();
		
		navigate.setSpeeds(-180, 180, true, 6000);
		
		int count = 0;
		
		while(true){
			if(count == 2){
				navigate.stopMotors();
				break;
			}
			if(lsPoller.getDifferentialData() > 0.13){
				count++;
				Sound.beep();
			}
		}
		
		
		navigate.setSpeeds(150, 150, false, 6000);
		
		navigate.turnTo(Math.toRadians(-15.8));
		
		preventTwitch();
		
		navigate.goStraight(150, 150, 7.25);	
		
		preventTwitch();
		

		// fix for actual competition
		if(this.startingCorner == 1){
			double[] position = {0.0, 0.0, 0.0};
			boolean[] update = {true, true, true};
			odo.setPosition(position, update);
		}else if(this.startingCorner == 2){
			double[] position = {6*30.45, 0.0, 3*Math.PI/2};
			boolean[] update = {true, true, true};
			odo.setPosition(position, update);
		}else if(this.startingCorner == 3){
			double[] position = {6*30.45, 6*30.45, Math.PI};
			boolean[] update = {true, true, true};
			odo.setPosition(position, update);
		}else if(this.startingCorner == 4){
			double[] position = {0.0, 6*30.45, Math.PI/2};
			boolean[] update = {true, true, true};
			odo.setPosition(position, update);
		}
		
		Sound.beep();
		
	}
	
	public void preventTwitch(){
		navigate.stopMotors();
		try{
			Thread.sleep(50);
		}catch(Exception e){}
	}
	
	
}
