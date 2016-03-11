package perturbator;

/**
 * Created by spirals on 09/03/16.
 */
public abstract class PerturbatorInterface {

    public abstract boolean pboolean(boolean value);

    public abstract byte pbyte(byte value);
    public abstract short pshort(short value);
    public abstract int pint(int value);
    public abstract long plong(long value);

    public abstract char pchar(char value);

    public abstract float pfloat(float value);
    public abstract double pdouble(double value);

}
