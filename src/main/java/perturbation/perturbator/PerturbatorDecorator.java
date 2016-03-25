package perturbation.perturbator;

/**
 * Created by spirals on 24/03/16.
 */
public class PerturbatorDecorator implements Perturbator {

    protected Perturbator innerPerturbator;

    public PerturbatorDecorator(Perturbator innerPerturbator) {
        this.innerPerturbator = innerPerturbator;
    }

    @Override
    public boolean pboolean(boolean value) {
        return this.innerPerturbator.pboolean(value);
    }

    @Override
    public byte pbyte(byte value) {
        return this.innerPerturbator.pbyte(value);
    }

    @Override
    public short pshort(short value) {
        return this.innerPerturbator.pshort(value);
    }

    @Override
    public int pint(int value) {
        return this.innerPerturbator.pint(value);
    }

    @Override
    public long plong(long value) {
        return this.innerPerturbator.plong(value);
    }

    @Override
    public char pchar(char value) {
        return this.innerPerturbator.pchar(value);
    }

    @Override
    public float pfloat(float value) {
        return this.innerPerturbator.pfloat(value);
    }

    @Override
    public double pdouble(double value) {
        return this.innerPerturbator.pdouble(value);
    }

    @Override
    public String toString() {
        return this.innerPerturbator.toString();
    }
}
