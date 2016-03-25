package perturbation.perturbator;

/**
 * Created by spirals on 24/03/16.
 */
public class PerturbatorDecorator implements Perturbator {

    protected Perturbator decoratedPerturbator;

    public PerturbatorDecorator(Perturbator decoratedPerturbator) {
        this.decoratedPerturbator = decoratedPerturbator;
    }

    @Override
    public boolean pboolean(boolean value) {
        return this.decoratedPerturbator.pboolean(value);
    }

    @Override
    public byte pbyte(byte value) {
        return this.decoratedPerturbator.pbyte(value);
    }

    @Override
    public short pshort(short value) {
        return this.decoratedPerturbator.pshort(value);
    }

    @Override
    public int pint(int value) {
        return this.decoratedPerturbator.pint(value);
    }

    @Override
    public long plong(long value) {
        return this.decoratedPerturbator.plong(value);
    }

    @Override
    public char pchar(char value) {
        return this.decoratedPerturbator.pchar(value);
    }

    @Override
    public float pfloat(float value) {
        return this.decoratedPerturbator.pfloat(value);
    }

    @Override
    public double pdouble(double value) {
        return this.decoratedPerturbator.pdouble(value);
    }

    @Override
    public String toString() {
        return this.decoratedPerturbator.toString();
    }
}
