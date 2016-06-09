package main;

import processor.*;

import spoon.processing.AbstractProcessor;
import spoon.Launcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdanglot on 08/06/16.
 */
public class Main {

    private static List<AbstractProcessor> processors = new ArrayList<>();

    private static int getIndexOfOption(String opt, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(opt))
                return i;
        }
        return -1;
    }

    private static String[] parseArgs(String[] args) {

        int index;
        if ((index = getIndexOfOption("-type", args)) != -1) {
            String[] types = args[index + 1].split(":");
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals("Integer")) {
                    UtilPerturbation.perturbableTypes.add("byte");
                    UtilPerturbation.perturbableTypes.add("short");
                    UtilPerturbation.perturbableTypes.add("int");
//                    UtilPerturbation.perturbableTypes.add("Integer");
                    UtilPerturbation.perturbableTypes.add("BigInteger");
                    UtilPerturbation.perturbableTypes.add("long");
                } else {
                    UtilPerturbation.perturbableTypes.add(types[i]);
                }
            }
        }

        System.out.println(UtilPerturbation.perturbableTypes);

        if ((index = getIndexOfOption("-r", args)) != -1)
            processors.add(new RenameProcessor());

        processors.add(new AssignmentProcessor());
        processors.add(new VariableCaster());
        processors.add(new PerturbationProcessor());

        index = getIndexOfOption("-spoon", args);

        String [] spoonArgs = new String[args.length - index -1];
        System.arraycopy(args, index+1, spoonArgs, 0, args.length-index-1);
        return spoonArgs;
    }


    public static void main(String[] args) {

        String [] spoonArgs = parseArgs(args);

        Launcher spoon = new Launcher();

        spoon.setArgs(spoonArgs);
        for (AbstractProcessor processor : processors)
            spoon.addProcessor(processor);

        spoon.run();
    }

}
