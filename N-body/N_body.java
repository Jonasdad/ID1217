import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class N_body implements Runnable {
    static int size = 1000;
    // command line arguments
    static int numBodies;
    static int numSteps;
    static int numThreads;
    static double theta;
    // command line arguments end
    static body[] bodies;
    static QuadTree initTree;
    static int threadCounter = 0;
    static CyclicBarrier barrier;

    public static void main(String[] args) {
        N_body n = new N_body();
        Random r = new Random();
        numBodies = Integer.parseInt(args[0]);
        numThreads = Integer.parseInt(args[1]);
        numSteps = Integer.parseInt(args[2]);
        theta = Double.parseDouble(args[3]);
        initTree = new QuadTree(size, 0, 0, size);
        initTree.Theta = theta;
        bodies = new body[numBodies];
        barrier = new CyclicBarrier(numThreads+1);

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numBodies; i++) {
            bodies[i] = new body(i + 1, r.nextInt(1000), Math.random()*size, Math.random()*size, 0, 0);
            initTree.push(bodies[i]);
        }
        long t0 = System.nanoTime();


        for (int ii = 0; ii < numSteps; ii++) { 
         
            System.out.println("Starting run " + (ii+1) + "...");
            long t1 = System.nanoTime();
            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(n);
                threads[i].setName("Thread " + (i + 1));
                threads[i].start();
            }
            try{
                barrier.await();
//                System.out.println("All threads have finished updating bodies.");
            }
            catch(Exception e){
                e.printStackTrace();
            }

            threadCounter = 0;
            initTree = null;
            initTree = new QuadTree(size, 0, 0, size);
            long t4 = System.nanoTime();

            for (int i = 0; i < numBodies; i++) {
                initTree.push(bodies[i]);
            }

           // long t2 = System.nanoTime();
           // System.out.println("Pushed bodies to tree in " + (t2 - t4) / 1000 + "ms");
           // System.out.println("Run " + (ii+1) + " took " + (t2 - t1) / 1000 + "ms");
        }
        
        System.out.println("Total runtime : " + (System.nanoTime() - t0) / 1000000 + "ms");
        for(int i = 0; i < numThreads; i++){
            try{
                threads[i].join();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    
    }

    public boolean inBoundaries(body b) {
        if (b.x <= 0 || b.y <= 0 || b.x > size || b.y > size) {
            invert(b);
            return false;
        }
        return true;
    }

    public void invert(body b) {
        b.velocityX = -b.velocityX;
        b.velocityY = -b.velocityY;
        return;
    }

    @Override
    public void run() {
        body b;
        while (true) {

            synchronized (this) {
                if (numBodies <= threadCounter) { // if all bodies have been updated
                    break;
                } else {
                    b = bodies[threadCounter];
                    threadCounter++;
                }
            }
            if (!inBoundaries(b)) {
                continue;
            }

                initTree.updateForce(b);
                b.x += b.velocityX;
                b.y += b.velocityY;
            

        }
        try{
            barrier.await();
        }
        catch(Exception e){
            e.printStackTrace();
    }
    }
}