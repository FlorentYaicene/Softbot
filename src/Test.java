import java.util.concurrent.TimeUnit;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.RangeFinderAdapter;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;
import lejos.utility.PilotProps;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Test {

	static RegulatedMotor leftMotor;
	static RegulatedMotor rightMotor;
		
	public static void main(String[] args ) throws Exception
	{	    	
		final RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		final RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
    	final DifferentialPilot robot = new DifferentialPilot(5.6f, 11.8f, leftMotor, rightMotor, false);
    	 
		robot.setTravelSpeed(20); // cm/sec
		
		
		float distance=10000;
		int cpt=0;
		
		//Touch sensor
		final EV3TouchSensor touchSensor1 = new EV3TouchSensor(SensorPort.S1);
		final EV3TouchSensor touchSensor2 = new EV3TouchSensor(SensorPort.S2);
		final SensorMode touch1 = touchSensor1.getTouchMode();
		final SensorMode touch2 = touchSensor2.getTouchMode();
		final float[] sampleTouch = new float[2];
		boolean run = true;
		
		//Ultrason (mur gauche)		 
		EV3UltrasonicSensor ir = new EV3UltrasonicSensor(SensorPort.S4);
		final SampleProvider sampleProvider=ir.getDistanceMode();
		float[] sampleUS = new float[sampleProvider.sampleSize()];
		
		robot.forward();
		
		do {		
			//Ultra son
			sampleProvider.fetchSample(sampleUS, 0);
			
			//distance en metre
			System.out.println("sample " + sampleUS[0]);
			Delay.msDelay(500);
			System.out.println("distance " + distance);
			Delay.msDelay(500);
			
			//touch sensor
			touch1.fetchSample(sampleTouch, 0);
			touch2.fetchSample(sampleTouch, 1);
			
			//A côté du mur
			if(((sampleUS[0]>=distance-0.1 && sampleUS[0]<=distance+0.1) || cpt==0) && (sampleTouch[0] == 0 && sampleTouch[1] == 0) && !robot.isMoving()) {
				System.out.println("Is moving");
				robot.forward();
			}
			
			if(sampleUS[0]>distance+0.11 && cpt!=0) {
				System.out.println("N'est plus contre le mur / distance :" + distance);
				robot.stop();
				robot.arc(10, 90);
				cpt++;
			}
			
			if(sampleTouch[0] != 0 || sampleTouch[1] != 0) {
				System.out.println("stop toi");
				robot.stop();
				//Go backwards and turn right
				robot.arc(10, -90);
				cpt--;				
				if(!robot.isMoving()) {
					System.out.println("Sleep");
					TimeUnit.SECONDS.sleep(3);
					distance=sampleUS[0];
				}				
			}		
			
		} while (Button.ESCAPE.isUp());
	}
}