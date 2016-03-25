package perturbation.perturbator;

/**
 * Created by spirals on 22/03/16.
 */
public class RndPosPerturbatorImpl extends PerturbatorDecorator {

    public RndPosPerturbatorImpl() {
        super(new RndPerturbatorImpl());
    }

    @Override
    public byte pbyte(byte value) {
        byte perturbation = super.pbyte(value);
        return perturbation >= 0 ? perturbation : (byte)(-1 * perturbation);
    }

    @Override
    public short pshort(short value) {
        short perturbation = (short) (new java.util.Random().nextInt());
        return perturbation >= 0 ? perturbation : (short)(-1 * perturbation);
    }

    @Override
    public int pint(int value) {
        int perturbation = super.pint(value);
        return perturbation >= 0 ? perturbation : -1 * perturbation;
    }

    @Override
    public long plong(long value) {
        long perturbation = plong(value);
        return perturbation >= 0 ? perturbation : -1 * perturbation;
    }

    @Override
    public char pchar(char value) {
        return super.pchar(value);
    }

    @Override
    public float pfloat(float value) {
        float perturbation = super.pfloat(value);
        return perturbation >= 0 ? perturbation: -1 * perturbation;
    }

    @Override
    public double pdouble(double value) {
        double perturbation = super.pdouble(value);
        return perturbation >= 0 ? perturbation: -1 * perturbation;
    }

    @Override
    public String toString() {
        return "Rnd+";
    }


}
