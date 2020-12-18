import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;
import static java.util.Map.entry;


public class Client2  {
    private Client2() {}

    public static HashMap<Integer, String> map1 = new HashMap<Integer, String>() {{
        put( 1, "rmi://localhost:5081/process1");
        put( 2, "rmi://localhost:5082/process2");
        put (3, "rmi://localhost:5083/process3");
        put (4, "rmi://localhost:5084/process4");
        put( 5, "rmi://localhost:5085/process5");
        put( 6, "rmi://localhost:5086/process6");
        put (7, "rmi://localhost:5087/process7");
        put (8, "rmi://localhost:5088/process8");
    }};

    public static class MyRunnable_4 implements Runnable {
        public int id;
        public MyRunnable_4(int id) {
            this.id = id;
        }
        public void run() {
            int delay = (int) (Math.random() * 10000);
            System.out.println("Process " + id + " Running with delay " + delay);
            try {
                ServerInterface node = (ServerInterface) Naming.lookup(map1.get(id));
                node.setup(this.id, map1);
                sleep(delay);
                node.startElection();
            } catch(Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread t4 = new Thread(new MyRunnable_4(4));
        t4.start();
    }
}





/*long start = System.currentTimeMillis();
Thread.sleep(3000);
long time = (System.currentTimeMillis() - start)/1000;
System.out.print("Process " + this.id + " - Time: " + time+ "\n");*/