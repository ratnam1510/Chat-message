import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.*;
import java.util.Base64;

public class ChatServer{
    private static final int PORT=8080;//define the port number on which
    private static List<ClientHandler>clients=new ArrayList<>();//we create a list of clients to store instances of ClientHandler which will represent a connected client
    private void broadcastImage(String imagePath){
        try{
            File imageFile=new File(imagePath);
            FileInputStream fis=new FileInputStream(imageFile);
            byte[]imageData=new byte[(int)imageFile.length()];
            fis.read(imageData);
            fis.close();

            String base64Image= Base64.getEncoder().encodeToString(imageData);

            for(ClientHandler client:clients){
                client.sendMessage(base64Image);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        try(ServerSocket serverSocket=new ServerSocket(PORT)){
            System.out.println("Server is running on port "+PORT);//this will print a message indicating that the server is running on whcih particular port

            while(true){//this will run while the server is continuously accepting incoming client connections
                Socket clientsocket=serverSocket.accept();//accept a new client connection and create a socket for communication with the client
                System.out.println("New client connected: "+clientsocket);

                ClientHandler clientHandler=new ClientHandler(clientsocket,clients);//create a new client handler instance for the connected client
                clients.add(clientHandler);//add the new client handler to the list of clients

                Thread clientThread=new Thread(clientHandler);//start a new thread to handle communication with the client
                clientThread.start();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }//Handle IOException for example print the stack trace
    }
}
