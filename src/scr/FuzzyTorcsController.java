package scr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class FuzzyTorcsController extends Controller {

    public static int M = 25; //Aproximadament un registre cada 5 metres
    public static int X = 5;

    double distanceFromStart;
    final double targetSpeed = 30;
    final int samplingDistance = 5;
    static List<Record> registry = getFromFile("Registre.torc");

    List<Record> mean_vector = new ArrayList<>();
    List<Record> accumulated_vector = new ArrayList<>();

    public Action control(SensorModel sensorModel) {
        // Load from 'FCL' file
        String fileName = "fcl/fuzzylogic.fcl";
        FIS fis = FIS.load(fileName,true);

        if( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return null;
        }

        Action action = new Action();

        if(sensorModel.getDistanceRaced()<400){
            action.accelerate=1;
        }
        else{
            action.accelerate=0;
            action.brake=0.5;
        }

        // Set Variables
        FunctionBlock gearBlock = fis.getFunctionBlock("gear");
        gearBlock.setVariable("rpm", sensorModel.getRPM());
        gearBlock.setVariable("accelerate", action.accelerate);

        // Evaluate
        gearBlock.evaluate();
//        fis.evaluate();

        // Show output variable's chart
        int gear = (int) gearBlock.getVariable("outgear").getValue();
        action.gear = sensorModel.getGear()+gear;
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
