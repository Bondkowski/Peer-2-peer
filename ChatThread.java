package Chat;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ChatThread implements Runnable {
    private Socket connectionSocket = null;
    private boolean work = true;
    private Integer id;
    private ChatServer server;
    private DataOutputStream responseToClient;

    public ChatThread(Socket socket, ChatServer server) {
        new Thread("ClientThread");
        this.connectionSocket = socket;
        this.server = server;
    }
    // In this method we check if chatRoom exist and if not, we create it

    public Socket getConnectionSocket(){
        return connectionSocket;
    }
    public DataOutputStream getResponseToClient(){
        return responseToClient;
    }
    public int getId(){
        return id;
    }
    public void setId(Integer i){
        id = i;
    }
    public String getMessage(){

        return "message";
    }

    public void run() {
        try {
            //reading input from client
            BufferedReader messageFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            responseToClient = new DataOutputStream(connectionSocket.getOutputStream());

            while(work) {

                if (messageFromClient.ready()) {
                    // reading input line by line
                    String message = messageFromClient.readLine();
                    if(!message.isEmpty()) {
                        JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);
                        server.sendMessage(jsonObject);
                        server.getmH().sendToServers(jsonObject);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Can not listen to the socket:  " + connectionSocket.getLocalPort());
            e.getMessage();
            e.printStackTrace();
        }
    }
}