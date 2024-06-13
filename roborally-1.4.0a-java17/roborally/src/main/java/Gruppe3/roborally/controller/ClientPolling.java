package Gruppe3.roborally.controller;

import Gruppe3.roborally.model.httpModels.PlayerResponse;

import java.io.IOException;

public class ClientPolling implements Runnable {
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            System.out.println("Polling server for player updates...");
            try {
                Thread.sleep(2000); // Sleep for 2 seconds before the next poll
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }
    }

    public void stop() {
        running = false;
    }
}
