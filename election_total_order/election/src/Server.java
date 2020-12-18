import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.Thread.sleep;

public class Server implements ServerInterface{

    private int ord_id;
    private int ord_level;
    private int ord_owner;
    private int owner_id;
    private int potential_owner_id;
    private HashMap<Integer, String> serverList;
    private int current;

    private int number_killed; //being killed
    private int captured;
    private int count_ack;

    private int can_id;
    private int can_level;
    private boolean killed;
    private HashMap<Integer, String> can_serverList;

    private Integer[] List_ord_channel;


    private Server() {
        super();
    }

    public void setup(int id, HashMap<Integer, String> serverList) {
        this.ord_id = id;
        this.ord_level = 0;
        this.ord_owner = 0;
        this.owner_id = 0;
        this.potential_owner_id = 0;
        this.serverList = serverList;
        this.current = serverList.size();

        this.captured = 0;
        this.number_killed = 0;
        this.count_ack = 0;

        //just to make sure
        this.can_id = -1;
        this.can_level = -1;
        this.killed = true;
        this.can_serverList = (HashMap<Integer, String>) serverList.clone();

        this.List_ord_channel= new Integer[this.serverList.size()+1];

        for (int i=0;i<=this.serverList.size();i++){
            this.List_ord_channel[i]=i;
        }


    }

    public void startElection() throws InterruptedException {
        this.can_id = this.ord_id;
        this.can_level = 0;
        this.killed = false;
        this.can_start();
    }

    public int compare_tuple(int my_level, int my_id, int other_level, int other_id){
        if(my_level > other_level){
            return 1;
        }
        else if(my_level == other_level){
            if(my_id > other_id){
                return 1;
            }
            else if(my_id == other_id){
                return 0;
            } else{
                return -1;
            }
        }else{
            return -1;
        }
    }

    public void ord_receive(int level, int id, int id_from) {
        int result = compare_tuple(this.ord_level, this.owner_id, level, id);
        if ( result == -1) {
            System.out.println("Ord " + this.ord_id + " value" + "(" + this.ord_level + ", " + this.owner_id + ")"+ " were smaller than " + "(" + level + ", " + id + ")");
            this.potential_owner_id = id_from;
            this.ord_level = level;
            this.owner_id = id;
            if (this.ord_owner == 0) {
                this.ord_owner = this.potential_owner_id;
                this.captured++;
                this.count_ack++;
            }
            System.out.println("Ord " + this.ord_id + ": " + "Potential owner: " + id_from + "| "+ "Owner: " + this.ord_owner + "| ");
            try {
                ServerInterface process = (ServerInterface) Naming.lookup(this.serverList.get(this.ord_owner));
//                System.out.println("sleeping!");
                synchronized (this.List_ord_channel[this.ord_owner]){
                    sleep((long)(Math.random() * 1000));
                }
//                this.captured++;
                System.out.println("Ord " + this.ord_id + " sends a message " + "(" + level + ", " + id + ")"+" to " + this.ord_owner);
                process.can_receive(level, id, this.ord_id);
            } catch (Exception e) {
                System.err.println("Process exception: " + e.toString());
                e.printStackTrace();
            }
        }
        if (result == 0) {
            System.out.println("Ord " + this.ord_id + " value" + "(" + this.ord_level + ", " + this.owner_id + ")"+ " were equal to " + "(" + level + ", " + id + ")");
            this.ord_owner= this.potential_owner_id;
            this.captured++;
            System.out.println("Ord " + this.ord_id + ": " + "Potential owner: " + id_from + "| "+ "Owner: " + this.ord_owner + "| ");
            try {
                ServerInterface process = (ServerInterface) Naming.lookup(this.serverList.get(this.ord_owner));
//                System.out.println("sleeping!");
                synchronized (this.List_ord_channel[this.ord_owner]){
                    sleep((long)(Math.random() * 1000));
                }
                this.count_ack++;
                System.out.println("Ord " + this.ord_id + " sends a message " + "(" + level + ", " + id + ")"+" to " + this.ord_owner);
                process.can_receive(level, id, this.ord_id);
            } catch (Exception e) {
                System.err.println("Process exception: " + e.toString());
                e.printStackTrace();
            }
        }
        if (result==1){
            try {
                ServerInterface process = (ServerInterface) Naming.lookup(this.serverList.get(id));
                process.killed_itself();
                System.out.println("Ord " + this.ord_id + " ignore "+id);
            } catch (Exception e) {
                System.err.println("Process exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public void can_start() throws InterruptedException {
        System.out.println("Process " + this.can_id + " starts election.");
        while (this.can_serverList.size() > 1 && this.killed==false){
            if(!this.can_serverList.containsKey(this.current) || this.current == this.can_id){
                this.current--;
            }
//            if(!this.can_serverList.containsKey(this.current)){
//                this.current--;
//            }
            else {
                System.out.println("Size of map" + this.can_id + ": " + this.can_serverList.size());
                System.out.println("Process " + this.can_id + ": " + this.can_level + ", " + this.can_id);
                String serverAddress = this.can_serverList.get(this.current);
                try {
                    ServerInterface process = (ServerInterface) Naming.lookup(serverAddress);
//                    System.out.println("sleeping!");
                    sleep((long) (Math.random() * 1000));
                    System.out.println("Process " + this.can_id + " sends message " + "(" + this.can_level + ", " + this.can_id + ")  to ord " + this.current);
                    this.current--;
                    process.ord_receive(this.can_level, this.can_id, this.can_id);
                } catch (Exception e) {
                    System.err.println("Process exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
        if(!this.killed){
            //System.out.println("hi");
            //Thread.sleep(10000);
            //System.out.println("SIZE OF Serverlist: " + this.serverList.size());
            for (HashMap.Entry<Integer, String> entry : this.serverList.entrySet()) {
                int id = entry.getKey();
                String serverAddress = entry.getValue();
                //System.out.println("ID TO SEND TO: "+ id);
                    try {
                        ServerInterface process = (ServerInterface) Naming.lookup(serverAddress);
//                        sleep((long)(Math.random() * 1000));
                        //System.out.println("Process " + this.can_id + " sends winning message to ord " + id);
                        process.receive_winmessage();
                    } catch (Exception e) {
                        System.err.println("Process exception: " + e.toString());
                        e.printStackTrace();
                    }

            }
            System.out.println("Process " + this.can_id +  " was elected.");
//            System.exit(0);
        }

    }

    public void receive_winmessage() {
        this.killed = true;
        this.can_serverList.clear();
        System.out.println("Process " + this.ord_id +  ": "+"Level: "+ this.can_level + "| Killed: "+ this.number_killed + "| Captured: " + this.captured + " | sent ack: " + this.count_ack);
    }

    public void killed_itself(){
        this.killed=true;
        System.out.println("Process: "+this.can_id+" Killed itself!");
    }

    public void can_receive(int level, int id, int id_from){
        if(!this.killed || this.can_serverList.size() > 1) {
            if (this.can_id == id && !this.killed) {
                this.can_level++;
                if (this.can_serverList.containsKey(id_from)) {
                    this.can_serverList.remove(id_from);
                }
            } else {
                if (compare_tuple(this.can_level, this.can_id, level, id) != 1) {
                    this.killed = true;
                    this.number_killed++;
                    System.out.println("Process " + this.can_id + " IS KILLED from " + id_from);
                    try {
//                        System.out.println("sleeping!");
                        sleep((long)(Math.random() * 1000));
                        ServerInterface process = (ServerInterface) Naming.lookup(this.serverList.get(id_from));
                        System.out.println("Process " + this.can_id + " sends message to ord " + id_from);
                        process.ord_receive(level, id, this.can_id);
                    } catch (Exception e) {
                        System.err.println("Process exception: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        try {
            int number = 5081;
            for(int i = 1; i < 9; i++){
                Server obj = new Server();
                ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);
                Registry registry = LocateRegistry.createRegistry(number+i-1);
                registry.rebind("process"+i, stub);
                System.out.println("Server "+ i +" ready");
            }
            System.out.println("Servers are ready");

        } catch (Exception e){
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
