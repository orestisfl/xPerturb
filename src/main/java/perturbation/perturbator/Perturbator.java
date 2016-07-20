package perturbation.perturbator;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by spirals on 25/03/16.
 */
public interface Perturbator extends Serializable {

    boolean pboolean(boolean value);

    byte pbyte(byte value);
    short pshort(short value);
    int pint(int value);
    long plong(long value);

    char pchar(char value);

    float pfloat(float value);
    double pdouble(double value);

    BigInteger pBigInteger(BigInteger value);

    @Override
    String toString();
}
