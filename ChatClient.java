import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ChatClient {
    //Server IP and port to connect to
    private static final String SERVER_IP = "192.168.137.184";
    private static final int SERVER_PORT = 8080;
    //these are the swing components to make the GUI
    private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter writer;
    //this is a contructor to initialize a chat client
    public ChatClient() {
        //here we set up the main frame
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        //create and set up the message input field at the bottom
        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);
        //create and set up the chat area in the center with scrolling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        //create a send button on the right
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        frame.add(sendButton, BorderLayout.EAST);

        //here we add an action listener to the text field for the enter key
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        //here we make the frame visible
        frame.setVisible(true);

        //here we get the input from the using a dialog box
        String userName = JOptionPane.showInputDialog(frame, "Enter your username:");
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username cannot be empty. Exiting.");
            System.exit(0);
        }

        try {
            //we connect the server using the specified IP and port
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            //set input and output streams for communication with the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            //send the username to the server
            writer.println(userName);
            //here we start a thread to continuously receive messages from the server and update the chat area
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        chatArea.append(serverMessage + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            receiveThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this is a method to send a message to the server
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            //send the message to the server and clear the message field
            writer.println(message);
            messageField.setText("");
        }
    }
    private void sendImage(String imagePath){
        try {
            File imageFile = new File(imagePath);
            FileInputStream fis=new FileInputStream(imageFile);
            byte[]imageData = new byte[(int)imageFile.length()];
            fis.read(imageData);
            fis.close();

            String base64Image= Base64.getEncoder().encodeToString(imageData);

            writer.println(base64Image);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //this is the main method to launch the chat client
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient());
    }


}
