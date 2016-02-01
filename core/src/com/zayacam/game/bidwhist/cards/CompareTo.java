package com.zayacam.game.bidwhist.cards;

import java.util.Comparator;

public class CompareTo implements Comparator<Card> {

	SortBy sortType = SortBy.DeckValue;
	boolean sortAscending ;
	
	public CompareTo(SortBy sortBy) {
		sortAscending = true;
		this.sortType = sortBy;
	}
	
	public CompareTo(SortBy sortBy, boolean ascending) {
		this(sortBy);
		this.sortAscending = ascending;
	}
	
	@Override
	public int compare(Card c1, Card c2) {
		int result;

		if (this.sortAscending) {
			switch (this.sortType) {
				case Suit :
					result = (c1.getCardSuit() == c2.getCardSuit() ? -1 : 0);
					break;
				case FaceValue:
					result = (c1.getFaceValue() == c2.getFaceValue() ? -1 : 0);
					break;
				case DeckValue :
				default:
					result = (c1.getDeckValue() < c2.getDeckValue() ? -1 : 0);
					break;
			}
		} else {
			switch (this.sortType) {
				case Suit :
					result = (c1.getCardSuit() == c2.getCardSuit() ? 0 : -1);
					break;
				case FaceValue:
					result = (c1.getFaceValue() == c2.getFaceValue() ? 0 : -1);
					break;
				case DeckValue :
				default:
					result = (c1.getDeckValue() < c2.getDeckValue() ? 0 : -1);
					break;
			}
		}

		return result;
	}
}
