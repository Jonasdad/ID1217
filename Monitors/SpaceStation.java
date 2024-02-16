import java.util.*;

public class SpaceStation implements Runnable {
    Random rand = new Random();
    int free = 6;
    int nitrogen = 10000;
    int quantum_fluid = 1000;

    public static void main(String[] args) {
        SpaceStation station = new SpaceStation();
        for (int i = 0; i < 8; i++) {
            Thread thread = new Thread(station);
            thread.setName("Shuttle " + (i + 1));
            thread.start();
        }

    }

    public void refuel_station() {
        int fuel_nitrogen = rand.nextInt(2000, 8000);
        int fuel_quantum_fluid = rand.nextInt(400, 4000);
        synchronized (this) {
            nitrogen += fuel_nitrogen;
            quantum_fluid += fuel_quantum_fluid;
            print_status();
            notifyAll();
        }
        System.out.println(Thread.currentThread().getName() + " refueled station: " + "+" + fuel_nitrogen
                + " nitrogen and " + "+" + fuel_quantum_fluid + " quantum fluid.");
    }

    public void refuel_shuttle() {
        int nitrogen_requested = rand.nextInt(500, 2000);
        int quantum_fluid_requested = rand.nextInt(100, 1000);
        synchronized (this) {
            while (nitrogen < nitrogen_requested || quantum_fluid < quantum_fluid_requested) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread " + Thread.currentThread() + " interrupted.");
                }
            }
            nitrogen -= nitrogen_requested;
            quantum_fluid -= quantum_fluid_requested;
            print_status();
        }
        System.out.println(Thread.currentThread().getName() + " refueled with " + nitrogen_requested + " nitrogen and "
                + quantum_fluid_requested + " quantum fluid.");
    }

    public synchronized void increment_free() throws InterruptedException {
        if (free < 4) {
            free++;
        }
        notifyAll();
    }

    public synchronized void decrement_free() throws InterruptedException {
        while (free <= 0) {
            wait();
        }
        free--;
    }

    public void run() {
        try {
            while (true) {
                int type = rand.nextInt(100);
                if (type <= 30 && free > 0) {
                    Thread.sleep(rand.nextInt(1500, 5000));
                    decrement_free();
                    System.out.println("Supply "+Thread.currentThread().getName() + " arrived, " + free + " free slots left.");
                    refuel_station();
                    Thread.sleep(rand.nextInt(5000, 6000));
                    increment_free();
                    System.out.println("Supply "+Thread.currentThread().getName() + " departed, " + free + " free slots left.");
                } else if (free > 0) {
                    Thread.sleep(rand.nextInt(1500, 5000));
                    decrement_free();
                    System.out.println("Space "+Thread.currentThread().getName() + " arrived, " + free + " free slots left.");
                    refuel_shuttle();
                    increment_free();
                    System.out.println("Space " +Thread.currentThread().getName() + " departed, " + free + " free slots left.");
                    Thread.sleep(rand.nextInt(5000, 6000));
                }
            }
        }

        catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread() + " interrupted.");
        }
    }

    public synchronized void print_status(){
        System.out.println("Nitrogen: " + nitrogen + " Quantum Fluid: " + quantum_fluid);
    }

}