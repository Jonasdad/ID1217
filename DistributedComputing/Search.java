package DistributedComputing;
import java.util.*;
import java.lang.*;
import java.rmi.*;

public interface Search extends Remote{
    boolean findName(String name) throws RemoteException;
}

    
