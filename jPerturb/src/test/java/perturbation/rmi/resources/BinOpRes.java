package perturbation.rmi.resources;

// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)


import perturbation.PerturbationEngine;
import perturbation.location.PerturbationLocation;
import perturbation.location.PerturbationLocationImpl;

public class BinOpRes {
    static {
        BinOpRes.initPerturbationLocation0();
    }

    public static PerturbationLocation __L0;

    public static PerturbationLocation __L1;

    public static PerturbationLocation __L2;

    public static PerturbationLocation __L3;

    public static PerturbationLocation __L4;

    public static PerturbationLocation __L5;

    public static PerturbationLocation __L6;

    public static PerturbationLocation __L7;

    public static PerturbationLocation __L8;

    public static PerturbationLocation __L9;

    public static PerturbationLocation __L10;

    public static PerturbationLocation __L11;

    public static PerturbationLocation __L12;

    public static PerturbationLocation __L13;

    public static PerturbationLocation __L14;

    public static PerturbationLocation __L15;

    public static PerturbationLocation __L16;

    public static PerturbationLocation __L17;

    public static PerturbationLocation __L18;

    public static PerturbationLocation __L19;

    public static PerturbationLocation __L20;

    public boolean and(boolean a, boolean b) {
        return PerturbationEngine.pboolean(BinOpRes.__L2, ((PerturbationEngine.pboolean(BinOpRes.__L0, a)) && (PerturbationEngine.pboolean(BinOpRes.__L1, b))));
    }

    public boolean or(boolean a, boolean b) {
        return PerturbationEngine.pboolean(BinOpRes.__L5, ((PerturbationEngine.pboolean(BinOpRes.__L3, a)) || (PerturbationEngine.pboolean(BinOpRes.__L4, b))));
    }

    public int plus(int a, int b) {
        return PerturbationEngine.pint(BinOpRes.__L20, ((PerturbationEngine.pint(BinOpRes.__L18, a)) + (PerturbationEngine.pint(BinOpRes.__L19, b))));
    }

    public int minus(int a, int b) {
        return PerturbationEngine.pint(BinOpRes.__L11, ((PerturbationEngine.pint(BinOpRes.__L9, a)) - (PerturbationEngine.pint(BinOpRes.__L10, b))));
    }

    public int multiply(int a, int b) {
        return PerturbationEngine.pint(BinOpRes.__L17, ((PerturbationEngine.pint(BinOpRes.__L15, a)) * (PerturbationEngine.pint(BinOpRes.__L16, b))));
    }

    public int divide(int a, int b) {
        return PerturbationEngine.pint(BinOpRes.__L8, ((PerturbationEngine.pint(BinOpRes.__L6, a)) / (PerturbationEngine.pint(BinOpRes.__L7, b))));
    }

    public int modulo(int a, int b) {
        return PerturbationEngine.pint(BinOpRes.__L14, ((PerturbationEngine.pint(BinOpRes.__L12, a)) % (PerturbationEngine.pint(BinOpRes.__L13, b))));
    }

    static private void initPerturbationLocation0() {
        BinOpRes.__L0 = new PerturbationLocationImpl("BinOpRes.java:4", 0, "Boolean");
        BinOpRes.__L1 = new PerturbationLocationImpl("BinOpRes.java:4", 1, "Boolean");
        BinOpRes.__L2 = new PerturbationLocationImpl("BinOpRes.java:4", 2, "Boolean");
        BinOpRes.__L3 = new PerturbationLocationImpl("BinOpRes.java:8", 3, "Boolean");
        BinOpRes.__L4 = new PerturbationLocationImpl("BinOpRes.java:8", 4, "Boolean");
        BinOpRes.__L5 = new PerturbationLocationImpl("BinOpRes.java:8", 5, "Boolean");
        BinOpRes.__L6 = new PerturbationLocationImpl("BinOpRes.java:24", 6, "Numerical");
        BinOpRes.__L7 = new PerturbationLocationImpl("BinOpRes.java:24", 7, "Numerical");
        BinOpRes.__L8 = new PerturbationLocationImpl("BinOpRes.java:24", 8, "Numerical");
        BinOpRes.__L9 = new PerturbationLocationImpl("BinOpRes.java:16", 9, "Numerical");
        BinOpRes.__L10 = new PerturbationLocationImpl("BinOpRes.java:16", 10, "Numerical");
        BinOpRes.__L11 = new PerturbationLocationImpl("BinOpRes.java:16", 11, "Numerical");
        BinOpRes.__L12 = new PerturbationLocationImpl("BinOpRes.java:28", 12, "Numerical");
        BinOpRes.__L13 = new PerturbationLocationImpl("BinOpRes.java:28", 13, "Numerical");
        BinOpRes.__L14 = new PerturbationLocationImpl("BinOpRes.java:28", 14, "Numerical");
        BinOpRes.__L15 = new PerturbationLocationImpl("BinOpRes.java:20", 15, "Numerical");
        BinOpRes.__L16 = new PerturbationLocationImpl("BinOpRes.java:20", 16, "Numerical");
        BinOpRes.__L17 = new PerturbationLocationImpl("BinOpRes.java:20", 17, "Numerical");
        BinOpRes.__L18 = new PerturbationLocationImpl("BinOpRes.java:12", 18, "Numerical");
        BinOpRes.__L19 = new PerturbationLocationImpl("BinOpRes.java:12", 19, "Numerical");
        BinOpRes.__L20 = new PerturbationLocationImpl("BinOpRes.java:12", 20, "Numerical");
    }
}

