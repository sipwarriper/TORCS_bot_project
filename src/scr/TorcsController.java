package scr;
import java.io.*; //Aquest * l'ha posat l'IDE amb alt + enter aixi que entenc que és segur
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TorcsController extends Controller {

    public static int M = 25; //Aproximadament un registre cada 5 metres
    public static int X = 5;

    final double targetSpeed = 32;
    final double targetSteering = 0.25;
    final double targetTrackPos = 0.1;
    final double targetSteering2 = 0.2;
    final int samplingDistance = 1;
    static double dist=0;
    static List<Record> registry = new ArrayList<>();
    final String registryFileName = "Registre.torc";

    List<Record> mean_vector = new ArrayList<>();
    List<Record> accumulated_vector = new ArrayList<>();

    public Action control(SensorModel sensorModel) {

       /* double distanceFromStart = sensorModel.getDistanceFromStartLine();

        if(dist == 0) dist = distanceFromStart;

        if(dist > distanceFromStart) dist = distanceFromStart; //la distancia des de la linia de sortida no creix sempre.
        */
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

        //if(distanceFromStart > dist + samplingDistance){
            registry.add(new Record(sensorModel,action));
            //System.out.println("Registre guardat. Distancia recorreguda: " + dist + " " + distanceFromStart);
           // dist = distanceFromStart;
       // }

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

	private void dumpFile(String nomFitxer){
        try {
            FileOutputStream fos = new FileOutputStream(nomFitxer);
            PrintWriter out = new PrintWriter(nomFitxer+"v");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(registry);
            oos.close();

            for(Record r: registry){
                out.println("[{Angle: " + String.valueOf(r.AngleToTrackAxis) + "}"
                        + "{DistRaced: " + String.valueOf(r.DistanceRaced) + "}"
                        + "{DistanceFromStartLine: " + String.valueOf(r.DistanceFromStartLine) + "}"
                        + "{Steering: " + String.valueOf(r.steering) + "}]");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        dumpFile(registryFileName);
	}
}
