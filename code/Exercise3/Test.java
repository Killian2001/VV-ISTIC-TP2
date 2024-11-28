public class Test {

    public static void main(String[] args) {
        if (true)
            if (true)
                /* Too much imbricated if statements from here! */
                if (true)
                    System.out.println("Hello World");
    }

    public void foo() {
        boolean cond = true;
        boolean otherCond = false;

        boolean a = true;
        boolean b = true;

        if (cond) {
            while(true) {
                if (!otherCond) {
                    for (int i = 0; i < 100; i++) {
                        /* Too much imbricated if statements from here! */
                        if (a || b) {
                            if (a && b) {
                                System.out.println("ok");
                            }
                        }
                    }
                }
            }
        }
    }

}