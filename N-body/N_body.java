import java.util.*;
import java.util.Random;
import java.lang.*;

public class N_body implements Runnable{


    static int numSteps;
    static int numBodies;
    static int numThreads;
    static body[] bodies;
    static int treesize = 10000;
    static int counterBodies = 0;
    static QuadTree tree;

    public static void main(String[] args) {
        Random rand = new Random();

        // Command Line Arguments
        numBodies = Integer.parseInt(args[0]);
        numThreads = Integer.parseInt(args[1]);
        numSteps = Integer.parseInt(args[2]);

        bodies = new body[numBodies];
        tree = new QuadTree(treesize, 0, 0, treesize);
        Thread[] threads = new Thread[numThreads];
        N_body nbody = new N_body();
        //  Create n bodies
        for(int i = 0; i < numBodies; i++){
            body b = new body(i+1, rand.nextInt(10000), rand.nextInt(10000),
            rand.nextInt(100), 0,0);
            bodies[i] = b;
            tree.push(b);
        }
        while(numSteps > 0){ //run the simulation for numSteps
            QuadTree updatedTree = new QuadTree(treesize, 0, 0, treesize);//create a new tree for the updated bodies
            for(int i = 0; i < numBodies; i++){ //insert the updated bodies into the new tree
                updatedTree.push(bodies[i]);  
            }

            for(int i = 0; i < numThreads; i++){
                threads[i] = new Thread(nbody);
                threads[i].start();
            }
            
            for(int i = 0; i < numThreads; i++){
                try{
                    threads[i].join();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            numSteps--;
        }


    }

    public double getDistanceSquared(body b1, body b2) {
        return Math.pow(b1.getX() - b2.getX(), 2) + Math.pow(b1.getY() - b2.getY(), 2);
    }

    public double calculateForce(body b1, body b2) {
        double distance = (getDistanceSquared(b1, b2));
        if (distance == 0) {
            return 0;
        }
        double force = (b1.getMass() * b2.getMass()) / Math.pow(distance, 2); // Assuming G = 1
        return force;
    }

    public void delete(body b){ //deletes a body from the tree
        if(!inBoundaries(b)){
            System.out.println(b.ID + " deleted");
            b = null;
        }
    }

    public boolean inBoundaries(body b){ //returns true if the body is in the boundaries of the map
        if(b.x >= 0 && b.x <= treesize && b.y >= 0 && b.y <= treesize){
            return true;
        }
        System.out.println(b.ID + " is out of boundaries");
        return false;
    }

    public void run(){
        body b;
        while(true){
            synchronized (this) {
                if (counterBodies == bodies.length) {
                    break;
                }
                b = bodies[counterBodies];
                counterBodies++;
            }

        }
    }
}
