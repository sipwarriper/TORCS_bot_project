package scr;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 4:59:21 PM

 */

public class Record implements Serializable {

    public Integer Speed;
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
            Speed = new Double(sensor.getSpeed()).intValue();
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