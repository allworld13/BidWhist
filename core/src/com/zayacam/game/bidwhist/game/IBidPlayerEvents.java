package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.cards.CardSuit;

public interface IBidPlayerEvents {
    void PlayerIsBidLeader(BidPlayer player);
    void PassingBid(BidPlayer thisPlayer) throws InterruptedException;
    String TableChatter(BidPlayer thisPlayer);
    boolean PlayedTrumpCard(Card card);

    void DiscardKittyTrades(int size);

    int AutoPlayCard(CardSuit leadSuit, int handRound);

    void ThrowInCards() throws InterruptedException;

    boolean PlayerHasBidded();
}
