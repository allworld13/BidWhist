package com.zayacam.game.bidwhist.game;

import com.zayacam.Utils;

import java.util.Scanner;

public class MainMenu  {

    public static final char ESC = 27;

    public String DrawMainMenu() {
        String pipeVerical, result = "";
        pipeVerical = "║";
        Utils.ClearScreen();
        System.out.print("╓");
        System.out.print("[Main Menu]");
        for (int i = 0; i < 18; i++) {
            System.out.print("─");
        }

        System.out.print("╖");

        result += "\n" + "\t(N)ew Game";
        result += padRight(" ",39) ;
        result += "\n" +  "\t(O)ptions" ;
        result += padRight(" ",40) ;
        result += "\n" + "\t(I)nfo";
        result += padRight(" ",43);
        result += padRight(" ",43) ;
        result += "\n" +  padRight(" ",5) ;
        result += "\n" + "\t(C)lear Screen";
        result += padRight(" ",35);
        result += "\n"  + "\t(Q)uit";
        result += "\n" + padRight(" ",5) ;
        result += "\n" + "\t\tEnter selection:> ";
        result += padRight(" ",31);
        result += "\n╙";
        for (int i = 0; i < 24; i++) {
            result +=("─");
        }
        result += "╜";

        return result;
    }

    public char SelectChoice() {
        char choice;
        Scanner sc = new Scanner(System.in);
        System.out.println(DrawMainMenu());
        choice = sc.next(".").charAt(0);
        return choice;
    }




    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}