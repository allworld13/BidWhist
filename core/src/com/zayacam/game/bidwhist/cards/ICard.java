package com.zayacam.game.bidwhist.cards;

import com.zayacam.game.bidwhist.game.CardPlay;

public interface ICard {
    void CardSelectedAndPlayed(CardPlay cardPlay);
    void CardUnSelected(CardPlay cardPlay);
}
