import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.time.*;

public class nbodyparallel implements Runnable {

    static body[] bodies;
    int counterBodies = 0;
    int counter = 0;
    static int numBodies;
    static int numSteps;
    static int numThreads;
    static CyclicBarrier barrier;

    static int printcounter = 0;

    public static void main(String[] args) {
        // Create a new instance of the nbodyparrallel class
        nbodyparallel nbody = new nbodyparallel();
        // Run the nbodyparrallel class

        numBodies = Integer.parseInt(args[0]);
        numThreads = Integer.parseInt(args[1]);
        numSteps = Integer.parseInt(args[2]) * numThreads;
        bodies = new body[numBodies];
        body[] bodiesOG = new body[numBodies];
        Random rand = new Random();
        for (int i = 0; i < bodies.length; i++) {
            int x = rand.nextInt(1000);
            int y = rand.nextInt(1000);
            body b = new body(i + 1, 1, x,y, 0, 0);
            body b2 = new body(i + 1, 1,x,y,0,0);
            bodies[i] = b;
            bodiesOG[i] = b2;
        }
        long t1 = System.nanoTime();
        barrier = new CyclicBarrier(numThreads);
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(nbody);
            threads[i].setName("Thread " + (i + 1));
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       /*
       for (int i = 0; i < bodies.length; i++) {
           System.out.println("Body " + bodies[i].ID +" new pos: "+ " X: " + bodies[i].getX() + " Y: " + bodies[i].getY() + " old pos " + " X: " + bodiesOG[i].getX() + " Y: " + bodiesOG[i].getY());
        }
        */
        System.out.println(
                printcounter + " steps completed in " + ((System.nanoTime() - t1)/1_000_000) + " ms");

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

    public void run() {
        // 1. Calculate force on each body
        body b;
        while (numSteps > 0) {
            while (true) {

                synchronized (this) {
                    if (counterBodies == bodies.length) {
                        break;
                    }

                    b = bodies[counterBodies];
                    counterBodies++;
                }

                for (int i = 0; i < bodies.length; i++) {
                    double force = calculateForce(b, bodies[i]);
                    double directionX = bodies[i].getX() - b.getX();
                    double directionY = bodies[i].getY() - b.getY();
                    b.setVelocityX(b.getVelocityX() + (force * directionX / bodies[i].getMass()));
                    b.setVelocityY(b.getVelocityY() + (force * directionY / bodies[i].getMass()));

                }
            }
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Update position of each body

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
                b2.setX(b2.getX() + b2.getVelocityX());
                b2.setY(b2.getY() + b2.getVelocityY());
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

}