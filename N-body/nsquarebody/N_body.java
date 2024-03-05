import java.util.*;
import java.util.Random;

public class N_body {
    public static void main(String[] args) {
        System.out.println("Hello, World!");


        System.out.println("Enter Number of Bodies");
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        System.out.println("Number of Bodies: " + n);
        s.close();
        Random rand = new Random();
        //  Create n bodies
        for(int i = 0; i < n; i++){
            
            body b = new body(i+1, rand.nextInt(100), rand.nextInt(100),
            rand.nextInt(100), 0,0);
        }
    }


   
}
