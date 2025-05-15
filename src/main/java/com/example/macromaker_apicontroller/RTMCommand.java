package com.example.macromaker_apicontroller;

import java.util.List;
/*
         * REAL-TIME-MACRO-COMMAND *
       0   <x>      ->    Mouse X Position
       1   <y>      ->    Mouse Y Position
       2   <mb1>    ->    Mouse Left-Click State
       3   <mb2>    ->    Mouse Right-Click State
       4   <mb3>    ->    Mouse Scroll-Click State
       5   <mw>     ->    Mouse Scroll-Dir State
       6   <keys>   ->    Active Key States
*/
public record RTMCommand(int x, int y, int mb1, int mb2, int mb3, int mw, List<String> keys) {
    @Override
    public String toString() {
        return x + " | " + y + " | " + mb1 + " | " + mb2 + " | " + mb3 + " | " + mw + " | " + keys.toString();
    }
}
