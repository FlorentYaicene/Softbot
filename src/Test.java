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
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;
import lejos.utility.PilotProps;

public class Test {

	static RegulatedMotor leftMotor;
	static RegulatedMotor rightMotor;
		
	public static void main(String[] args ) throws Exception
	{
		//introMessage();
		
	
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.0"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "18.0"));
    	
    	System.out.println("Wheel diameter is " + wheelDiameter);
    	System.out.println("Track width is " +trackWidth);
    	
    	leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	 
        // Wait for user to press ENTER
    	//System.out.println("Waiting for press");
		//Button.waitForAnyPress();
        robot.setAcceleration(4000);
		robot.setTravelSpeed(20); // cm/sec
		robot.setRotateSpeed(180); // deg/sec
		System.out.println("Going forwards");
		robot.forward();
		
		
//		// get a port instance
//		Port port = LocalEV3.get().getPort("S2");
//
//		// Get an instance of the Ultrasonic EV3 sensor
//		SensorModes sensor = new EV3UltrasonicSensor(port);
//
//		// get an instance of this sensor in measurement mode
//		SampleProvider distance= sensor.getMode("Distance");
//
//		// initialize an array of floats for fetching samples. 
//		// Ask the SampleProvider how long the array should be
//		float[] sample = new float[distance.sampleSize()];
		
//		// fetch a sample
//		while(true) 
//		  distance.fetchSample(sample, 0);

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		long startTime = System.currentTimeMillis();
		long duration;
		
		EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S1);
		SensorMode touch = touchSensor.getTouchMode();
		float[] sample = new float[touch.sampleSize()];
		System.out.println("avant la boucle");

		
		do {
			touch.fetchSample(sample, 0);
			System.out.println("Dans la boucle");
		} while (leftMotor.isMoving() && rightMotor.isMoving()
				&& sample[0] == 0);
		
		System.out.println("Going backwards");
		robot.backward();
		Delay.msDelay(1000);
		robot.stop();
		
	}
   
	public static void introMessage() {
		
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		g.drawString("Pilot Demo", 5, 0, 0);
		g.setFont(Font.getSmallFont());
		g.drawString("Run the PilotParams sample ", 2, 20, 0);
		g.drawString("first to create a properties " , 2, 30, 0);
		g.drawString("file." , 2, 40, 0);
		g.drawString("Requires a wheeled vehicle ", 2, 50, 0);
		g.drawString("with two independant motors.", 2, 60, 0);
		g.drawString("Plug motors into ports B and ", 2, 70, 0);
		g.drawString("C and press enter. ", 2, 80, 0);
		  
		// Quit GUI button:
		g.setFont(Font.getSmallFont()); // can also get specific size using Font.getFont()
		int y_quit = 100;
		int width_quit = 45;
		int height_quit = width_quit/2;
		int arc_diam = 6;
		g.drawString("QUIT", 9, y_quit+7, 0);
		g.drawLine(0, y_quit,  45, y_quit); // top line
		g.drawLine(0, y_quit,  0, y_quit+height_quit-arc_diam/2); // left line
		g.drawLine(width_quit, y_quit,  width_quit, y_quit+height_quit/2); // right line
		g.drawLine(0+arc_diam/2, y_quit+height_quit,  width_quit-10, y_quit+height_quit); // bottom line
		g.drawLine(width_quit-10, y_quit+height_quit, width_quit, y_quit+height_quit/2); // diagonal
		g.drawArc(0, y_quit+height_quit-arc_diam, arc_diam, arc_diam, 180, 90);
		
		// Enter GUI button:
		g.fillRect(width_quit+10, y_quit, height_quit, height_quit);
		g.drawString("GO", width_quit+15, y_quit+7, 0,true);
		
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		g.clear();
	}

}