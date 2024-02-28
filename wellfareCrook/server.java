import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server started at port 8080");
            String[] names = {"Alice ", "Bob ", "Mallory ", "Eve ", "Trudy"};
            System.out.print("\nPotential Crooks: ");
            printArray(names);
            System.out.println("\nAwaiting clients");
            CyclicBarrier barrier = new CyclicBarrier(2);
            while (true) {
                Socket client = serverSocket.accept(); //establishes connection
                System.out.println("Client 1 connected");
                System.out.println("Waiting for client 2");
                Socket client2 = serverSocket.accept();
                System.out.println("Client 2 connected");

                Thread thread1 = new ClientHandler(client, barrier, "Client 1");
                thread1.start();
                Thread thread2 = new ClientHandler(client2, barrier, "Client 2");
                thread2.start();

                thread1.join();
                thread2.join();

                System.out.println("Work finished");
                serverSocket.close();


            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void printArray(String[] arr){
        for(int i = 0; i < arr.length; i++){
            System.out.print(arr[i]);
        }
        System.out.println();
    }
}


class ClientHandler extends Thread {
    final Socket socket;
    String[] names = {"Alice", "Bob", "Mallory", "Eve", "Trudy", "END"};
    String[] crooks = new String[names.length];
    CyclicBarrier barrier;
    String clientName;
    static int counter = 0;
    static Object lock = new Object();
    public ClientHandler(Socket socket, CyclicBarrier barrier, String name) {
        this.socket = socket;
        this.barrier = barrier;
        this.clientName = name;
    }

    public void run() {
        try (
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            for (int i = 0; i < names.length; i++) {
                // Send a name to the client
                dataOutputStream.writeUTF(names[i]);
                dataOutputStream.flush();
                // Wait for the client's response
                String response = dataInputStream.readUTF();
                if(response.equals("1")){
                    System.out.println(clientName + " found " + names[i]);
                }
                if(response.equals("1")){
                    try{
                        synchronized(lock){
                            counter++;
                           // System.out.println(clientName + " is incrementing counter: " + counter);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                barrier.await();
                if(counter == 2){
                    crooks[i] = names[i];
                    System.out.println("CROOK : " + crooks[i] + "!!");
                }
                else{
                    System.out.println(names[i] + " is not a crook");
                }
                counter = 0;
                Thread.sleep(1000);
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
