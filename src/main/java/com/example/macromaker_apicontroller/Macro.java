package com.example.macromaker_apicontroller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.*;

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
    private int buffer = 25;
    private int loopCount = 1;

    private final Robot robot = new Robot();
    //private final double displayWidth;
    //private final double displayHeight;

    public Macro() throws AWTException {
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        displayWidth = screenSize.getWidth();
        displayHeight = screenSize.getHeight();*/
    }

/*
    public void moveMouse(int x, int y) {
        if (x <= displayWidth && y <= displayHeight)
            robot.mouseMove(x, y);
        else
            System.out.println("*** Mouse X/Y input coordinates are outside the bounds of the display ***");
    }
*/

    private Timeline macroAnim;
    public void killAnimation() {
        try {
            macroAnim.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int animIndex;
    public void executeKeyMacro(String macroString) {
        final int macroSize = macroString.length();
        int[] keycode = new int[macroSize];


        macroAnim = new Timeline(
                new KeyFrame(Duration.millis(0))
        );
        final Timeline waitingAnim = new Timeline(
                new KeyFrame(Duration.millis(initialWaitTime_ms), e -> macroAnim.play())
        );
        macroAnim.setCycleCount(loopCount);

        int keystroke_duration = 0;

        animIndex = 0;

        // Macro execution
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
                {
                    macroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration+=buffer), e -> {
                        robot.keyPress(VK_SHIFT);
                        robot.keyPress(getAltKey(keycode[animIndex]));
                        robot.keyRelease(getAltKey(keycode[animIndex++]));
                        robot.keyRelease(VK_SHIFT);
                    }));
                }
                default -> {
                    if (Character.isUpperCase(parsedChar)) {
                        macroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration+=buffer), e -> {
                            robot.keyPress(VK_SHIFT);
                            robot.keyPress(keycode[animIndex]);
                            robot.keyRelease(keycode[animIndex++]);
                            robot.keyRelease(VK_SHIFT);
                        }));
                    }
                    else {
                        macroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration+=buffer), e -> {
                            robot.keyPress(keycode[animIndex]);
                            robot.keyRelease(keycode[animIndex++]);
                        }));
                    }
                }

            }
        }
        macroAnim.getKeyFrames().add(new KeyFrame(Duration.millis(keystroke_duration+=buffer), e -> animIndex = 0));
        System.out.println("finished adding frames to animation");
        waitingAnim.play();
    }


    public int getAltKey(int key) {
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


    public int getInitialWaitTime_ms() {
        return initialWaitTime_ms;
    }

    public void setInitialWaitTime_ms(int initialWaitTime_ms) {
        this.initialWaitTime_ms = initialWaitTime_ms * 1000;
        if (this.initialWaitTime_ms < 1000)
            this.initialWaitTime_ms = 1000;
        else if (this.initialWaitTime_ms > 30000)
            this.initialWaitTime_ms = 30000;
    }


    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
        if (this.buffer < 1)
            this.buffer = 1;
        else if (this.buffer > 100)
            this.buffer = 100;
    }


    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        if (loopCount > 0)
            this.loopCount = loopCount;
        else
            this.loopCount = 1;
    }

}
