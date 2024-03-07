import java.util.*;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.lang.*;

public class N_body implements Runnable{


    static int numSteps;
    static int numBodies;
    static int numThreads;
    static double Theta;

    static body[] bodies;
    static int treesize = 10000;
    static int counterBodies = 0;
    static int counter;
    static QuadTree tree;
    static CyclicBarrier barrier;
    static int printcounter = 0;

    public static void main(String[] args) {
        Random rand = new Random();

        // Command Line Arguments
        numBodies = Integer.parseInt(args[0]);
        numThreads = Integer.parseInt(args[1]);
        numSteps = Integer.parseInt(args[2])*numThreads;
        Theta = Double.parseDouble(args[3]);
        long t1 = System.nanoTime();
        barrier = new CyclicBarrier(numThreads);
        bodies = new body[numBodies];
        tree = new QuadTree(treesize, 0, 0, treesize);
        tree.Theta = Theta;
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
        }

        System.out.println(printcounter/4 + " steps completed in " + ((System.nanoTime() - t1)/1_000_000) + " ms");
    }

    public void delete(body b){ //deletes a body from the tree
        b = null;
        return;
    }

    public boolean inBoundaries(body b){ //returns true if the body is in the boundaries of the map
        if(b.x >= 0 && b.x <= treesize && b.y >= 0 && b.y <= treesize){
            return true;
        }
        System.out.println(b.ID + " is out of boundaries");
        delete(b);
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
            for (int i = 0; i < bodies.length; i++) {
                tree.updateForce(b);
            }
        }
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            if (counter == bodies.length) {
                break;
            }
            body b2;
            synchronized (this) {

                if (counter == bodies.length) {
                    break;
                }
                b2 = bodies[counter];
                counter++;
            }
            b2.setX(b2.x + b2.velocityX);
            b2.setY(b2.y + b2.velocityY);
        }

        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        counter = 0;
        counterBodies = 0;
        synchronized (this) {
            printcounter++;
                numSteps--;

        }
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }


        }
    }

