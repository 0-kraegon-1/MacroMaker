package com.example.macromaker_apicontroller;

public class AppData {
    private static final String saveFileTitle = "MacroMaker Save Files";
    private static final String keyMacroSaveFileTitle = "Key Macro Save Files";
    private static final String mouseMacroSaveFileTitle = "Mouse Macro Save Files";
    private static final String appVersion = "2.0";
    private static final String appMainTitle = "MacroMaker " + appVersion;
    private static final double macroFrameRate = 1000d / 90d;   //  90fps
    public enum ActiveApp { KEYBOARD_MACRO_EDITOR, MOUSE_MACRO_EDITOR }
    protected static ActiveApp activeApp = ActiveApp.KEYBOARD_MACRO_EDITOR;



    public synchronized static double GetMacroFrameRate() {
        return macroFrameRate;
    }

    public ActiveApp getActiveApp() {
        return activeApp;
    }

    public synchronized void setActiveApp(ActiveApp activeApp) {
        AppData.activeApp = activeApp;
        System.out.println("ACTIVE-APP-CHANGED:  " + activeApp.name() + " -> ACTIVE");  // debug-print
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getSaveFileTitle() {
        return saveFileTitle;
    }

    public String getMouseMacroSaveFileTitle() {
        return mouseMacroSaveFileTitle;
    }

    public String getKeyMacroSaveFileTitle() {
        return keyMacroSaveFileTitle;
    }

    public String getAppMainTitle() {
        return appMainTitle;
    }
}
