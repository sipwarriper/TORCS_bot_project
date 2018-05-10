package scr;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 4:59:21 PM

 */

public class TorcsController extends Controller {

    final double targetSpeed = 30;
    final double targetSteering = 0.25;
    final double targetTrackPos = 0.05;
    final double targetSteering2 = 0.1;
    final int samplingDistance = 5;
    static double dist=0;
    static List<Record> registry = new ArrayList<>();
    final String registryFileName = "Registre.torc";

    class Record implements Serializable{
        public double Speed;
        public double AngleToTrackAxis;
        public double[] TrackEdgeSensors;
        public double[] FocusSensors;//ML
        public double TrackPosition;
        public int Gear;
        public double[] OpponentSensors;
        public int RacePosition;
        public double LateralSpeed;
        public double CurrentLapTime;
        public double Damage;
        public double DistanceFromStartLine;
        public double DistanceRaced;
        public double FuelLevel;
        public double LastLapTime;
        public double RPM;
        public double[] WheelSpinVelocity;
        public double ZSpeed;
        public double Z;

        public Record(SensorModel sensor){
            Speed = sensor.getSpeed();
            AngleToTrackAxis = sensor.getAngleToTrackAxis();
            TrackEdgeSensors = sensor.getTrackEdgeSensors();
            FocusSensors = sensor.getFocusSensors();
            TrackPosition = sensor.getTrackPosition();
            Gear = sensor.getGear();
            OpponentSensors = sensor.getOpponentSensors();
            RacePosition = sensor.getRacePosition();
            LateralSpeed = sensor.getLateralSpeed();
            CurrentLapTime = sensor.getCurrentLapTime();
            Damage = sensor.getDamage();
            DistanceFromStartLine = sensor.getDistanceFromStartLine();
            DistanceRaced = sensor.getDistanceRaced();
            FuelLevel = sensor.getFuelLevel();
            LastLapTime = sensor.getLastLapTime();
            this.RPM = sensor.getRPM();
            WheelSpinVelocity = sensor.getWheelSpinVelocity();
            this.ZSpeed = sensor.getZSpeed();
            Z = sensor.getZ();
        }
    }

    public Action control(SensorModel sensorModel) {

        double distanceFromStart = sensorModel.getDistanceFromStartLine();

        if(dist == 0) dist = distanceFromStart;

        if(dist > distanceFromStart) dist = distanceFromStart; //la distancia des de la linia de sortida no creix sempre.

        if(distanceFromStart > dist + samplingDistance){
            registry.add(new Record(sensorModel));
            System.out.println("Registre guardat. Distancia recorreguda: " + dist + " " + distanceFromStart);
            dist = distanceFromStart;
        }

        Action action = new Action ();

        if (sensorModel.getSpeed () < targetSpeed) {
            action.accelerate = 1;
        }

        if(sensorModel.getTrackPosition() > targetTrackPos){
            action.steering += -targetSteering;
        }
        else if(sensorModel.getTrackPosition() < -targetTrackPos){
            action.steering += targetSteering;
        }
        else{
            //girem per mantenir-nos paral·lels a la trajectoria de la pista
            if (sensorModel.getAngleToTrackAxis() < -0.2) {
                action.steering += -targetSteering2;
            }
            else if(sensorModel.getAngleToTrackAxis() > 0.2){
                action.steering += targetSteering2;
            }
            else {
                action.steering = 0;
            }
        }

        action.gear = 2;

        return action;
    }

    /*
    public Action control(SensorModel sensorModel) {

        Action action = new Action ();
        if (sensorModel.getSpeed () < targetSpeed) {
            action.accelerate = 1;
        }
        if (sensorModel.getAngleToTrackAxis() < 0) {
            action.steering = -0.1;
        }
        else {
            action.steering = 0.1;
        }
        action.gear = 1;
        return action;
    }*/

    public void reset() {
		System.out.println("Restarting the race!");
	}

	private void dumpFile(){
        try {
            FileOutputStream fos = new FileOutputStream(registryFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(registry);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFromFile(){
        try {
            FileInputStream fis = new FileInputStream(registryFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            registry = (List<Record>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void shutdown() {
		System.out.println("Volcant tota la RAM");
        dumpFile();
	}
}
