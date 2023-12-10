import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;//the cleints socket for communication
    private List<ClientHandler> clients;//a list to store all connected clients handlers
    private PrintWriter writer;//printwriter to send messages to clients
    private String userName;//the username of the client

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }//constructor to initialize the client handler with clients socket and the list of clients
//the run method that executes when the client thread is started
    @Override
    public void run() {
        try {
            //we set up input and output streams for communication with the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            //read the username sent by the client
            userName = reader.readLine();
            broadcast(userName + " has joined the chat.");//broadcast a message to all clients that the current user has joined the chat

            String clientMessage;//this will continuously read the messages from the client and broadcast them to all clients
            while ((clientMessage = reader.readLine()) != null) {
                if(clientMessage.startsWith("IMAGE:")){
                    String base64Image=clientMessage.substring(6);
                    byte[]imageData = java.util.Base64.getDecoder().decode(base64Image);
                    System.out.println("Received image. size: "+imageData.length+" bytes");


                }
                else{
                    broadcast(userName+": "+clientMessage);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();//Handle IOException for example it will print the stack trace
        } finally {
            try {
                clientSocket.close();//close the client socket and remove the current client handler from the list
                clients.remove(this);
                broadcast(userName + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendMessage(String message) {
        writer.println(message);
    }//this is a method to send a message to a client
    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }//this is a method to broadcast a message to all clients
    }
    public void sendImage(String base64Image){
        writer.println(base64Image);
    }
}