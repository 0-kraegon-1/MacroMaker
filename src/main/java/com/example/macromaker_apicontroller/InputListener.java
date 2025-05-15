package com.example.macromaker_apicontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class InputListener {
    private final int LEFT_CLICK = 1;      // The value of left mouse click in Windows VK_codes
    private final int RIGHT_CLICK = 2;     // The value of right mouse click in Windows VK_codes
    private final int  SCROLL_CLICK = 4;   // The value of scroll wheel click in Windows VK_codes
    private volatile int mouseX = 0;
    private volatile int mouseY = 0;
    private volatile boolean mouseB1Pressed = false;
    private volatile boolean mouseB2Pressed = false;
    private volatile boolean mouseB3Pressed = false;
    private volatile int scrollDir = 0;
    private volatile boolean running = false;
    private final List<String> pressedKeysList = new ArrayList<>();
    private Process process;

    public void startInputListener() {
        running = true;
        try {
            process = Runtime.getRuntime().exec("C:/Users/tony_/source/repos/InputCapture/x64/Debug/InputCapture.exe");
            // Start a thread to read output from the C++ program
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String output;
                    while (running && (output = reader.readLine()) != null) {
                        // KEYBOARD-EVENTS
                        if (output.startsWith("Key_")) {
                            if (output.contains("Pressed:")) {
                                String keyCode = output.split(":")[1];
                                if (!pressedKeysList.contains(keyCode))
                                    pressedKeysList.add(keyCode);
                                //System.out.println("Key Pressed: " + keyCode);
                            }
                            else if (output.contains("Released:")) {
                                String keyCode = output.split(":")[1];
                                pressedKeysList.remove(keyCode);
                                //System.out.println("Key Released: " + keyCode);
                            }
                        }
                        // MOUSE-EVENTS
                        else if (output.startsWith("Mouse_")) {
                            if (output.contains("Moved:")) {
                                String[] coords = output.split(":");
                                mouseX = Integer.parseInt(coords[1]);
                                mouseY = Integer.parseInt(coords[2]);
                                //System.out.println("mouse_moved_to: [" + mouseX + ", " + mouseY + "]");
                            }
                            else if (output.contains("Pressed:")) {
                                int keyCode = Integer.parseInt(output.split(":")[1]);
                                switch (keyCode) {
                                    case LEFT_CLICK   -> mouseB1Pressed = true;
                                    case RIGHT_CLICK  -> mouseB2Pressed = true;
                                    case SCROLL_CLICK -> mouseB3Pressed = true;
                                }
                                //System.out.println("Mouse Btn " + keyCode + " Pressed");
                            }
                            else if (output.contains("Released:")) {
                                int keyCode = Integer.parseInt(output.split(":")[1]);
                                switch (keyCode) {
                                    case LEFT_CLICK   -> mouseB1Pressed = false;
                                    case RIGHT_CLICK  -> mouseB2Pressed = false;
                                    case SCROLL_CLICK -> mouseB3Pressed = false;
                                }
                                //System.out.println("Mouse Btn " + keyCode + " Released");
                            }
                            else if (output.contains("Wheel:")) {
                                int keyCode = Integer.parseInt(output.split(":")[1]);
                                if (keyCode > 0)
                                    scrollDir = 1;
                                else
                                    scrollDir = -1;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private volatile boolean escapeListenerRunning = false;
    public void startMacroEscapeListener() {
        if (running) {
            System.out.println("* InputCapture is running, MacroEscapeListener startup aborted  *");
            return;
        }
        escapeListenerRunning = true;
        try {
            process = Runtime.getRuntime().exec("C:/Users/tony_/source/repos/EscListener/EscListener/x64/Debug/EscListener.exe");
            // Start a thread to read output from the C++ program
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String output;
                    while (escapeListenerRunning && (output = reader.readLine()) != null) {
                        // Trigger Escape Event
                        if (output.equals("Escape")) {
                            Macro.QUIT = true;
                            if (process != null)
                                process.destroy();
                            escapeListenerRunning = false;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMacroEscapeListener() {
        if (escapeListenerRunning)
            escapeListenerRunning = false;
        if (process != null)
            process.destroy();
    }


    public void stopInputListener() {
        running = false;
        if (process != null)
            process.destroy();
        if (!pressedKeysList.isEmpty())
            pressedKeysList.clear();
    }

    public synchronized int getMouseX() {
        return mouseX;
    }
    public synchronized int getMouseY() {
        return mouseY;
    }

    public synchronized int isMouseB1Pressed() {
        return Boolean.compare(mouseB1Pressed, false);
    }

    public synchronized int isMouseB2Pressed() {
        return Boolean.compare(mouseB2Pressed, false);
    }

    public synchronized int isMouseB3Pressed() {
        return Boolean.compare(mouseB3Pressed, false);
    }

    public synchronized List<String> getPressedKeys() {
        return pressedKeysList;
    }

    public int mouseWheelState() {
        if (scrollDir == 0)
            return scrollDir;
        else {
            int mouseWheelState = scrollDir;
            scrollDir = 0;
            return mouseWheelState;
        }
    }
}
