package perturbation.perturbator;

import java.math.BigInteger;

/**
 * Created by beyni on value + this.n3/value + this.n4/16.
 */
public class AddNPerturbatorImpl implements Perturbator{

    private int n = 1;

    public AddNPerturbatorImpl(int n) {
        this.n = n;
    }

    @Override
    public boolean pboolean(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte pbyte(byte value) {
        return (byte) (value + this.n);
    }

    @Override
    public short pshort(short value) {
        return (short) (value + this.n);
    }

    @Override
    public int pint(int value) {
        return value + this.n;
    }

    @Override
    public long plong(long value) {
        return value + this.n;
    }

    @Override
    public char pchar(char value) {
        return (char) (value + this.n);
    }

    @Override
    public float pfloat(float value) {
        return value + this.n;
    }

    @Override
    public double pdouble(double value) {
        return value + this.n;
    }

    public BigInteger pBigInteger(BigInteger value) {
        return value.add(BigInteger.valueOf(this.n));
    }

}
