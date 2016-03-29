package com.zayacam.game.bidwhist.cards;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zayacam.game.Assets;
import com.zayacam.game.bidwhist.game.CardPlay;
import com.zayacam.game.bidwhist.game.GamePlay;

public class Card  {

    public Rectangle bounds;
    Image playingCard;
    private GamePlay gamePlay;
    private CardFacePosition cardFaceDisplay;
    private CardSuit cardSuit;
    private CardFace cardFace ;
    private ICard cardEvents;
    private int grpIndexName;
    private float cardValue;
    private String cardFaceValue;
    private float deckValue;
    private boolean bidDud;
    private boolean trumpCard;
    private boolean raisedCard;
    private boolean readyToPlay;
    private boolean available;

    //region ctor

    private Card() {
        this.cardFaceDisplay = CardFacePosition.FaceDown;
        this.cardFace = CardFace.None;
        GamePlay.RestCard(this);

    }

    public Card (GamePlay gamePlay) {
        this();
        this.gamePlay = gamePlay;
        this.cardEvents = gamePlay;
        this.bidDud = false;
    }

    public Card(GamePlay gamePlay, CardSuit cs, float cardValue, float deckValue) {
        this(gamePlay);
        this.cardSuit = cs;
        this.cardValue = cardValue;
        this.cardFaceValue = ConvertToFaceValue(cardValue);
        this.deckValue = deckValue;
    }

    public Card(GamePlay gamePlay, CardSuit cs, int i) {
        this(gamePlay,cs,i,i);
    }

    //endregion

    public void setPlayingCard(Image cardImg) {
        bounds = new Rectangle(0, 0, Assets.CardWidth, Assets.CardHeight);
        this.playingCard = cardImg;
        this.playingCard.setPosition(0, Assets.P1YBaseLine);
        this.playingCard.setName(this.toStringBef());
        this.playingCard.setBounds(0, 0, Assets.CardWidth, Assets.CardHeight);
        this.playingCard.setTouchable(Touchable.enabled);
    }

    public Image PlayingCard() {
        return playingCard;
    }

    public CardSuit getCardSuit() {
        return this.cardSuit;
    }

    public void setCardSuit(CardSuit cardSuit) {
        this.cardSuit = cardSuit == null ? CardSuit.NoTrump : cardSuit;
    }

    public float getCardValue() {
        return this.cardValue;
    }

    public float setCardValue(float cardValue) {
        ConvertToFaceValue(cardValue);
        return this.cardValue = cardValue;
    }

    public float getDeckValue() {
        return this.deckValue;
    }

    public String getFaceValue() {
        return this.cardFaceValue;
    }

    public boolean IsFaceDown() {
        return this.cardFaceDisplay == CardFacePosition.FaceDown ? true : false ;
    }

    private String ConvertToFaceValue(float cardValue) {
        String result ;

        switch ((int) cardValue) {
            case 1:
            case 14:
                result = "Ace";
                this.cardFace = CardFace.Ace;
                break;
            case 11:
            	result = "Jack";
                this.cardFace = CardFace.Jack;
            	break;
            case 12:
            	result = "Queen";
                this.cardFace = CardFace.Queen;
            	break;
            case 13:
            	result = "King";
                this.cardFace = CardFace.King;
                break;
            case 53 :
            	result = "Little";
                this.cardFace = CardFace.Joker;
            	break;
            case 54:
                result = "Big";
                this.cardFace = CardFace.Joker;
            	break;
            default:
                result = Float.toString(cardValue);
        }
        return result;

    }

    public String toStringBef() {
    	String suitImg = "", result = "";
        try {

            // set the card suit
            switch (cardSuit) {
                case Heart:
                    suitImg = "♥";
                    break;
                case Diamond:
                    suitImg = "♦";
                    break;
                case Spade:
                    suitImg = "♠";
                    break;
                case Club:
                    suitImg = "♣";
                    break;
                case Joker:
                    suitImg = (cardValue == 54) ? "♀" : "♂";
                    break;
                case NoTrump:
                    suitImg = "";
                    break;
            }
        } catch (Exception ex) {
            System.out.println("No card suit defined!");
        }

        // set the card's face value
    	switch (this.cardFace){
	    	case Jack:
	    	case Queen:
	    	case King:
	    	case Ace:
            case Joker:
                result = this.cardFaceValue.substring(0, 1);
	    		break;
			default:
                result = this.cardFaceValue;
    	}
        result = (String.format("%1$2s", result) + suitImg).trim();
        return result;
   }

    public CardFace getCardFace() {
        return cardFace;
    }

    public boolean isBidDud() {
        return bidDud;
    }

    public void SetBidDud(boolean bidDud) {
        this.bidDud = bidDud;
    }

    public boolean IsAJoker() {
        return this.cardFace == CardFace.Joker;
    }

    public void SetTrumpCard(boolean trumpCard) {
        this.trumpCard = trumpCard;
    }

    public boolean IsTrumpCard() {
        return this.trumpCard;
    }

    @Override
    public String toString() {
        return (this.cardFaceValue + (this.cardSuit == CardSuit.Joker ? "" : " of ") +
                this.cardSuit.toString() + " : " + Float.toString(this.deckValue)).trim() ;
    }

    public void CardSelected(CardPlay cardPlay) {
        this.SetAvailable(false);

    }

    public void CardUnSelected(CardPlay cardPlay) {

    }

    public void setGrpIndexName(int grpIndexName) {
        this.grpIndexName = grpIndexName;
    }

    public boolean IsRaised() {
        return raisedCard;
    }

    public void SetIsRaised(boolean raised) {
        this.raisedCard = raised;
    }

    public boolean IsReadyToPlay() {
        return readyToPlay;
    }

    public void SetReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public boolean IsAvailable() {
        return available;
    }

    public void SetAvailable(boolean available) {
        this.available = available;
    }

    public boolean IsAnAce() {
        return this.cardFace == CardFace.Ace;
    }
}
