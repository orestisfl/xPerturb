package perturbation.perturbator;

/**
 * Created by spirals on 22/03/16.
 */
public class AddOnePerturbatorImpl extends PerturbatorDecorator {

    public AddOnePerturbatorImpl(Perturbator innerPerturbator) {
        super(innerPerturbator);
    }

    @Override
    public byte pbyte(byte value) {
        return (byte)(value + 1);
    }

    @Override
    public short pshort(short value) {
        return (short)(value + 1);
    }

    @Override
    public int pint(int value) {
        return value + 1;
    }

    @Override
    public long plong(long value) {
        return value + 1;
    }

    @Override
    public char pchar(char value) {
        return (char)(value + 1);
    }

    @Override
    public float pfloat(float value) {
        return value + 1;
    }

    @Override
    public double pdouble(double value) {
        return value + 1;
    }

    @Override
    public String toString() {
        return "PONE";
    }
}
