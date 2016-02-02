package com.zayacam.game.bidwhist.cards;

public interface IDeckEvents {
    void DeckCreated();
    void DeckShuffled();

    void JokersRemoved();

    void DeckShuffling();
}
