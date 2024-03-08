import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 8080);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            ) {
                while (true) {
                    // Read a name from the server
                    String name = dataInputStream.readUTF();
                    System.out.println("Server requested search of: " + name);
                    if(name.equals("END")){break;}//added this line to break the loop when the server sends "END" to the client
                    // Process the name and send a response
                    System.out.println("Processing name: " + name);
                    String response = processName(name);
                    System.out.println("Sending response: " + response);
                    dataOutputStream.writeUTF(response);
                    dataOutputStream.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private static String processName(String name) {
        String[] names = {"Jonas", "Deniel", "Roy", "Alice", "Isabelle", "Jordan"};
        // Replace this with your actual processing code
        for(int i = 0; i < names.length; i++){
            if(names[i].equals(name)){
                return "1";
            }
        }
        return "0";
    }
}