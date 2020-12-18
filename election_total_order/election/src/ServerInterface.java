import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

public interface ServerInterface extends Remote {
    public void setup(int id, HashMap<Integer, String> serverList) throws RemoteException;
    public void startElection() throws RemoteException, InterruptedException;
    public int compare_tuple(int my_level, int my_id, int other_level, int other_id) throws RemoteException;
    public void ord_receive(int level, int id, int link) throws RemoteException;
    public void can_receive(int level, int id, int id_from) throws RemoteException;
    public void can_start() throws RemoteException, InterruptedException;
    public void receive_winmessage() throws RemoteException;
    public void killed_itself() throws RemoteException;
}