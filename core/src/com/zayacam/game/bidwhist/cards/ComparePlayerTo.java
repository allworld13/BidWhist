package com.zayacam.game.bidwhist.cards;

import com.zayacam.game.bidwhist.game.BidPlayer;

import java.util.Comparator;

public class ComparePlayerTo implements Comparator<BidPlayer> {

	SortBy sortType = SortBy.DeckValue;
	boolean sortAscending ;

	public ComparePlayerTo(SortBy sortBy) {
		sortAscending = true;
		this.sortType = sortBy;
	}

	public ComparePlayerTo(SortBy sortBy, boolean ascending) {
		this(sortBy);
		this.sortAscending = ascending;
	}

	@Override
	public int compare(BidPlayer p1, BidPlayer p2) {
		int result;

		if (this.sortAscending) {
			switch (this.sortType) {
				case PlayerIndex:
					result = (p1.getIndex() < p2.getIndex() ? -1 : 0);
					break;
				case PlayerOrder:
				default:
					result = (p1.getPlayOrder() < p2.getPlayOrder() ? -1 : 0);
					break;
			}
		} else {
			switch (this.sortType) {
				case PlayerIndex:
					result = (p1.getIndex() < p2.getIndex() ? 0 : -1);
					break;
				case PlayerOrder:
				default:
					result = (p1.getPlayOrder() < p2.getPlayOrder() ? 0 : -1);
					break;
			}
		}

		return result;
	}

	
}
