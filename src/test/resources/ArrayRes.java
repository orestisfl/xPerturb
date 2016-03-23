public class ArrayRes {

    static byte[] a = new byte[] {true ? 1 : 2};
    static byte[] b = new byte[] {1};
    static Object[]o = new Object[6];
    static int[] array = new int[7];

    static {
        array[1] = (Integer) o[0];
    }
}