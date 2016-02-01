package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.SortBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TableHand extends ArrayList<CardPlay> {
    public TableHand() {}

    public void Sort(SortBy sortBy) {
        Sort(sortBy, true);
    }
    public void Sort(SortBy sortBy, final boolean ascending) {
//        Collections.sort(this, new CompareCardPlayTo(sortBy, ascending));
        Collections.sort(this, (CardPlay p1, CardPlay p2)
                -> p1.card.getCardValue() < p2.card.getCardValue() ? -1: 1);
    }
}
