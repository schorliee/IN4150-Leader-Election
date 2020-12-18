import java.rmi.Naming;
import java.util.HashMap;
import static java.lang.Thread.sleep;


public class Client  {
    private Client() {}



    public static class MyRunnable_1 implements Runnable {
        public int id;
        HashMap<Integer, String> map1;
        public MyRunnable_1(int id,HashMap<Integer, String> map1) {
            this.id = id;
            this.map1 = map1;
        }
        public void run() {
            int delay = (int) (Math.random() * 1000);
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

    public static class MyRunnable_2 implements Runnable {
        public int id;
        HashMap<Integer, String> map1;
        public MyRunnable_2(int id,HashMap<Integer, String> map1) {
            this.id = id;
            this.map1 = map1;
        }
        public void run() {
            int delay = (int) (Math.random() * 1000);
            System.out.println("Process " + id + " Running with delay " + delay);
            try {
                ServerInterface node = (ServerInterface) Naming.lookup(map1.get(id));
                node.setup(this.id, map1);
                sleep(delay);
//                node.startElection();
            } catch(Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        HashMap<Integer, String> map1 = new HashMap<Integer, String>();
        for(int i=1; i < 9; i++){
            int number = 5080+i;
            map1.put(i, "rmi://localhost:"+number+"/process"+i);
        }

        for(int i = 1; i < 9; i++){
            if (i<=1)
            {
//                candidate
                Thread t1 = new Thread(new MyRunnable_1(i,map1));
                t1.start();
            }
            else{
//                not candidate
                Thread t2 = new Thread(new MyRunnable_2(i,map1));
                t2.start();
            }
        }
    }
}

