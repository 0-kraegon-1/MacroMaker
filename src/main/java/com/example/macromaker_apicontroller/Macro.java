package com.example.macromaker_apicontroller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.InputEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.*;

public class Macro {
    private final static int VK_TILDE = 16777342;
    private final static int VK_BACKTICK = 192;
    private final static int VK_PERCENT = 37;
    private final static int VK_SINGLE_QUOTE = 222;
    private final static int VK_DOUBLE_QUOTE = 152;
    private final static int VK_QUESTION_MARK = 63;
    private final static int VK_OPEN_CURLY_BRACE = 161;
    private final static int VK_CLOSE_CURLY_BRACE = 162;
    private final static int VK_LESS_THAN = 153;
    private final static int VK_GREATER_THAN = 160;
    private final static int VK_VERTICAL_PIPE = 16777340;
    private int initialWaitTime_ms = 5000;
    private int keystrokeDelay = 25;
    private int loopCount = 1;
    private final Robot robot;
    private final double screenWidth;
    private final double screenHeight;
    private int animIndex;
    private final int LEFT_CLICK = 1;
    private final int RIGHT_CLICK = 2;
    private final int  SCROLL_CLICK = 3;
    private volatile boolean LEFT_CLICK_ACTIVE = false;
    private volatile boolean RIGHT_CLICK_ACTIVE = false;
    private volatile boolean SCROLL_CLICK_ACTIVE = false;
    private int loopIndex = 0;
    protected static boolean QUIT = false;
    protected static boolean BOT_MODE = false;
    private final SecureRandom RNG = new SecureRandom();

    public Macro() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            System.out.println("***FAILED TO CREATE JAVA ROBOTS***");
            throw new RuntimeException(e);
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        screenWidth = screenSize.getWidth();
        screenHeight = screenSize.getHeight();
    }


    //          ***** COMMAND-STRING-FORMAT *****
    //         <x> <y> <mb1> <mb2> <mb3> <mw> <k>
    //             10 20 1 0 0 0 [W, A, Space]
    private Timeline RT_KeyboardAndMouseMacroAnim;
    private Timeline RTM_WaitingAnim;
    private volatile double currentFrame = 0;
    private final List<String> allActiveKeys = new ArrayList<>();
    private final Timer stopwatch = new Timer();
    private boolean loopIndefinitely = false;
    private String mostRecentKeystroke = null;
    private long timestamp = 0;
    InputListener inputListener = new InputListener();

    protected synchronized void executeRealTimeMouseAndKeyMacro(ActionEvent actionEvent, List<RTMCommand> macroList) {
        // Create macro thread and apply user-settings
        RT_KeyboardAndMouseMacroAnim = new Timeline( new KeyFrame(Duration.millis(0), event -> {  }));
        if (loopIndefinitely)
            RT_KeyboardAndMouseMacroAnim.setCycleCount(Animation.INDEFINITE);
        else
            RT_KeyboardAndMouseMacroAnim.setCycleCount(loopCount);
        RTM_WaitingAnim = new Timeline( new KeyFrame(Duration.millis(0), event -> {
            ((Label)((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorText")).setText(" WAITING... ");
        }));
        RTM_WaitingAnim.getKeyFrames().add(new KeyFrame(Duration.millis(initialWaitTime_ms), event -> {
            ((Label)((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorText")).setText(" PLAYING ");
            RT_KeyboardAndMouseMacroAnim.play();
            System.out.println("-macro execution has begun");
        }));
        RTM_WaitingAnim.setCycleCount(1);
        final int macroSize = macroList.size();
        final double frameRate = AppData.GetMacroFrameRate();
        currentFrame = frameRate;
        animIndex = 0;
        loopIndex = 0;

        //  Build macro animation for each frame
        for (int i = 0; i < macroSize; i++) {
            RT_KeyboardAndMouseMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(currentFrame+=frameRate), event -> {
                //  Retrieve the Real-Time Mouse and Keyboard Macro command
                RTMCommand command = macroList.get(animIndex++);
                //  Move mouse position <x> <y>
                robot.mouseMove(command.x(), command.y());

                //  Check for mouse left-click and unclick  <mb1>
                if (command.mb1() == 1 && !LEFT_CLICK_ACTIVE) {
                    pressMouseButton(LEFT_CLICK);
                } else if (command.mb1() == 0 && LEFT_CLICK_ACTIVE) {
                    releaseMouseButton(LEFT_CLICK);
                }

                //  Check for mouse right-click and unclick  <mb2>
                if (command.mb2() == 1 && !RIGHT_CLICK_ACTIVE)
                    pressMouseButton(RIGHT_CLICK);
                else if (command.mb2() == 0 && RIGHT_CLICK_ACTIVE)
                    releaseMouseButton(RIGHT_CLICK);

                //  Check for mouse scroll-click and unclick  <mb3>
                if (command.mb3() == 1 && !SCROLL_CLICK_ACTIVE)
                    pressMouseButton(SCROLL_CLICK);
                else if (command.mb3() == 0 && SCROLL_CLICK_ACTIVE)
                    releaseMouseButton(SCROLL_CLICK);

                //  Check for mouse wheel movement  <mw>
                final int scrollDir = command.mw();
                if (scrollDir > 0) {
                    robot.mouseWheel(scrollDir);
                } else if (scrollDir < 0) {
                    robot.mouseWheel(scrollDir);
                }

                //  Check for key-presses  <k>
                for (String keyCode : command.keys()) {
                    System.out.println("keyCode = " + keyCode);
                    if (!allActiveKeys.contains(keyCode)) {
                        allActiveKeys.add(keyCode);
                        pressKey(keyCode);
                        mostRecentKeystroke = keyCode;
                        if (stopwatch.isRunning())
                            stopwatch.restart();
                        else
                            stopwatch.start();
                    } else if (stopwatch.getElapsedTime() >= 300 && mostRecentKeystroke != null) {
                        if (stopwatch.getElapsedTime() >= timestamp) {
                            pressKey(mostRecentKeystroke);
                            timestamp = stopwatch.getElapsedTime() + 25;
                        }
                    }
                }
                // Check for key-releases
                // NOTE: Cannot update allActiveKeys while iterating through it. Must remove depressed keys after
                final List<String> keysToBeRemoved = new ArrayList<>();
                for (String keyCode : allActiveKeys) {
                    if (!command.keys().contains(keyCode)) {
                        keysToBeRemoved.add(keyCode);
                        releaseKey(keyCode);
                        stopwatch.restart();
                    }
                }
                //  Update list of allActiveKeys
                if (!keysToBeRemoved.isEmpty()) {
                    for (String keyCode : keysToBeRemoved) {
                        allActiveKeys.remove(keyCode);
                    }
                    if (!allActiveKeys.isEmpty()) {
                        final int endIndex = command.keys().size() - 1;
                        mostRecentKeystroke = command.keys().get(endIndex);
                    }
                    timestamp = 0;
                }
                //  Check if the user has requested to QUIT execution of the macro
                if (QUIT) {
                    currentFrame = 0;
                    animIndex = 0;
                    loopIndex = 0;
                    QUIT = false;
                    releaseAllButtons();
                    ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(false);
                    mostRecentKeystroke = null;
                    System.out.println("KILLED RTM EXECUTION");
                    RT_KeyboardAndMouseMacroAnim.stop();
                }
            } ));
        }
        RT_KeyboardAndMouseMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(currentFrame+=frameRate), event -> {
            currentFrame = 0;
            animIndex = 0;
            if (!loopIndefinitely) {
                if (loopCount != loopIndex) {
                    System.out.println("-loop " + ++loopIndex + "/" + loopCount + " has finished");   // notification-print
                    if (loopCount == loopIndex) {
                        loopIndex = 0;
                        ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(false);
                        inputListener.stopMacroEscapeListener();
                        System.out.println("-macro execution has concluded");   // notification-print
                    }
                }
            }
            else {
                System.out.println("-loop " + ++loopIndex + "/∞ has finished");   // notification-print
            }
        } ));
        System.out.println("finished adding frames to animation\nwaitingAnimPlaying...");      // notification-print
        inputListener.startMacroEscapeListener();
        RTM_WaitingAnim.play();   // START-MACRO-ANIMATION
    }

    private void pauseTimeline(Timeline timeline) {
        timeline.pause();
        try {
            Thread.sleep(RNG.nextLong(250, 750));
        } catch (InterruptedException e) {
            System.out.println("*** THREAD-FAILED-TO-SLEEP ***");    // notification-print
        }
        timeline.play();
    }

    private synchronized void releaseAllButtons() {
        while (!allActiveKeys.isEmpty())
            releaseKey(allActiveKeys.remove(0));
        if (LEFT_CLICK_ACTIVE) {
            releaseMouseButton(LEFT_CLICK);
            LEFT_CLICK_ACTIVE = false;
        }
        if (RIGHT_CLICK_ACTIVE) {
            releaseMouseButton(RIGHT_CLICK);
            RIGHT_CLICK_ACTIVE = false;
        }
        if (SCROLL_CLICK_ACTIVE) {
            releaseMouseButton(SCROLL_CLICK);
            SCROLL_CLICK_ACTIVE = false;
        }
    }

    private synchronized void pressKey(String keyCode) {
        try {
            robot.keyPress(KeycodeUtil.getVKCodeFromKeyCode(keyCode));
            //System.out.println("robot pressed-key:  " + keyCode);    // debug-print
        } catch (Exception e) {
            System.out.println("***ROBOT-FAILED-TO-PRESS-KEY***  ->  String:["+keyCode+"]");
        }
    }

    private synchronized void releaseKey(String keyCode) {
        try {
            robot.keyRelease(KeycodeUtil.getVKCodeFromKeyCode(keyCode));
            //System.out.println("robot released-key: " + keyCode);    // debug-print
        } catch (Exception e) {
            System.out.println("***ROBOT-FAILED-TO-RELEASE-KEY***  ->  String:["+keyCode+"]");
        }
    }


    private synchronized void pressMouseButton(int button) {
        switch (button) {
            case LEFT_CLICK -> {
                LEFT_CLICK_ACTIVE = true;
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                //System.out.println("left-click pressed by java robot");          // debug-print
            }
            case RIGHT_CLICK -> {
                RIGHT_CLICK_ACTIVE = true;
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                //System.out.println("right-click pressed by java robot");         // debug-print
            }
            case SCROLL_CLICK -> {
                SCROLL_CLICK_ACTIVE = true;
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                //System.out.println("scroll-click pressed by java robot");        // debug-print
            }
        }
    }
    private synchronized void releaseMouseButton(int button) {
        switch (button) {
            case LEFT_CLICK -> {
                LEFT_CLICK_ACTIVE = false;
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                //System.out.println("left-click released by java robot\n");       // debug-print
            }
            case RIGHT_CLICK ->  {
                RIGHT_CLICK_ACTIVE = false;
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                //System.out.println("right-click released by java robot\n");      // debug-print
            }
            case SCROLL_CLICK ->  {
                SCROLL_CLICK_ACTIVE = false;
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                //System.out.println("scroll-click released by java robot\n");     // debug-print
            }
        }
    }


    private Timeline keyboardMacroAnim;
    private Timeline KB_WaitingAnim;
    public synchronized void executeKeyMacro(ActionEvent actionEvent, String macroString) {
        final int macroSize = macroString.length();
        int[] keycode = new int[macroSize];
        keyboardMacroAnim = new Timeline( new KeyFrame(Duration.millis(0)) );
        // Create the Waiting animation
        KB_WaitingAnim = new Timeline( new KeyFrame(Duration.millis(initialWaitTime_ms), e -> {
            if (QUIT) {
                QUIT = false;
                ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(false);
                KB_WaitingAnim.stop();
            } else {
                ((Label)((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorText")).setText(" PLAYING ");
                keyboardMacroAnim.play();
            }
        } ));
        if (loopIndefinitely)
            keyboardMacroAnim.setCycleCount(Animation.INDEFINITE);
        else
            keyboardMacroAnim.setCycleCount(loopCount);
        int keystroke_duration = 0;
        animIndex = 0;

        // Build macro animation
        for (int i = 0; i < macroSize; i++) {
            char parsedChar = macroString.charAt(i);
            keycode[i] = getExtendedKeyCodeForChar(parsedChar);

            if (parsedChar == '%')
                keycode[i] = VK_PERCENT;
            if (parsedChar == '?')
                keycode[i] = VK_QUESTION_MARK;
            //System.out.printf("char [%c] keycode = %d%n", parsedChar, keycode[i]);

            switch(keycode[i]) {
                case 0 -> { return; }
                case VK_TILDE, VK_EXCLAMATION_MARK,  VK_AT, VK_NUMBER_SIGN, VK_DOLLAR, VK_PERCENT, VK_CIRCUMFLEX,
                        VK_AMPERSAND, VK_ASTERISK, VK_LEFT_PARENTHESIS, VK_RIGHT_PARENTHESIS, VK_UNDERSCORE, VK_PLUS,
                        VK_OPEN_CURLY_BRACE, VK_CLOSE_CURLY_BRACE, VK_VERTICAL_PIPE, VK_COLON, VK_DOUBLE_QUOTE,
                        VK_LESS_THAN, VK_GREATER_THAN, VK_QUESTION_MARK ->
                    keyboardMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration += keystrokeDelay), e -> {
                        if (QUIT) {
                            QUIT = false;
                            keyboardMacroAnim.stop();
                            return;
                        }
                        robot.keyPress(VK_SHIFT);
                        robot.keyPress(getUnshiftedKey(keycode[animIndex]));
                        robot.keyRelease(getUnshiftedKey(keycode[animIndex++]));
                        robot.keyRelease(VK_SHIFT);
                    }));
                default -> {
                    if (Character.isUpperCase(parsedChar)) {
                        keyboardMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration += keystrokeDelay), e -> {
                            if (QUIT) {
                                QUIT = false;
                                keyboardMacroAnim.stop();
                                return;
                            }
                            robot.keyPress(VK_SHIFT);
                            robot.keyPress(keycode[animIndex]);
                            robot.keyRelease(keycode[animIndex++]);
                            robot.keyRelease(VK_SHIFT);
                        }));
                    }
                    else {
                        keyboardMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration += keystrokeDelay), e -> {
                            if (QUIT) {
                                QUIT = false;
                                //GlobalScreen.removeNativeKeyListener(MacroMaker_API_MouseMacroController.killMacroListener);          // DEPRECATED
                                ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(false);
                                keyboardMacroAnim.stop();
                                return;
                            }
                            robot.keyPress(keycode[animIndex]);
                            robot.keyRelease(keycode[animIndex++]);
                        }));
                    }
                }
            }
        }
        // Reset index tracker at the end of the animation and check to see if indicator needs to be turned off
        keyboardMacroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration += keystrokeDelay), e -> {
            animIndex = 0;
            if (keyboardMacroAnim.getCycleCount() == loopCount)
                ((Button)actionEvent.getTarget()).getScene().lookup("#macroIndicatorField").setVisible(false);
        }));
        System.out.println("finished adding frames to animation");

        // Start macro animation
        KB_WaitingAnim.play();
    }


    public synchronized void killKeyboardAnimation() {
        try {
            KB_WaitingAnim.stop();
            keyboardMacroAnim.stop();
            System.out.println("*** KEYBOARD-REPEATER-ANIMATION-KILLED ***");        // notification-print
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("*** FAILED-TO-KILL-KEYBOARD-MACRO-ANIMATION ***");   // notification-print
        }
    }


    public synchronized void killRT_KeyboardAndMouseAnimation() {
        try {
            RTM_WaitingAnim.stop();
            RT_KeyboardAndMouseMacroAnim.stop();
            System.out.println("*** RTM-ANIMATION-KILLED ***");           // notification-print
        } catch (Exception e) {
            System.out.println("*** FAILED-TO-KILL-RTM-ANIMATION ***");   // notification-print
            e.printStackTrace();
        }
    }


    public synchronized int getUnshiftedKey(int key) {
        return switch (key) {
            case VK_TILDE -> VK_BACKTICK;
            case VK_EXCLAMATION_MARK -> VK_1;
            case VK_AT -> VK_2;
            case VK_NUMBER_SIGN -> VK_3;
            case VK_DOLLAR -> VK_4;
            case VK_PERCENT -> VK_5;
            case VK_CIRCUMFLEX -> VK_6;
            case VK_AMPERSAND -> VK_7;
            case VK_ASTERISK -> VK_8;
            case VK_LEFT_PARENTHESIS -> VK_9;
            case VK_RIGHT_PARENTHESIS -> VK_0;
            case VK_UNDERSCORE -> VK_MINUS;
            case VK_PLUS -> VK_EQUALS;
            case VK_OPEN_CURLY_BRACE -> VK_OPEN_BRACKET;
            case VK_CLOSE_CURLY_BRACE -> VK_CLOSE_BRACKET;
            case VK_VERTICAL_PIPE -> VK_BACK_SLASH;
            case VK_COLON -> VK_SEMICOLON;
            case VK_DOUBLE_QUOTE -> VK_SINGLE_QUOTE;
            case VK_LESS_THAN -> VK_COMMA;
            case VK_GREATER_THAN -> VK_PERIOD;
            case VK_QUESTION_MARK -> VK_SLASH;
            default -> 0;
        };
    }


    public synchronized int getInitialWaitTime_ms() {
        return initialWaitTime_ms;
    }


    public synchronized void setInitialWaitTime_ms(int initialWaitTime) {
        if (initialWaitTime < 1000)
            this.initialWaitTime_ms = 1000;
        else
            this.initialWaitTime_ms = Math.min( initialWaitTime, 30000 );
    }


    public synchronized int getKeystrokeDelay() {
        return keystrokeDelay;
    }


    public synchronized void setKeystrokeDelay(int keystrokeDelay) {
        this.keystrokeDelay = keystrokeDelay;
        if (this.keystrokeDelay < 1)
            this.keystrokeDelay = 1;
        else if (this.keystrokeDelay > 100)
            this.keystrokeDelay = 100;
    }


    public synchronized void setLoopIndefinitely(boolean loopIndefinitelyCheckbox) {
        loopIndefinitely = loopIndefinitelyCheckbox;
    }


    public synchronized void setLoopCount(int loopCount) {
        if (loopCount > 0)
            this.loopCount = loopCount;
        else
            this.loopCount = 1;
    }


    public synchronized String getScreenDimensions() {
        return (int)screenWidth + "x" + (int)screenHeight;
    }


    public synchronized String getMousePosition() {
        return "X" + MouseInfo.getPointerInfo().getLocation().x + "  Y" + MouseInfo.getPointerInfo().getLocation().y;
    }

    public synchronized int getMousePosX() {
        return MouseInfo.getPointerInfo().getLocation().x;
    }
    public synchronized int getMousePosY() {
        return MouseInfo.getPointerInfo().getLocation().y;
    }

}
