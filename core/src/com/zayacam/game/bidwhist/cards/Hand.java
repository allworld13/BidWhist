package com.zayacam.game.bidwhist.cards;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.ArrayList;
import java.util.Collections;


public class Hand extends ArrayList<Card> implements IHand{

	public ArrayList<Sprite> sprite_hand;

	public Hand() {
		super();
	}

	@Override
	public ArrayList<Card> getCards() {
		return this;
	}

	@Override
	public void SortCards(SortBy sortBy) {
		SortCards(sortBy, false);
	}

	@Override
	public void SortCards(SortBy sortBy, boolean ascending) {
		Collections.sort(this, new CompareTo(sortBy, ascending));
	}

	@Override
	public boolean HasCard(Card card) {
		return false;
	}

	@Override
	public void AddCard(Card card) {
		this.add(card);
	}

	@Override
	public void AddCard(ArrayList<Card> cards) {
		if (cards == null) return;
		for (Card c: cards) {
			if (!this.contains(c))
				this.add(c);
		}
	}

	@Override
	public Card RemoveCard(int index) {
        Card result = null;

        if (this.size() > 0  && index > -1 && index <= this.size())
        {
            result = this.get(index);
            this.remove(index);
        }
        return result;
	}

	@Override
	public void RemoveCard(Card card) {
        this.remove(card);
	}

	@Override
	public int getSize() {
		return this.size();
	}

	public String GetCardsString() {
		final int noOfCards = this.size();
		StringBuilder result = new StringBuilder();
		result.append("  ");
//		result.append("\n  ");
		for (int i = 0; i < noOfCards; i++) {
			if (i>=11)
				result.append(String.format("%1$3d   ", i) );
			else if (i>6)
				result.append(String.format("%1$4d  ", i) );
			else
				result.append(String.format("%1$3d  ", i) );
		}
		result.append("\n");

		for (Card c: this.getCards()) {
			result.append("| "+ c.toStringBef()+ " " );
		}

		result.append("|");
		return result.toString();
	}

	public void ShowCards(SortBy sortBy) {
		SortCards(sortBy);
		System.out.print(GetCardsString());
	}

	@Override
	public void ShowCards() {
		ShowCards(SortBy.DeckValue);
	}

	@Override
	public Card getCard(Card thisCard) {
		for (Card c: this) {
			if (c.getDeckValue() == thisCard.getDeckValue())
				return c;
		}
		return null;
	}

	@Override
	public ArrayList<Card> getCards(int noOfCards) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean HasSuit(CardSuit lookfor) {
		boolean hasThisSuit = false;
		try {
			hasThisSuit = this.stream().anyMatch(c -> c.getCardSuit().equals(lookfor));
		} catch (Exception ex) {
			System.out.println("XXX ERROR XXX : " + ex.toString());
		}
		return hasThisSuit;
	}

	public void SetJokerSuit(CardSuit gameSuit, GamePlay.BidRule_Direction direction) {
		float deckValue, cardValue;
		String faceValue;
		for (Card c: this) {
			c.SetBidDud(false);

			deckValue = c.getDeckValue();
			faceValue = c.getFaceValue();
			cardValue = c.getCardValue();

			if (c.IsAJoker()) {
				c.setCardSuit(gameSuit);
				if (GamePlay.GAME_SUIT.equals(CardSuit.NoTrump)) {
					c.SetBidDud(true);
					continue;
				}
			}
			switch (direction) {
				case Uptown:
					if (c.IsAnAce())
						c.setCardValue(c.getCardValue() + (float) 13.5);
					break;
				case Downtown:
					if (c.IsAJoker()) {
						if ((int) c.getDeckValue() == 53)
							c.setCardValue(-2);
						else
							c.setCardValue(-3);
					} else if (c.IsAnAce()) {
						c.setCardValue(-1);
					} else {
						//c.setCardValue(0);
					}
					break;
			}
		}
	}

}