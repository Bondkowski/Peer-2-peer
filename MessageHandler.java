package Chat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Babka on 07.05.2015.
 */
public class MessageHandler implements Runnable{
    private ChatServer server;
    private boolean work;
    private HashMap<String, InetAddress> serversList = new HashMap<>();
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    private DatagramSocket serverSocket;
    private DatagramSocket outputSocket;

    public MessageHandler(ChatServer server){
        this.server = server;
        try {
            serversList.put("Server2", InetAddress.getByName("52.24.52.12"));
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
        }
    }

    public void sendToServers(JSONObject jsonObject) {
        try {
            String message = jsonObject.toJSONString();
            sendData = message.getBytes();
            for (InetAddress s : serversList.values()) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, s, 12345);
                outputSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void run(){
        try{
            serverSocket = new DatagramSocket(9999);
            outputSocket = new DatagramSocket(12345);
            serversList.put("Server2", InetAddress.getByName("172.0.0.1"));

            while (work){
                System.out.println("Handler is working");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData());
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);
                server.sendMessage(jsonObject);
            }

        }catch (Exception e){
            e.getMessage();
        }
    }
}
