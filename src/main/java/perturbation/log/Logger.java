package perturbation.log;

import perturbation.PerturbationEngine;
import perturbation.PerturbationLocation;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by spirals on 28/03/16.
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

    public static void logHeaderLatex() {
        String str = PerturbationEngine.print().replaceAll("_", "\\_")+"\\\\\n";
        str += "\\begin{tabular}{c|c|c|c|c|l}\\\\\n";
        str += "Index & Location & Type & \\#Call & \\#Activation & \\%Success\\\\\n";
        str += "\\hline\n";
        writeToFile(str, "log/LogLocations");
    }

    public static void logOnePerturbationLocationLatex(PerturbationLocation location) {
        String str = location.getLocationIndex() + "&" + location.getLocationInCode() + "&";
        str += location.getType() + "&";
        str += location.numberOfCall + "&" + location.numberOfActivation + "&";
        double perc = ((double)location.numberOfSuccess / (double)(location.numberOfSuccess + location.numberOfFailure)) * 100;
        int numberOfDash = (int)perc / 5;
        for (int i = 0 ; i < numberOfDash ; i++) str += "-";
        str += " " + String.format("%.2f", perc)+"\\\\\n";
        writeToFile(str, "log/LogLocations");
    }

    public static void logFooterLatex() {
        writeToFile("\\end{tabular}\\\\\n", "log/LogLocations");
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
