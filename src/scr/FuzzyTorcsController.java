package scr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class FuzzyTorcsController extends Controller {

    final double targetSpeed = 30;
    final int samplingDistance = 5;
    static List<Record> registry = getFromFile("Registre.torc");

    public Action control(SensorModel sensorModel) {

        Action action = new Action();

        action.gear = 2;

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

    public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		System.out.println("Apagant Fuzzy Controller");
	}
}
