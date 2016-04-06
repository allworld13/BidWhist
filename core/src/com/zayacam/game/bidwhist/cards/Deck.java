package com.zayacam.game.bidwhist.cards;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.zayacam.game.Assets;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.ArrayList;
import java.util.Random;

public class Deck implements IHand {
    public static final int INIT_DECKSIZE = 52;
    public static final int SUIT_LENGTH = 13;
	ArrayList<Card> deck;
	boolean excludeJokers;
	private GamePlay gamePlay;
	private IDeckEvents deckEvents;

	private Deck() {

	}

	private Deck(GamePlay gameplay) {
		this();
		this.gamePlay = gameplay;
		this.deckEvents =  gameplay;
		this.excludeJokers = false;
		Make();
	}

	public Deck(GamePlay gamePlay, boolean excludeJokers) {
		this(gamePlay);
        this.excludeJokers = excludeJokers;
        if (excludeJokers) {
        	this.excludeJokers = excludeJokers;
            this.RemoveJokersFromDeck();
        }
		if (deckEvents!= null)
			deckEvents.DeckCreated();
	}

    protected ArrayList<Card> getDeck() {
        return deck;
    }

    protected ArrayList<Card> getDeck(int noOfCards) {
    	ArrayList<Card> result = new ArrayList<Card>() ;
    		for(int x=0; x< noOfCards;x++) {
    			result.add(deck.get(x));
    		}
    	return result;
    }

    /*
     *  Creates a new deck, by initially assigning all cards to the collection
     */
	void Make() {
        deck = new ArrayList<>();
        Card card;
		CardSuit cs;
		int cardValue;
		Assets.DrawDeckOfCards();
		for (int deckIndex = 0; deckIndex < INIT_DECKSIZE; deckIndex++) {
			cs = DetermineCardSuit(deckIndex);
			cardValue = (deckIndex % SUIT_LENGTH) + 1;

			card = new Card(gamePlay, cs, cardValue, deckIndex+1);
			if (Assets.isDeckDrawn ) {
				card.setPlayingCard(Assets.gfxDeck.get(deckIndex));
				card.PlayingCard().setTouchable(Touchable.enabled);
			}
			card.SetIsRaised(false);
			this.AddCard(card);
        }
		cs = CardSuit.Joker;

		card = new Card(gamePlay, cs, 53);
		if (Assets.isDeckDrawn) card.setPlayingCard(Assets.gfxDeck.get(52));
        deck.add(card);

		card = new Card(gamePlay, cs, 54);
		if (Assets.isDeckDrawn) card.setPlayingCard(Assets.gfxDeck.get(53));
        deck.add(card);

		this.Init();
	}

	private CardSuit DetermineCardSuit(int deckIndex) {
		CardSuit cs = null;

		if (deckIndex < 13) {
			cs = CardSuit.Heart;
		} else if (deckIndex >= 13 * 1  & deckIndex < 13 * 2) {
			cs = CardSuit.Spade;
		} else if (deckIndex >= 13 * 2 & deckIndex < 13 * 3) {
			cs = CardSuit.Diamond;
		} else if (deckIndex >= 13 * 3 & deckIndex < 13 * 4) {
			cs = CardSuit.Club;
		} else if (deckIndex >= 52 & deckIndex <54 ) {
			cs = CardSuit.Joker;
		}
		return cs;
	}

	void RemoveJokersFromDeck() {
        for (Card c: deck) {
            if (c.getDeckValue() >= INIT_DECKSIZE - 2) {
                this.RemoveCard(c);
            }
        }
		if (deckEvents != null)
			deckEvents.JokersRemoved();
    }

    public void Shuffle() {

		if (deckEvents != null)
			deckEvents.DeckShuffled();

		Random rand = new Random();
		ArrayList<Card> newDeck =  new ArrayList<Card>();
    	Card c;
    	int nextNumber ;
    	for (int x = deck.size(); x > 0; x-- ) {
    		nextNumber = rand.nextInt(x);
    		c = deck.get(nextNumber);
    		newDeck.add(c);
    		deck.remove(c);
    	}
    	this.deck.clear();
    	this.deck = newDeck;

		if (deckEvents != null)
			deckEvents.DeckShuffled();
    }
    
    public Card Deal() {
        Card result = null;
        int remainingSize = deck.size();
        if (remainingSize > 0 ) {
            result = deck.get(0);
            deck.remove(0);
        }
        return result;
    }

    public ArrayList<Card> Deal(int noCardsToDeal) {
        ArrayList<Card> result = null;
        int remainingSize = deck.size();
        if (remainingSize - noCardsToDeal >= 0 ) {
            result = new ArrayList<Card>(noCardsToDeal);
            for (int i = 0; i < noCardsToDeal; i++) {
                result.add(this.Deal());
            }
        }
        
        return result;
    }

	@Override
	public void SortCards(SortBy sortStyle, boolean ascending) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean HasCard(Card c) {
		for (Card card : deck) {
			if (card.getDeckValue() == c.getDeckValue())
				return true;
		}
		return false;
	}

	@Override
	public boolean HasSuit(CardSuit suit) {
		for (Card card : deck) {
			if (card.getCardSuit() == suit)
				return true;
		}
		return false;
	}

	@Override
	public void AddCard(Card c) {
		if (!deck.contains(c)) 
			deck.add(c);
	}

	@Override
	public void AddCard(ArrayList<Card> cards) {
		// TODO: 1/3/2016
	}

	@Override
	public void RemoveCard(Card thisCard) {
		if (this.deck.contains(thisCard))
			this.deck.remove(thisCard);
	}
	
	@Override
	public Card RemoveCard(int index) {
		Card result = null;
		if (this.getSize() >= index) {
			result = this.deck.get(index);
			this.RemoveCard(index);
		}
		return result ;
	}

	@Override
	public int getSize() {
		 return deck.size();
	}

	@Override
	public void ShowCards() {
		// TODO: 1/3/2016
	}

	@Override
	public Card getCard(Card c) {
		return null;
	}

	@Override
	public ArrayList<Card> getCards() {
		return deck;
	}

	@Override
	public ArrayList<Card> getCards(int noOfCards) {
		ArrayList<Card> result = new ArrayList<Card>();
		
		for(int i=0;i<noOfCards;i++) {
			result.add(deck.get(i));
		}
		return result;
	}

	@Override
	public void SortCards(SortBy sortStyle) {
		// TODO Auto-generated method stub
		
	}

	public void Init() {
		for (Card c : this.deck) {
			c.SetAvailable(true);
			c.SetReadyToPlay(true);
			c.SetIsRaised(false);
		}
	}
}
