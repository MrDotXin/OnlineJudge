public class Main {
    
    public static void main(String[] args) {

        try {
            System.out.println("satrt");
            Thread.sleep(600L);
            System.out.println("end");
        } catch(InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("hello");
    }
}
