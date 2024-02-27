package DistributedComputing;
import java.rmi.*;
public class Server {
    public static void main(String[] args){
    try{
        String[] IBMYorktown = {"John", "Doe", "Jane", 
                                "Smith", "James", "Brown", 
                                "Robert", "Johnson", "Michael"};
        String[] ColumbiaStudents = {"John", "Doe", "Jane", 
                                     "Smith", "James", "Brown", 
                                     "Robert", "Johnson", "Michael"};
        String[] WelfareNYC = {"John", "Doe", "Jane", 
                               "Smith", "James", "Brown", 
                               "Robert", "Johnson", "Michael"};
        Search search = new SearchImpl(IBMYorktown, ColumbiaStudents, WelfareNYC);
        Registry registry = LocateRegistry.getRegistry();
        registry.bind("search", search);
        System.out.println("Server Ready");
    }
    catch(Exception e){
        e.printStackTrace();
    }
    }
}
