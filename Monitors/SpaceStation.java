import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpaceStation implements Runnable {
    Random rand = new Random();
    int free = 6;
    int nitrogen = 10000;
    int quantum_fluid = 5000;
    String RESET = "\u001B[0m";
    String BLACK = "\u001B[30m";
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String BLUE = "\u001B[34m";
    String PURPLE = "\u001B[35m";
    String CYAN = "\u001B[36m";
    String WHITE = "\u001B[37m";

    int total_station_refuel = 0;
    int total_shuttle_refuels = 0;
    int total_visits = 0;

    Map<String, Integer> arrivalCounts = new HashMap<String, Integer>();

    public static void main(String[] args) {

        SpaceStation station = new SpaceStation();
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(station);
            threads[i].setName("Shuttle " + (i + 1));
            threads[i].start();
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }, 30, TimeUnit.SECONDS);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nStatistics:");
        System.out.println("Total amount station refueled: " + station.total_station_refuel);
        System.out.println("Total amount shuttle refueled: " + station.total_shuttle_refuels);
        for (int i = 0; i < 8; i++) {
            System.out.println(
                    "Shuttle " + (i + 1) + " visited " + station.arrivalCounts.get("Shuttle " + (i + 1)) + " times.");
        }
        System.exit(1);
    }

    public void refuel_station() {
        int fuel_nitrogen = rand.nextInt(2000, 4000);
        int fuel_quantum_fluid = rand.nextInt(1000, 2000);
        synchronized (this) {
            nitrogen += fuel_nitrogen;
            quantum_fluid += fuel_quantum_fluid;
            total_station_refuel += fuel_nitrogen + fuel_quantum_fluid;
            print_status();
            notifyAll();
        }
        System.out.println(
                BLUE + "Supply " + Thread.currentThread().getName() + " refueled station: " + "+" + fuel_nitrogen
                        + " nitrogen and " + "+" + fuel_quantum_fluid + " quantum fluid." + RESET);
    }

    public void refuel_supply_shuttle() {
        int nitrogen_requested = rand.nextInt(500, 1500);
        int quantum_fluid_requested = rand.nextInt(100, 800);
        synchronized (this) {
            while (nitrogen < nitrogen_requested || quantum_fluid < quantum_fluid_requested) {
                try {
                    System.out.println(RED + "Not enough fuel for supply " + Thread.currentThread().getName()
                            + " waiting for refuel." + RESET);
                    wait();
                } catch (InterruptedException e) {
                }
            }
            nitrogen -= nitrogen_requested;
            quantum_fluid -= quantum_fluid_requested;
            total_shuttle_refuels += nitrogen_requested + quantum_fluid_requested;
            print_status();
        }
        System.out.println(PURPLE + "Supply " + Thread.currentThread().getName() + " refueled with "
                + nitrogen_requested + " nitrogen and "
                + quantum_fluid_requested + " quantum fluid." + RESET);
    }

    public void refuel_shuttle() {
        int nitrogen_requested = rand.nextInt(500, 1500);
        int quantum_fluid_requested = rand.nextInt(100, 800);
        synchronized (this) {
            while (nitrogen < nitrogen_requested || quantum_fluid < quantum_fluid_requested) {
                try {
                    System.out.println(RED + "Not enough fuel for " + Thread.currentThread().getName()
                            + " waiting for refuel." + RESET);
                    wait();
                } catch (InterruptedException e) {
                }
            }
            nitrogen -= nitrogen_requested;
            quantum_fluid -= quantum_fluid_requested;
            total_shuttle_refuels += nitrogen_requested + quantum_fluid_requested;
            print_status();
        }
        System.out.println(GREEN + "Space " + Thread.currentThread().getName() + " refueled with " + nitrogen_requested
                + " nitrogen and "
                + quantum_fluid_requested + " quantum fluid." + RESET);
    }

    public synchronized void increment_free() throws InterruptedException {
        if (free <= 6) {
            free++;
        }
        notifyAll();
    }

    public synchronized void decrement_free() throws InterruptedException {
        if (free <= 0) {
            System.out.println(
                    RED + "No free slots for supply " + Thread.currentThread().getName() + " waiting." + RESET);
        }
        while (free <= 0) {
            wait();
        }
        free--;
        total_visits++;
    }

    public void run() {
        try {
            while (true) {
                int type = rand.nextInt(100);
                if (type <= 30) {
                    Thread.sleep(rand.nextInt(1500, 5000));
                    arrivalCounts.put(Thread.currentThread().getName(),
                            arrivalCounts.getOrDefault(Thread.currentThread().getName(), 0) + 1);
                    decrement_free();
                    System.out.println(CYAN + "Supply " + Thread.currentThread().getName() + " arrived, " + free
                            + " free slots left." + RESET);
                    refuel_station();
                    refuel_supply_shuttle();
                    Thread.sleep(rand.nextInt(5000, 6000));
                    increment_free();
                    System.out.println(CYAN + "Supply " + Thread.currentThread().getName() + " departed, " + free
                            + " free slots left." + RESET);
                } else {
                    Thread.sleep(rand.nextInt(1500, 5000));
                    arrivalCounts.put(Thread.currentThread().getName(),
                            arrivalCounts.getOrDefault(Thread.currentThread().getName(), 0) + 1);
                    decrement_free();
                    System.out.println(YELLOW + "Space " + Thread.currentThread().getName() + " arrived, " + free
                            + " free slots left." + RESET);
                    refuel_shuttle();
                    increment_free();
                    System.out.println(GREEN + "Space " + Thread.currentThread().getName() + " departed, " + free
                            + " free slots left." + RESET);
                    Thread.sleep(rand.nextInt(5000, 6000));
                }
            }
        }

        catch (InterruptedException e) {
        }
    }

    public synchronized void print_status() {
        System.out.println(WHITE + "Nitrogen: " + nitrogen + " Quantum Fluid: " + quantum_fluid + RESET);
    }

}