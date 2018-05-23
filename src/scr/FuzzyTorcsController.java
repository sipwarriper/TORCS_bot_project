package scr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class FuzzyTorcsController extends Controller {

    public static int M = 25; //Aproximadament un registre cada 5 metres
    public static int X = 5;

    /*double distanceFromStart;
    final double targetSpeed = 30;
    final int samplingDistance = 5;*/
    static List<Record> registry = getFromFile("Registre.torc");

    List<Record> mean_vector = new ArrayList<>();
    List<Record> accumulated_vector = new ArrayList<>();

    public Action control(SensorModel sensorModel) {

        Action action = new Action();

        //Inicialitzar fuzzylogic
        String fileName = "fcl/fuzzylogic.fcl";
        FIS fis = FIS.load(fileName,true);

        if( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return null;
        }

        if(sensorModel.getDistanceRaced()<400){
            action.accelerate=1;
        }
        else{
            action.accelerate=0;
            action.brake=0.5;
        }

        action.steering = girar_temporal(sensorModel); //steering temporal sense fuzzy

        // Set Variables
        FunctionBlock gearBlock = fis.getFunctionBlock("gear");
        gearBlock.setVariable("rpm", sensorModel.getRPM());
        gearBlock.setVariable("accelerate", action.accelerate);

        /*FunctionBlock acceleration = fis.getFunctionBlock("acceleration");
        acceleration.setVariable("distNextTurn", );
        acceleration.setVariable("angle", );
        acceleration.setVariable("speed", );
        acceleration.setVariable("currentTurn", );*/

        // Evaluate
        gearBlock.evaluate();

        int gear = (int) gearBlock.getVariable("outgear").getValue();
        /*if(!(sensorModel.getGear() == 1 && gear == -1))*/
        action.gear = sensorModel.getGear() + gear;
        if(action.gear == 0) action.gear = 1;

        /*double accel = acceleration.getVariable("accel").getValue();
        if(accel < 0) action.brake = -accel;
        else action.accelerate = accel;*/

        return action;
    }

    static private List<Record> getFromFile(String filename){
        List<Record> read_registry = null;

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            read_registry = (ArrayList<Record>) ois.readObject();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return read_registry;
    }

    private double girar_temporal(SensorModel sensorModel){
        final double targetSteering = 0.25;
        final double targetTrackPos = 0.1;
        final double targetSteering2 = 0.2;

        double steering = 0;

        if(sensorModel.getTrackPosition() > targetTrackPos){
            steering += -targetSteering;
        }
        else if(sensorModel.getTrackPosition() < -targetTrackPos){
            steering += targetSteering;
        }
        else{
            //girem per mantenir-nos paral·lels a la trajectoria de la pista
            if (sensorModel.getAngleToTrackAxis() < -0.2) {
                steering += -targetSteering2;
            }
            else if(sensorModel.getAngleToTrackAxis() > 0.2){
                steering += targetSteering2;
            }
            else {
                steering = 0;
            }
        }

        return steering;
    }
    private void normalize(List<Record> registry) {
        double min=Double.POSITIVE_INFINITY, max=Double.NEGATIVE_INFINITY;
        for(Record r: registry){
            if(min > r.AngleToTrackAxis) min = r.AngleToTrackAxis;
            if(max < r.AngleToTrackAxis) max = r.AngleToTrackAxis;
        }
        for(Record r: registry){
            if(r.AngleToTrackAxis > 0) r.AngleToTrackAxis = r.AngleToTrackAxis/max;
            else r.AngleToTrackAxis = r.AngleToTrackAxis/(-min); //-min per deixar el valor en negatiu
        }
    }

    private List<Record> getMeanVector(List<Record> registry) {
        List<Record> rl = new ArrayList<>();
        Iterator<Record> it = registry.iterator();
        while(it.hasNext()){
            int i = 0;
            Record r = new Record();
            while(i<M && it.hasNext()){
                r.addValues(it.next());
                i++;
            }
            r.divideValues(M);
            rl.add(r);
        }

        return rl;
    }

    private List<Record> getAccumulatedVector(List<Record> registry) {
        List<Record> rl = new ArrayList<>();
        Iterator<Record> it = registry.iterator();
        while(it.hasNext()){
            int i = 0;
            Record r = new Record();
            while(i<X && it.hasNext()){
                r.addValues(it.next());
                i++;
            }
            rl.add(r);
        }

        return rl;
    }

    //aquest metode fica a 0 els valors de angle i steering del vector dels 'dist' primers metres (errors de mesura)
    private void correctVector(List<Record> l, double dist){
        Iterator<Record> it = l.iterator();
        Record r = it.next();
        while(it.hasNext() && ( r.DistanceRaced < dist) ){
            r.steering = 0;
            r.AngleToTrackAxis = 0;
            r = it.next();
        }
    }

    public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		System.out.println("Apagant Fuzzy Controller");
	}
}
