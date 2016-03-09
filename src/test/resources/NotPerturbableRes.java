public class NotPerturbableRes {

    private static final int field = 0;

    public boolean switchM() {
        int value = 2;
        switch (value) {
            case field:
                return false;
            case 1:
                return false;
            default:
                return true;
        }
    }

    public int whileTrue() {
        while (true) {
            if (true)
                return 1;
        }
    }

    public int DECunary(int i) {
        return --i;
    }

    public int unaryDEC(int i) {
        return i--;
    }

    public int unaryINC(int i) {
        return i++;
    }

    public int INCunary(int i) {
        return ++i;
    }


}