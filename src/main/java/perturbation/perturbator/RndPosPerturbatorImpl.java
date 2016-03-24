package perturbation.perturbator;

/**
 * Created by spirals on 22/03/16.
 */
public class RndPosPerturbatorImpl extends RndPerturbatorImpl {

    @Override
    public byte pbyte(byte value) {
        byte perturbation = (byte) (new java.util.Random().nextInt());
        return perturbation >= 0 ? perturbation : (byte)(-1 * perturbation);
    }

    @Override
    public short pshort(short value) {
        short perturbation = (short) (new java.util.Random().nextInt());
        return perturbation >= 0 ? perturbation : (short)(-1 * perturbation);
    }

    @Override
    public int pint(int value) {
        int perturbation = (new java.util.Random().nextInt());
        return perturbation >= 0 ? perturbation : -1 * perturbation;
    }

    @Override
    public long plong(long value) {
        long perturbation = new java.util.Random().nextLong();
        return perturbation >= 0 ? perturbation : -1 * perturbation;
    }

    @Override
    public char pchar(char value) {
        return (char) (new java.util.Random().nextInt());
    }

    @Override
    public float pfloat(float value) {
        float perturbation = new java.util.Random().nextFloat();
        return perturbation >= 0 ? perturbation: -1 * perturbation;
    }

    @Override
    public double pdouble(double value) {
        double perturbation = new java.util.Random().nextFloat();
        return perturbation >= 0 ? perturbation: -1 * perturbation;
    }

    @Override
    public String toString() {
        return "Rnd+";
    }


}
