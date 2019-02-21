public abstract class AbstractRes {


    public int value() {
        return 0;
    }

    class notStaticInnerClass {

        public int value() {
            return 0;
        }

    }

    static class staticInnerClass {

        public int value() {
            return 0;
        }

    }

}