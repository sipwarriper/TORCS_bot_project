package scr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class FuzzyTorcsController extends Controller {

    double distanceFromStart;
    final double targetSpeed = 30;
    final int samplingDistance = 5;
    static List<Record> registry = getFromFile("Registre.torc");

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

    public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		System.out.println("Apagant Fuzzy Controller");
	}
}
