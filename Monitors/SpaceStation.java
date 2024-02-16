import java.util.*;

public class SpaceStation implements Runnable {
    Random rand = new Random();
    int free = 6;
    int nitrogen = 10000;
    int quantum_fluid = 1000;
    String RESET = "\u001B[0m";
    String BLACK = "\u001B[30m";
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String BLUE = "\u001B[34m";
    String PURPLE = "\u001B[35m";
    String CYAN = "\u001B[36m";
    String WHITE = "\u001B[37m";
    public static void main(String[] args) {

        SpaceStation station = new SpaceStation();
        for (int i = 0; i < 8; i++) {
            Thread thread = new Thread(station);
            thread.setName("Shuttle " + (i + 1));
            thread.start();
        }

    }

    public void refuel_station() {
        int fuel_nitrogen = rand.nextInt(4000, 8000);
        int fuel_quantum_fluid = rand.nextInt(2000, 4000);
        synchronized (this) {
            nitrogen += fuel_nitrogen;
            quantum_fluid += fuel_quantum_fluid;
            print_status();
            notifyAll();
        }
        System.out.println(GREEN + Thread.currentThread().getName() + " refueled station: " + "+" + fuel_nitrogen
                + " nitrogen and " + "+" + fuel_quantum_fluid + " quantum fluid."+RESET);
    }

    public void refuel_supply_shuttle(){
        int nitrogen_requested = rand.nextInt(50, 200);
        int quantum_fluid_requested = rand.nextInt(10, 100);
        synchronized (this) {
            while (nitrogen < nitrogen_requested || quantum_fluid < quantum_fluid_requested) {
                try {
                    System.out.println(RED +"Not enough fuel for supply " + Thread.currentThread().getName() + " waiting for refuel." + RESET);
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread " + Thread.currentThread() + " interrupted.");
                }
            }
            nitrogen -= nitrogen_requested;
            quantum_fluid -= quantum_fluid_requested;
            print_status();
        }
        System.out.println(GREEN + "Supply " + Thread.currentThread().getName() + " refueled with " + nitrogen_requested + " nitrogen and "
                + quantum_fluid_requested + " quantum fluid."+ RESET);
    }

    public void refuel_shuttle() {
        int nitrogen_requested = rand.nextInt(50, 200);
        int quantum_fluid_requested = rand.nextInt(10, 100);
        synchronized (this) {
            while (nitrogen < nitrogen_requested || quantum_fluid < quantum_fluid_requested) {
                try {
                    System.out.println(RED +"Not enough fuel for " + Thread.currentThread().getName() + " waiting for refuel." + RESET);
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread " + Thread.currentThread() + " interrupted.");
                }
            }
            nitrogen -= nitrogen_requested;
            quantum_fluid -= quantum_fluid_requested;
            print_status();
        }
        System.out.println(GREEN + "Space " + Thread.currentThread().getName() + " refueled with " + nitrogen_requested + " nitrogen and "
                + quantum_fluid_requested + " quantum fluid."+ RESET);
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
                    System.out.println(CYAN + "Supply "+Thread.currentThread().getName() + " arrived, " + free + " free slots left." + RESET);
                    refuel_station();
                    refuel_supply_shuttle();
                    Thread.sleep(rand.nextInt(5000, 6000));
                    increment_free();
                    System.out.println(CYAN + "Supply "+Thread.currentThread().getName() + " departed, " + free + " free slots left." + RESET);
                } else if (free > 0) {
                    Thread.sleep(rand.nextInt(1500, 5000));
                    decrement_free();
                    System.out.println(YELLOW + "Space "+Thread.currentThread().getName() + " arrived, " + free + " free slots left."+ RESET);
                    refuel_shuttle();
                    increment_free();
                    System.out.println(YELLOW + "Space " +Thread.currentThread().getName() + " departed, " + free + " free slots left."+ RESET);
                    Thread.sleep(rand.nextInt(5000, 6000));
                }
            }
        }

        catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread() + " interrupted.");
        }
    }

    public synchronized void print_status(){
        System.out.println(WHITE + "Nitrogen: " + nitrogen + " Quantum Fluid: " + quantum_fluid + RESET);
    }

}