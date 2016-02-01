package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.ComparePlayerTo;
import com.zayacam.game.bidwhist.cards.SortBy;

import java.util.ArrayList;
import java.util.Collections;

public class GameTable extends ArrayList<CardPlay> {
    public GameTable() {}

    public ArrayList<CardPlay> getCardsPlay(int playRound) {
        ArrayList<CardPlay> plays = new ArrayList<CardPlay>();
        boolean foundPlays = false;
        for (CardPlay cp :this) {
            if (cp.getPlayRound() == playRound) {
                plays.add(cp);
                foundPlays = true;
            }
        }
        return foundPlays ? plays : null;
    }

}
