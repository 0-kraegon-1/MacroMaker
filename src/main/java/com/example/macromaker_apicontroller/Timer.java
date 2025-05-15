package com.example.macromaker_apicontroller;

public class Timer implements Runnable {
    private long startTime;
    private long pausedTime;
    private boolean running;

    public Timer() {
        this.running = false;
        this.pausedTime = 0;
    }

    // Start the stopwatch
    public void start() {
        if (!running) {
            running = true;
            startTime = System.currentTimeMillis() - pausedTime;  // Adjust for paused time
            pausedTime = 0;
            // Start the thread to update time
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    // Pause the stopwatch
    public void pause() {
        if (!running) {
            return;  // Timer is already paused
        }
        running = false;
        pausedTime = System.currentTimeMillis() - startTime;  // Capture the time elapsed before pausing
    }


    // Reset the timestamps
    public void reset() {
        pausedTime = 0;
        startTime = 0;
    }

    public void restart() {
        pausedTime = 0;
        startTime = System.currentTimeMillis();
    }

    // Get the current elapsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return pausedTime;
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        if (!running) {
            start();    // Safety-call, just in case the user calls run() instead of start()
        } else while (running) {
            // This loop is just to keep the thread alive while the stopwatch is running
            try {
                Thread.sleep(100);  // Sleep for a short while to avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
