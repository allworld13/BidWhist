package com.zayacam.game.bidwhist.cards;

import java.util.ArrayList;


public interface IHand {
	/*
     *  Creates a new deck, by initially assigning all cards to the collection
     */

	void SortCards(SortBy sortStyle);
	void SortCards(SortBy sortStyle, boolean ascending);
	boolean HasCard(Card card);
	boolean HasSuit(CardSuit suit);
	void AddCard(Card card);
	void AddCard(ArrayList<Card> cards);
	Card RemoveCard(int index);
	void RemoveCard(Card card);
	ArrayList<Card> getCards();
	ArrayList<Card> getCards(int noOfCards);
	int getSize();
	void ShowCards();
	Card getCard(Card c);
}
