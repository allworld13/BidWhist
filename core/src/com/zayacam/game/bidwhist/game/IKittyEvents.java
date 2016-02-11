package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public interface IKittyEvents {
    void KittyCardPlayed(_BidWhistStage stage, Card selectedCard);
}
