package com.zayacam.bidwhist;

import com.zayacam.Utils;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.game.MainMenu;

public class Main {
    //private static final SortCards sortBy ;

    public static void main(String[] args) throws Exception {
        char choice;
/*
		System.out.println("\n\nTesting Deck Class");
//		TestDeck.DisplayAllCards();
		TestDeck.ShuffleDeck();
		System.out.println("\n\nTesting Hand Class");

		System.out.println("\n\nTesting Hand Class");
		TestHand.ShowHand();
		TestHand.SortByDeckValue();
		TestHand.SortBySuit();
		TestHand.SortByFaceValue();

*/
        boolean exitLoop = false;
        MainMenu mainMenu = new MainMenu();
        do {
            choice = mainMenu.SelectChoice();
            switch (choice) {
                case 'm':
                    mainMenu.DrawMainMenu();
                    break;
                case 'n':
                    RunNewGame();
                    break;
                case 'c':
                    Utils.ClearScreen();
                    break;
                case 'q':
                    exitLoop = true;
                    break;
                default:
                    System.out.println(mainMenu.DrawMainMenu());
                    break;
            }
        } while (choice != 'q' || !exitLoop);
        if (choice == 'q') GamePlay.RunQuitGame();
    }

    public static void RunNewGame() throws InterruptedException {
        GamePlay game = new GamePlay();
        game.Init();
        //game.PlayTheGame();
    }
}