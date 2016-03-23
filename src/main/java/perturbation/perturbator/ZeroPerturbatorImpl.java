package perturbation.perturbator;

import perturbation.PerturbatorInterface;

/**
 * Created by spirals on 22/03/16.
 */
public class ZeroPerturbatorImpl implements PerturbatorInterface {
    @Override
    public boolean pboolean(boolean value) {
        return false;
    }

    @Override
    public byte pbyte(byte value) {
        return 0;
    }

    @Override
    public short pshort(short value) {
        return 0;
    }

    @Override
    public int pint(int value) {
        return 0;
    }

    @Override
    public long plong(long value) {
        return 0;
    }

    @Override
    public char pchar(char value) {
        return 0;
    }

    @Override
    public float pfloat(float value) {
        return 0;
    }

    @Override
    public double pdouble(double value) {
        return 0;
    }

    @Override
    public String toString() {
        return "zero perturbator";
    }
}
