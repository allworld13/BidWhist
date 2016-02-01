package com.zayacam.game.bidwhist.cards;

public enum CardFacePosition {
    FaceDown(1) ,
    FaceUp(2);

    final int id;
    CardFacePosition(int id) { this.id = id; }
    public int getValue() {return id;}

    public static CardFacePosition fromValue(int value) {
        for (CardFacePosition facePosition: CardFacePosition.values()) {
            if (facePosition.id == value) {
                return facePosition;
            }
        }
        return null;
    }
}
