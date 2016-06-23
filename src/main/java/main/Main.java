package main;

import org.omg.PortableServer.ServantRetentionPolicy;
import processor.*;

import spoon.Launcher;

/**
 * Created by bdanglot on 08/06/16.
 */
public class Main {

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
            UtilPerturbation.perturbableTypes.clear();
            String[] types = args[index + 1].split(":");
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals("IntNum")) {
                    UtilPerturbation.perturbableTypes.add("byte");
                    UtilPerturbation.perturbableTypes.add("short");
                    UtilPerturbation.perturbableTypes.add("int");
                    UtilPerturbation.perturbableTypes.add("Integer");
                    UtilPerturbation.perturbableTypes.add("BigInteger");
                    UtilPerturbation.perturbableTypes.add("long");
                } else {
                    UtilPerturbation.perturbableTypes.add(types[i]);
                }
            }
        }

        System.out.println(UtilPerturbation.perturbableTypes);

        if ( (index = getIndexOfOption("-spoon", args)) ==-1 ) {
            System.err.println("Error : you must provide args for spoon after the -spoon flags");
            System.exit(-1);
        }

        String [] spoonArgs = new String[args.length - index -1];
        System.arraycopy(args, index+1, spoonArgs, 0, args.length-index-1);
        return spoonArgs;
    }


    public static void main(String[] args) {

        String [] spoonArgs = parseArgs(args);

        Launcher spoon = new Launcher();

        spoon.setArgs(spoonArgs);

        spoon.addProcessor(new AssignmentProcessor());
        spoon.addProcessor(new VariableCaster());
        spoon.addProcessor(new PerturbationProcessor());

        if (getIndexOfOption("-r", args) != -1)
            spoon.addProcessor(new RenameProcessor());

        spoon.run();
    }

}
