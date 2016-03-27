package soccerbot;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * USLocalizer performs the tasks required for localizing the robot 
 * using the ultrasonic sensor at startup.
 * 
 * The robot's center of rotation must initially intersect with the corner-to-corner diagonal of the field.
 * This clsss will use the 'rising edges' during wall detection and the diagonal to determine the true 0 degrees.
 * 
 * @see Odometer
 */
public class USLocalizer {
	
	// Constants
	private static final int ROTATION_SPD = 250;
	private static final int D_HIGH = 45;
	private static final int D_LOW = 43;
	private static final int ACC = 6000;
	
	private Odometer odo;
	private Navigation navigate;
	private USPoller usPollerL;
	private USPoller usPollerR;
			
	/**
	 * This constructor needs the odometer to be able to update its heading and a USPoller
	 * to receive its polled data to determine the rising edges. Motors are necessary so that we may rotate 
	 * the robot in place.
	 * 
	 * @param odometer The odometer object that will be used
	 * @param poller Polls the Ultrasonic sensor and retrieves data from it
	 * @param leftMotor This robot's left motor
	 * @param rightMotor This robot's right motor
	 */
	USLocalizer(Odometer odometer,Navigation navigate, USPoller pollerL, USPoller pollerR){
		this.odo = odometer;
		this.navigate = navigate;
		this.usPollerL = pollerL;
		this.usPollerR = pollerR;
	}
	
	/**
	 * This method performs the rising edge localization. It will allow the robot to rotate and detect walls until 
	 * the moment it doesn't. We call this the rising edge. When taking note of the rising edges, we are able to determine
	 * true north and update the odometer to reflect this.
	 */
	public void localize(){
		Robonaldo.loadMotor.stop();  // stop laodMotor from moving while localizing
		
		double deltaT = 0;
		double angleA; double angleB;
		// Temporary variables used to calculate angleA and angleB
		Double angleHigh = null;
		Double angleLow = null;
				
		
		//INITIATE COUNTERCLOCKWISE SEQUENCE
		navigate.setSpeeds(-ROTATION_SPD, ROTATION_SPD, true);
		// If robot starts facing away from a wall, continue to turn until it is facing a wall
		while(usPollerL.getDistance() > D_LOW){
			//empty loop
		}
		// Robot is now facing the wall. Use left USSensor for to detect first rising edge
		while(true){
			
			// find angleHigh and angleLow
			if(usPollerL.getDistance() >= D_LOW && angleHigh == null){
				angleHigh = odo.getTheta();
			}else if(usPollerL.getDistance() >= D_HIGH && angleHigh != null){
				angleLow = odo.getTheta();
			}
			
			// Calculate angleA and break out of loop
			if(angleHigh != null && angleLow != null){
				angleA = (angleHigh+angleLow)/2;
				// reset angleHigh and angleLow
				angleHigh = null;
				angleLow = null;
				
				//Stop motors briefly
				navigate.stopMotors();
				Sound.beep();
				break;
			}
		}
		
		
		// INITIATE CLOCKWISE SEQUENCE
		navigate.setSpeeds(ROTATION_SPD, -ROTATION_SPD, true);
		// Empty loop used to filter out if robot begins facing wall (this is a precautionary step)
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
		while(usPollerR.getDistance() > D_LOW){
			//empty loop	
		}
		while(true){
			// Set high and low angles
			if(usPollerR.getDistance() >= D_LOW && angleHigh == null){
				angleHigh = odo.getTheta();
			}else if(usPollerR.getDistance() >= D_HIGH && angleHigh != null){
				angleLow = odo.getTheta();
			}
			
			// Calculate both angles and break out of loop
			if(angleHigh != null && angleLow != null){
				angleB = (angleHigh+angleLow)/2;		
				angleHigh = null;
				angleLow = null;
				
				//Stop motors briefly
				navigate.stopMotors();
				Sound.beep();
				break;
				
			}
			
		}
		
		
		// Correct robot to approximately 0 deg heading
		if(angleA < angleB){
			deltaT = Math.toRadians(35.125) - (angleA+angleB)/2;
		}else {
			deltaT = Math.toRadians(218.5) - (angleA+angleB)/2;
		}
				
		// Update odometer angle
		navigate.setSpeeds(ROTATION_SPD, ROTATION_SPD, false);
		navigate.turnTo(odo.getTheta()+deltaT+Math.PI/2);		
	}

}
