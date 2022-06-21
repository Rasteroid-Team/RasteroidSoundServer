/*
 * This project is given as is with license GNU/GPL-3.0. For more info look
 * on github
 */
package communications;

import static communications.CommunicationController.MAXATTEMPTS;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Client-side class that has the connect logic and a thread that checks for
 * "dead but not null" sockets to reinitialize them to solve errors in the
 * connections
 * @author Jaume Fullana, Joan Gil
 */
class ClientConnector implements Runnable{
    
    private CommunicationController controller;
    private HashMap<InetAddress,Integer> hash = new HashMap();
    
    ClientConnector(CommunicationController controller) {
        this.controller = controller;
    }
    
    /**
     * Starts a connection with a given ip and returns it's socket
     * @param ip Ip of the pc to connect to
     * @return Socket of the connection or null if there's an error
     */
    Socket connect(String ip){
        try {
            Socket socket=new Socket(ip,this.controller.PORT);
            System.out.println("Connected");
            return socket;
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    Socket connect(InetAddress ip, int port){
        try {
            Socket socket=new Socket(ip,port);
            System.out.println("Connected");
            return socket;
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
            ArrayList <Connection> listPcConnections = null;
            synchronized(controller.pcConnections){
                listPcConnections = controller.pcConnections;
            }
            for (int i=0;i<listPcConnections.size();i++){
                if (listPcConnections.get(i) != null && !listPcConnections.get(i).isStatusOk()){
                    this.tryToReconnect(listPcConnections.get(i));
                }
            }
        }
    }
    
    /**
     * Method that attempts to recover a connection by trying to connect to it.
     * Only called if the connection was closed by the ServerHealth class
     * @param conn Connection class to be reestablished
     */
    private void tryToReconnect(Connection conn){
        if(!hash.containsKey(conn.getIp())){
            hash.put(conn.getIp(), 0);
        }else{
            hash.replace(conn.getIp(), hash.get(conn.getIp())+1);
        }
        if(hash.get(conn.getIp()) < MAXATTEMPTS){
            try {
                Socket socket=new Socket(conn.getIp(),controller.PORT);
                conn.setSocket(socket);
                conn.setStatusOk(true);
                conn.setServerHealth(new ServerHealth(this.controller,conn));
                hash.remove(conn.getIp());
                System.out.println("Reconnected");
            } catch (IOException ex) {
                System.out.println("Error reconnecting: " + ex.getMessage());
            }
        }else{
            conn.processClousure(false);
        }
    }
    
}
