package Chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.simple.JSONObject;



public class ChatServer {
    //Thread pool up to ten working threads in one moment
    private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private static ServerSocket mainSocket;
    private static Integer clientIdCounter = 0;
    private static String serverID;
    private static ArrayList<ChatThread> clientList = new ArrayList<>();
    private static MessageHandler mH;

    public ChatServer (ServerSocket socket, String sID){
        mainSocket = socket;
        serverID = sID;
    }
    public MessageHandler getmH(){
        return mH;
    }
    public void sendMessage(JSONObject jsonObject) {
        System.out.println(jsonObject.get("message"));
        for (ChatThread c : clientList) {
            try {
                c.getResponseToClient().writeBytes(jsonObject.toJSONString() + "\n");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void addChatClient(ChatThread c){
        executorService.submit(c);
        c.setId(++clientIdCounter);
        clientList.add(c);
        System.out.println("The number of clients in the list has changed to: "+clientList.size());
    }
    public static void removeChatClient(ChatThread c){
        clientList.remove(c);
    }

    public static void main(String[] args) throws IOException {
        try{
            //checking if we have an argument for port number and serverID
            if(args.length != 2) {
                System.err.println("Incorrect arguments! Type TCP port number(Int) and ServerID(String) as arguments");
                System.exit(1);
            }
            int portNumber = Integer.parseInt(args[0]);
            serverID = args[1];
            mainSocket = new ServerSocket(portNumber);
            ChatServer server = new ChatServer(mainSocket,serverID);
            mH = new MessageHandler(server);
            mH.run();

            while (true) {
                int queueSize = executorService.getQueue().size();
                Socket listeningSocket = mainSocket.accept();
                DataOutputStream responseToClient = new DataOutputStream(listeningSocket.getOutputStream());
                //limiting the queue of clients waiting for the thread by 10.
                //In total we will have 20: 10 working threads and 10 clients in the queue.
                //All other client will be discarded with message
                if(queueSize > 10) {
                    responseToClient.writeBytes("Server is overloaded... Sorry!\n");
                }else {
                    ChatThread c = new ChatThread(listeningSocket,server);
                    addChatClient(c);
                }
            }
        }catch(Exception e){
            System.err.println(e);
            e.printStackTrace();
        }

    }
}