package soccerbot;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
/**
 * Robonaldo instantiates all the threads and operations required at start-up of the program.
 * The initial parameters fed into the robot will be received in this class and be passed along to the rest of
 * the system. 
 */
public class Robonaldo {
	
	public static final EV3LargeRegulatedMotor loadMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor launchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	public static double WIDTH = 18.2;
	public static double RADIUS = 2.096;

	
	
	//MAIN METHOD
	public static void main(String[] args){
		
		Odometer odo = new Odometer(leftMotor, rightMotor);
		Screen screen = new Screen(odo);
		odo.start();
		screen.start();
		
		// 13.68, 2.096
		leftMotor.setSpeed(140); rightMotor.setSpeed(140);
		
		for(int k=0; k<4; k++){
			leftMotor.rotate(-convertDistance(RADIUS, 60.48), true);
			rightMotor.rotate(-convertDistance(RADIUS, 60.48), false);
			
			leftMotor.rotate(-convertAngle(RADIUS, WIDTH, Math.PI/2), true);
			rightMotor.rotate(convertAngle(RADIUS, WIDTH, Math.PI/2), false);
		}
		
	
		/*loadMotor.setSpeed(120);
		loadMotor.rotateTo(-110);
		loadMotor.stop();
		launchMotor.setAcceleration(2000000000);
		launchMotor.setSpeed(200);
		launchMotor.rotate(360);
		
		
		
		int button = Button.waitForAnyPress();
		while(button == Button.ID_ENTER){
			launchMotor.stop();
			loadMotor.setAcceleration(150);			
			loadMotor.rotateTo(110);
			loadMotor.setSpeed(70);
			loadMotor.rotateTo(-110);
			loadMotor.stop();
			
			try{
				Thread.sleep(2000);
			}catch(Exception e){};
			
			launchMotor.setSpeed(700);
			launchMotor.rotate(-300);
			button = Button.waitForAnyPress();
			launchMotor.rotateTo(300);
		}*/
		
		
		
		
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
