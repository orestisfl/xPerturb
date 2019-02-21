import java.math.BigInteger;

public class BigIntegerRes {

    private BigInteger bint = BigInteger.ONE;

    public BigInteger method(BigInteger value) {
        BigInteger a = BigInteger.ZERO;
        a = a.add(value);
        a = a.add(BigInteger.valueOf(16));
        return a.multiply(BigInteger.TEN);
    }


}