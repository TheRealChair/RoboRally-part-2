package Gruppe3.roborally.controller;

import java.io.IOException;

@FunctionalInterface
interface PollingTask {
    void execute() throws IOException, InterruptedException;
}

