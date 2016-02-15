package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.Card;

public class CardPlay {
    private int playRound;
    public BidPlayer player;
    public Card card;

    public CardPlay(BidPlayer player, Card card) {
        this.player = player;
        this.card = card;
    }
    public CardPlay(BidPlayer player, Card card, int playRound) {
        this(player,card);
        this.playRound = playRound;
    }

    public int getPlayRound() {
        return playRound;
    }

    public void setPlayRound(int playRound) {
        this.playRound = playRound;
    }

    public void Sort(int playRound, GamePlay.BidRule_Direction direction) {
        //Collections.sort(this, new CompareCardPlayTo(SortBy.FaceValue
        //        , direction == GamePlay.BidRule_Direction.Uptown ? false: true));
    }

    @Override
    public String toString() {
        return String.format("%1s - %2s ", this.player.getPlayerName(),
                this.card.toStringBef());
    }
}
