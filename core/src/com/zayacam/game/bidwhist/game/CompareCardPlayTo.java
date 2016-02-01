package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.SortBy;

import java.util.Comparator;

public class CompareCardPlayTo implements Comparator<CardPlay> {

	SortBy sortType = SortBy.DeckValue;
	boolean sortAscending ;

	public CompareCardPlayTo(SortBy sortBy) {
		sortAscending = true;
		this.sortType = sortBy;
	}

	public CompareCardPlayTo(SortBy sortBy, boolean ascending) {
		this(sortBy);
		this.sortAscending = ascending;
	}

	@Override
	public int compare(CardPlay p1, CardPlay p2) {
		int result;

		if (this.sortAscending) {
			switch (this.sortType) {
				case PlayerOrder:
				default:
					result = (p1.card.getCardValue() < p2.card.getCardValue() ? -1 : 0);
					break;
			}
		} else {
			switch (this.sortType) {
				case PlayerOrder:
				default:
					result = (p1.card.getCardValue() < p2.card.getCardValue() ? 0 : -1);
					break;
			}
		}

		return result;
	}

	
}
