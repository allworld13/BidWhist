package com.zayacam.game.bidwhist.cards;

public enum CardSuit  {
    NoTrump(0),
    Heart(1),
    Spade(2),
    Diamond(3),
    Club(4),
    Joker(10) ;

    final int id;
    CardSuit(int id) { this.id = id; }
    public int getValue() {return id;}

    public static CardSuit fromValue(int value) {
        for (CardSuit suit: CardSuit.values()) {
            if (suit.id == value) {
                return suit;
            }
        }
        return null;
    }

    public String toString() {
        String result = "";
        switch (this) {
            case Heart:
                result = "♥";
                break;
            case Diamond:
                result = "♦";
                break;
            case Spade:
                result = "♠";
                break;
            case Club:
                result = "♣";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }
}

