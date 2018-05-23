package scr;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 4:59:21 PM

 */

public class Record implements Serializable {

    public final long serialVersionUID = 1L;

    //public Integer Speed;
    public double AngleToTrackAxis;
   /* public double[] TrackEdgeSensors;
    public double[] FocusSensors;//ML
    public double TrackPosition;
    public int Gear;
    public double[] OpponentSensors;
    public int RacePosition;
    public double LateralSpeed;
    public double CurrentLapTime;
    public double Damage;*/
    public double DistanceFromStartLine;
    public double steering;
    public double DistanceRaced;
    /*public double FuelLevel;
    public double LastLapTime;
    public double RPM;
    public double[] WheelSpinVelocity;
    public double ZSpeed;
    public double Z;*/

    public Record(){
        DistanceFromStartLine = 0;
        steering = 0;
        DistanceRaced = 0;
        AngleToTrackAxis = 0;
    }

    public Record(SensorModel sensor, Action action){
            //Speed = new Double(sensor.getSpeed()).intValue();
            AngleToTrackAxis = sensor.getAngleToTrackAxis();
            /*TrackEdgeSensors = sensor.getTrackEdgeSensors();
            FocusSensors = sensor.getFocusSensors();
            TrackPosition = sensor.getTrackPosition();
            Gear = sensor.getGear();
            OpponentSensors = sensor.getOpponentSensors();
            RacePosition = sensor.getRacePosition();
            LateralSpeed = sensor.getLateralSpeed();
            CurrentLapTime = sensor.getCurrentLapTime();
            Damage = sensor.getDamage();*/
            DistanceFromStartLine = sensor.getDistanceFromStartLine();
            steering = action.steering;
            DistanceRaced = sensor.getDistanceRaced();
            /*FuelLevel = sensor.getFuelLevel();
            LastLapTime = sensor.getLastLapTime();
            this.RPM = sensor.getRPM();
            WheelSpinVelocity = sensor.getWheelSpinVelocity();
            this.ZSpeed = sensor.getZSpeed();
            Z = sensor.getZ();*/
    }

    public void addValues(Record r) {
        this.AngleToTrackAxis += r.AngleToTrackAxis;
        if(this.DistanceRaced == 0) this.DistanceRaced = r.DistanceRaced;
        this.steering += r.steering;
        if(this.DistanceFromStartLine == 0) this.DistanceFromStartLine = r.DistanceFromStartLine;
    }

    public void divideValues(double factor){
        //this.DistanceFromStartLine /= factor;
        //this.DistanceRaced /= factor;
        this.steering /= factor;
        this.AngleToTrackAxis /= factor;
    }
}
