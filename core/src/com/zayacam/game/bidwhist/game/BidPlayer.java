package com.zayacam.game.bidwhist.game;

import com.zayacam.Utils;
import com.zayacam.game.bidwhist.cards.*;

import java.util.*;

public class BidPlayer implements IBidPlayerEvents {

    private IGameEvents gameEvents;
    private UUID id;
    private int teamId;
    private int bidsTaken;
    private int playOrder;
    private String playerName;
    private Hand hand;
    private boolean isDealer = false;
    private boolean handWinner;
    private int index;
    private String chatter = "";
    private boolean awardedTheBid;
    private boolean hasSound = false;
    private boolean alwaysSortAscending = false;
    private boolean sortJokersWithTrump = false;
    private boolean autoPlay = false;
    private boolean isHuman;
    private int bidHand_Books ;
    private boolean playerHasBidded = false;
    private CardSuit bidSuit;
    private boolean playerHasPlayed = false;

    private BidPlayer() {
        id = UUID.randomUUID();
        hand = new Hand();
        awardedTheBid = false;
        bidsTaken = 0;
        isHuman = false;
        handWinner = false;
    }

    public BidPlayer(IGameEvents gameEvents) {
        this();
        this.gameEvents = gameEvents;
    }

    public BidPlayer(String playerName, IGameEvents gameEvents) {
        this(gameEvents);
        this.playerName = playerName.trim();
        //System.out.println("\t" + playerName + " created!");
    }

    public UUID getId() {
        return id;
    }

    public boolean isAwardedTheBid() {
        return awardedTheBid;
    }

    public void setAwardedTheBid(boolean awardedTheBid) {
        this.awardedTheBid = awardedTheBid;
        if (awardedTheBid)
            gameEvents.BidAwarded();
    }

    private GamePlay.BidRule_Direction bidHand_Direction;

    public void bidHand() {
        char choice;
        System.out.print("\nBidding " + this.playerName + " \n" );
        this.hand.ShowCards();
        System.out.println();
        boolean doneBidding = false;

        bidsTaken = 0;
        Scanner sc = new Scanner(System.in);

        do {
            try {
                ShowGameBid();
                int bidBooks = 0;
                System.out.println("\n\tbid books or \"0\" to pass> ");
                choice = sc.next().charAt(0);
                if (Utils.isNumeric(Character.toString(choice)))
                    bidBooks = Integer.parseInt(Character.toString(choice));
                else {
                    if (choice == 'q') GamePlay.RunQuitGame();
                }

                if (bidBooks == 0) {
                    PassingBid(this);
                    break;
                }
                setBidHand_Books(bidBooks);
                System.out.println("\tEnter direction:  (D)ownTown, (U)pTown or (N)o Trump");
                char directionChoice = sc.next().charAt(0);

                int iDirection = 0;
                do {
                    switch (directionChoice) {
                        case 'D':
                        case 'd':
                            iDirection = GamePlay.BidRule_Direction.Downtown.getValue();
                            break;
                        case 'U':
                        case 'u':
                            iDirection = GamePlay.BidRule_Direction.Uptown.getValue();
                            break;
                        case 'N':
                        case 'n':
                            iDirection = GamePlay.BidRule_Direction.NoTrump.getValue();
                            break;
                        default:
                            Utils.Beep();
                            directionChoice = sc.next().charAt(0);
                            break;
                    }
                } while (iDirection <= 0);

                GamePlay.BidRule_Direction direction  = GamePlay.BidRule_Direction.fromValue(iDirection);
                setBidHand_Direction(direction);

                doneBidding = gameEvents.ValidatePlayersBid(this);
                if (doneBidding)
                    PlayerIsBidLeader(this);
            }
            catch (Exception ex) {
                System.out.println(toString().format("\n\t*** %1s ***\n", ex.toString()));
            }
        } while ( !doneBidding );
    }

    private void ShowGameBid() {
        if (GamePlay.GAME_BOOKS > 0) {
            System.out.println(toString().format("\t[ %1d  %2s ] Enter take: or '0' to pass your bid.",
                    GamePlay.GAME_BOOKS,
                    GamePlay.GAME_DIRECTION != null ? "- " + GamePlay.GAME_DIRECTION.name() : ""));
        }
    }

    public int getBidHand_Books() {
        return bidHand_Books;
    }

    public void setChatter(String chatter) {
        this.chatter = chatter;
    }

    public void setBidHand_Books(int bidHand_Books) {
        this.bidHand_Books = bidHand_Books;
    }

    public GamePlay.BidRule_Direction getBidDirection() {
        return bidHand_Direction;
    }

    public void setBidHand_Direction(GamePlay.BidRule_Direction bidDirection) {
        this.bidHand_Direction = bidDirection;
    }

    public String getPlayerName() {
        return playerName.trim();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName.trim();
    }

    public Hand getHand() {
        return hand;
    }

    public String getBid() {
        String bid;
        bid =  String.format("%1s ", Integer.toString(this.bidHand_Books ) );

        return bid;
    }

    public String getChatter() {
        return chatter;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void setHuman(boolean human) {
        isHuman = human;
    }

    public int getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index ;
    }

    public boolean isHandWinner() {
        return handWinner;
    }

    public void SetHandWinner(boolean handWinner) {
        this.handWinner = handWinner;
        if (handWinner)
            gameEvents.WonThisHand(this);
    }

    public Card PlayCard(int i) {
        Card card = this.getHand().remove(i);
        return card;
    }

    @Override
    public String toString() {
        return String.format("%1s", playerName);
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getBidsTaken() {
        return bidsTaken;
    }

    public void AddToBidTaken() {
        this.bidsTaken += 1;
    }

    @Override
    public void PlayerIsBidLeader(BidPlayer player) {
        System.out.println(String.format("\t*** %1s,  I'm winning the bid, thus far! ***", getPlayerName()));
    }

    @Override
    public void PassingBid(BidPlayer thisPlayer) throws InterruptedException {
        Utils.Beep();

        System.out.println("\t**** Yo peeps,  I'm passing this bid.");
    }

    @Override
    public String TableChatter(BidPlayer thisPlayer) {
        return null;
    }

    @Override
    public boolean PlayedTrumpCard(Card card) {
        System.out.println("\t\t*** "+ this.getPlayerName() +" says:  \"Hommie, I'm cutting!\"");
        card.setTrumpCard(true);
        return true;
    }

    @Override
    public void DiscardKittyTrades(int size) {
        boolean isDone=false, isQuitting = false;
        System.out.println(String.format("\nSelect %1d cards, to discard:> ",
                size));

        getHand().SortCards(SortBy.DeckValue);
        ArrayList<Integer> outIndex = new ArrayList<>(size);
        Scanner sc = new Scanner(System.in);

        do {
            final String selected  = sc.next() ;
            if (Utils.isNumeric(selected)) {
                if (!outIndex.contains( Integer.parseInt(selected)))
                    outIndex.add(Integer.parseInt(selected));
                else if (outIndex.contains( Integer.parseInt(selected)))
                    outIndex.removeIf(p->p.toString() == selected);
            } else switch (selected.charAt(0)) {
                case 'q':
                case 'Q':
                    isQuitting = true;
                    break;
            }
            if (outIndex.size() >= size )
                isDone = true;

        } while (!isDone && !isQuitting);


        outIndex.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? -1 : 1;
            }
        });
        System.out.println("Final list: " + outIndex.toString());

        System.out.println("\n");
        ArrayList disCards = new ArrayList<Card>(outIndex.size());
        for (int i = 0; i < outIndex.size(); i++) {
            Card c = this.getHand().get(outIndex.get(i));
            c =  this.getHand().getCard(c);
            disCards.add(c);
            //System.out.println("Removing: " + c.toStringBef());
        }
        this.getHand().removeAll(disCards);
    }

    @Override
    public int AutoPlayCard(CardSuit leadSuit, int handRound) {

        int result = 0;
        Random random = new Random();
        ArrayList<Card> cardsOfSuit = new ArrayList<>();

        System.out.print("\n\tThinking .....");

        for (Card c: getHand().getCards()) {
            if (c.getCardSuit() == leadSuit) {
                cardsOfSuit.add(c);
            }
        }
        if (cardsOfSuit.size() == 0 && hand.getSize() >= 0) {
            try {
                result = random.nextInt(hand.getSize());
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        else {
            int randIndex = random.nextInt(cardsOfSuit.size());
            Card c = cardsOfSuit.get(randIndex);
            result = getHand().indexOf(c);
        }
        return result;
    }

    @Override
    public void ThrowInCards() throws InterruptedException {
        Utils.Beep(3);
        System.out.println("\n\n\n");
        for (int i = 0; i < 4; i++) {
            System.out.println("\t\t\t\t\t\t\t-=<[ *** Yo, we got set! *** ]>=-\n");
        }
    }

    @Override
    public boolean PlayerHasBidded() {
        return playerHasBidded;
    }

    @Override
    public boolean HasPlayed() {
        return playerHasPlayed;
    }

    public void SetPlayerHasPlayed(boolean hasPlayed) {
        this.playerHasPlayed = hasPlayed;
    }


    public void setPlayerHasBidded(boolean playerHasBidded) {
        this.playerHasBidded = playerHasBidded;
    }

    public CardSuit getBidSuit() {
        return bidSuit;
    }

    public void setBidHand_Suit(CardSuit bidHand_Suit) {
        this.bidSuit = bidHand_Suit;
    }
}
