package perturbation.log;

import perturbation.PerturbationLocation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by beyni on 28/03/16.
 */
public class Logger {

    static {
        eraseLogFile();
    }

    private static void eraseLogFile() {
        FileWriter writer;
        try {
            writer = new FileWriter("log/LogLocations", false);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logLocationOfClass(Class clazz) {
        List<PerturbationLocation> listOfLocations = PerturbationLocation.getLocationFromClass(clazz);

        String str = "\\begin{tabular}{c|c|c|c|l}\\\\\n";
        str += "Index & Location & \\#Call & \\#Activation & \\%Success\\\\\n";
        str += "\\hline\n";

        for (PerturbationLocation location : listOfLocations) {
            str += location.getLocationIndex() + "&" + location.getLocationInCode() + "&";
            str += location.numberOfCall + "&" + location.numberOfActivation + "&";
            double perc = ((double)location.numberOfSuccess / (double)(location.numberOfSuccess + location.numberOfFailure)) * 100;
            int numberOfDash = (int)perc / 5;
            for (int i = 0 ; i < numberOfDash ; i++) str += "-";
            str += " " + String.format("%.2f", perc)+"\\\\\n";
        }

        str += "\\end{tabular}\\\\\n";

        writeToFile(str, "log/LogLocations");
    }

    public static void writeToFile(String strToWrite, String pathFile) {
        FileWriter writer;
        try {
            writer = new FileWriter(pathFile, true);
            writer.write(strToWrite);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
