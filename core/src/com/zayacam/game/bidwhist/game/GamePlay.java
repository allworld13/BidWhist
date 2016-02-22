package com.zayacam.game.bidwhist.game;

import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.bidwhist.cards.*;

import java.util.*;
import java.util.stream.Collectors;

public class GamePlay extends Thread implements IGameEvents, IDeckEvents, ICard {

    public static boolean PlayerOrderSet;
    private boolean gamePlayerOrderSet;

    public boolean AllPlayersPlayedRound() {
        boolean result = false;
        result = gamePlayers.stream().allMatch(bp -> bp.HasPlayed());
        return result;
    }

    public void SetAcesForUpTown() {

    }

    public enum BidRule_Number_Range {
        PASS(0),
        MIN_BID(3),
        MAX_BID(7);

        final int id;

        BidRule_Number_Range(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }

    }
    public enum BidRule_Direction {
        Downtown(1),
        Uptown(2),
        NoTrump(3);
        final int id;

        BidRule_Direction(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }

        static BidRule_Direction fromValue(int value) {
            for (BidRule_Direction direction : BidRule_Direction.values()) {
                if (direction.id == value) {
                    return direction;
                }
            }
            return null;
        }

    }

    public Deck deck;

    public static IGameEvents gameEvents;
    public boolean SportKitty;
    public boolean ShowKitty;
    public static final int MAX_CARDS_TO_DISCARD = 6;
    public static final int MAX_PLAYER_HANDSIZE = 12;
    public final int MAX_NO_PLAYERS = 4;
    public ArrayList<BidPlayer> gamePlayers;
    public Hand KittyHand;
    public BidPlayer bidWinner;
    public static BidRule_Direction GAME_DIRECTION;
    public static CardSuit GAME_SUIT;
    public static int GAME_BOOKS;
    public TableHand tableHand;

    public static int team1Score, team2Score;


    private boolean gameStarted = false;
    private UUID id;
    private GameTable gameTable;
    private int minimalBid = 4;         // 3 or 4
    private boolean cutCardPlayed;
    private int noCardsInKitty = 6; // 4 to 6
    private boolean noTrumpExchangeJokers = false; // if no trumps, the jokers exchange
    private int noHandsToWin = 3;
    private boolean soundFX = false;
    private boolean bgMusic = false;
    private boolean isNewGame = true;
    private boolean hasGameCompleted;
    //private boolean downTownBeatsUpTown;
    private CardSuit leadSuit = null;
    private int playerPlayCount = 0;

    //region ctors
    public GamePlay() {
        id = UUID.randomUUID();
        gameEvents = this;
        gameEvents.StartingNewGame();
        hasGameCompleted = false;
        GAME_BOOKS = 0;
        isNewGame = true;
    }
    //endregion

    //region init
    public void Init() {
        ShowKitty = false;
        SportKitty = false;
        tableHand = new TableHand();
        InitializeDeck();
        InitializeKitty();
        InitializePlayers();

        bidWinner = gamePlayers.get(0);
        //bidWinner.setAwardedTheBid(true);
    }

    //region init stuff

    //region Deck init
    private void InitializeDeck() {
        CreateDeck();
        ShuffleDeck();
    }

    void CreateDeck() {
        deck = null;
        deck = new Deck(this, false);
    }

    void ShuffleDeck() {
        deck.Shuffle();
    }

    //endregion

    //Creates the game KittyHand
    void InitializeKitty() {
        KittyHand = new Hand();
        KittyHand.AddCard(deck.Deal(noCardsInKitty));
        if (gameEvents != null)
            gameEvents.KittyInitialized();
    }

    //Initially adds all the players to the game, for play
    public void InitializePlayers() {
        gamePlayers = new ArrayList<>();
        BidPlayer newPlayer;
        int teamIndex;
        for (int i = 0; i < MAX_NO_PLAYERS; i++) {
            teamIndex = i % 2 == 0 ? 1 : 2;
            newPlayer = new BidPlayer(String.format("Player %1d", (i + 1)), this);
            newPlayer.setTeamId(teamIndex);
            newPlayer.getHand().AddCard(deck.Deal(MAX_PLAYER_HANDSIZE));
            newPlayer.getHand().SortCards(SortBy.DeckValue);
            if (i == 0) {
                newPlayer.setHuman(true);
                newPlayer.setPlayerName("AllWorld");
            } else newPlayer.setHuman(false);

            gamePlayers.add(newPlayer);
            newPlayer.setIndex(gamePlayers.size());
        }
        if (gameEvents != null)
            gameEvents.PlayersInitialized();
    }

    //endregion

    //region all players bidding

    void collectPlayersBids() {
        BidPlayer bp;
        System.out.println(
                String.format("\nStart Bidding between %1d and %2d, or press %3d to pass your bid.",
                        BidRule_Number_Range.MIN_BID.getValue(),
                        BidRule_Number_Range.MAX_BID.getValue(),
                        BidRule_Number_Range.PASS.getValue())
        );

        for (int i = 0; i < MAX_NO_PLAYERS; i++) {
            gamePlayers.get(i).bidHand();
        }
    }

    /*
        Declares the bid winner after all bids are accepted, and then declares the winner
    */
    public BidPlayer DetermineBidWinner() {
        BidPlayer bidWinner = null;

        if (gamePlayers.stream().anyMatch(bp -> bp.isAwardedTheBid()))
            bidWinner = gamePlayers.stream().filter(bp -> bp.isAwardedTheBid()).findFirst().get();

        return bidWinner;
    }

    //endregion


    @Override
    public UUID StartingNewGame() {

        Utils.log(getName(), "*** Starting New Game");
        return this.id;
    }

    /*
    Given a player's bid, validates their entered bid
    */
    @Override
    public boolean ValidatePlayersBid(BidPlayer bidPlayer) throws InterruptedException {
        boolean validBid;

        if ((bidPlayer.getBidHand_Books() < BidRule_Number_Range.MIN_BID.getValue()))
            throw new IllegalStateException("Bid less than minimal allowed!");
        if ((bidPlayer.getBidHand_Books() > BidRule_Number_Range.MAX_BID.getValue()))
            throw new IllegalStateException("The Bid is greater than maximum allowed");
        if ((bidPlayer.getBidDirection() != BidRule_Direction.NoTrump)
                && (bidPlayer.getBidDirection() != BidRule_Direction.Downtown)
                && (bidPlayer.getBidDirection() != BidRule_Direction.Uptown ))
            throw new IllegalStateException("The Bid direction must be DownTown, UpTown, or No Trump only!");

        // this player is assumed to have won the bid
        validBid = true;

        if (bidPlayer.getBidHand_Books() > GAME_BOOKS) {

        } else if (bidPlayer.getBidHand_Books() == GAME_BOOKS &&
                bidPlayer.getBidDirection().ordinal() > GAME_DIRECTION.ordinal()) {
        } else {
            // but, this player actually didn't win the bid
            validBid = false;
            gameEvents.ValidBid_BidNotExceedLeader(bidPlayer);
        }

        AwardPlayerTheBid(bidPlayer, validBid);
        return validBid;
    }

    @Override
    public void ResetAllOtherBidAwards(BidPlayer bidPlayer) {
        for (BidPlayer player : gamePlayers.stream()
                .filter(bp -> bp.isAwardedTheBid())
                .collect(Collectors.toList())) {
            player.setAwardedTheBid(false);
        }
    }

    @Override
    public void ValidBid_BidNotExceedLeader(BidPlayer bidPlayer) throws InterruptedException {
        Utils.Beep();
        StringBuilder sb = new StringBuilder();
        sb.append("\nYou're bid must exceed %1s's bid.  Or pass \"0\" your bid. \n\n " );
        sb.append(bidPlayer.getHand().GetCardsString());
        System.out.println(String.format(sb.toString(),
                bidWinner.toString() ));
    }

    @Override
    public int GetTeamScore(BidPlayer bidPlayer) {
        int finalScore = 0;
        for (BidPlayer bp : gamePlayers.stream().filter(fbp -> fbp.getTeamId() == bidPlayer.getTeamId())
                .collect(Collectors.toList())) {
            finalScore += bp.getBidsTaken();
        }
        return finalScore;
    }


    @Override
    public boolean PlaySelectedCard(CardPlay cardPlay) throws InterruptedException {
        boolean validPlay = false;

        if (cardPlay.player.getPlayOrder() == 1) {
            leadSuit = cardPlay.card.getCardSuit();
        }
        if (cardPlay.card.getCardSuit().equals(leadSuit))
            validPlay = true;
        else if (!cardPlay.player.getHand().HasSuit(leadSuit)) {
            if (GAME_SUIT.equals(cardPlay.card.getCardSuit()))
                gameEvents.PlayerPlaysTrump(cardPlay);
            else
                gameEvents.PlayerThrewOffSuit(cardPlay, leadSuit);
            validPlay = true;
        } else {
            // have the suit, but misplayed
            validPlay = gameEvents.PlayerHasRenege(cardPlay, leadSuit);
        }

        if (validPlay) {
            cardPlay.card.SetAvailable(false);
            cardPlay.player.getHand().remove(cardPlay.card);
            tableHand.add(cardPlay);
            cardPlay.player.SetPlayerHasPlayed(true);
            System.out.println(String.format("\t%1s played  %2s",
                    cardPlay.player.getPlayerName(),
                    cardPlay.card.toStringBef()));

        }
        return validPlay;
    }

    /*
        Sets the order in which the players will play the game:
     */
    public void SetGamePlayerPlayOrder(BidPlayer roundWinner) {
        System.out.println("\n*** Setting Player's playing order");
        System.out.println("\t***[ " + roundWinner.getPlayerName() + " ]***");
        gamePlayers.parallelStream().forEach(x -> x.setPlayOrder(5));

        int indexer = 0;

        Collections.sort(gamePlayers, new ComparePlayerTo(SortBy.PlayerIndex));
        for (BidPlayer bp : gamePlayers.stream()
                .filter(gp -> gp.getIndex() >= roundWinner.getIndex())
                .collect(Collectors.toList())) {
            bp.setPlayOrder(++indexer);
        }

        for (BidPlayer bp : gamePlayers.stream()
                .filter(gp -> gp.getIndex() < roundWinner.getIndex())
                .collect(Collectors.toList())) {
            bp.setPlayOrder(++indexer);
        }
        PlayerOrderSet = true;
        Collections.sort(gamePlayers, new ComparePlayerTo(SortBy.PlayerOrder));
        ShowPlayOrder();
    }

    private void ShowPlayOrder() {
        Collections.sort(gamePlayers, new ComparePlayerTo(SortBy.PlayerOrder));
        for (BidPlayer bp : gamePlayers) {
            System.out.println(bp.toString());
        }
    }

    /*
            Game Play: cycles through all the players. Players discards cards until game completes
    */
    @Override
    public boolean PlayerHasRenege(CardPlay cardPlayed, CardSuit leadSuit) throws InterruptedException {
        StringBuilder sb = new StringBuilder();

        Utils.Beep(2);
        // insert the card played back into the player's hand

        cardPlayed.player.getHand().AddCard(cardPlayed.card);
        sb.append("\n***** Listen here hommie, you can't renege!  Play a "
                + leadSuit.name() + " *****");
        System.out.println(sb.toString() + "\n");

        // player has not played a valid card
        return false;
    }

    @Override
    public boolean PlayerPlaysTrump(CardPlay cardPlayed) {
        Assets.PlayCuttingCard();
        cardPlayed.player.PlayedTrumpCard(cardPlayed.card);
        cardPlayed.card.setTrumpCard(true);
        return true;
    }


    @Override
    public BidPlayer JudgeTable(int playRound) {

        //final CardSuit ls = leadSuit;

        System.out.println("\nJudging the cards on the table\n");
        gamePlayers.stream().forEach(bidPlayer -> bidPlayer.SetHandWinner(false));

        System.out.println(String.format("\t\t\t\t\t\t\t\t-=< %2s %1s >=-"
                , GAME_SUIT != null ? GAME_SUIT.toString() : ""
                ,GAME_DIRECTION)
        );
        ShowTableCards(leadSuit);

        tableHand.Sort(SortBy.CardValue);
        List<CardPlay> filteredTableHand = null;
        boolean cutCardPlayed = false;
        cutCardPlayed = tableHand.stream().anyMatch(cp -> cp.card.IsTrumpCard());
        if (!cutCardPlayed)
            filteredTableHand = tableHand.stream()
                    .filter(cp -> !cp.card.isBidDud()).collect(Collectors.toList());
        else
            filteredTableHand = tableHand.stream()
                    .filter(cp -> cp.card.IsTrumpCard()).collect(Collectors.toList());

        int winner = 0;
        switch (GAME_DIRECTION) {
            case Downtown:
                winner = 0;
                break;
            case Uptown:
                winner = (filteredTableHand.size() - 1);
                break;
        }

        BidPlayer bidplayer = filteredTableHand.get(winner).player;
        bidplayer.SetHandWinner(true);
        tableHand.clear();
        return bidplayer;
    }

    @Override
    public boolean PlayerThrewOffSuit(CardPlay cardPlayed, CardSuit leadSuit) {
        Assets.PlayThrowOffCard();
        cardPlayed.card.setBidDud(true);
        cardPlayed.player.SetHandWinner(false);
        return true;
    }

    @Override
    public void WonThisHand(BidPlayer bidPlayer) {
        System.out.println("\n\n\n\tYo, " + bidPlayer + " won the hand! ");
        gamePlayers.stream().filter(gp -> gp.equals(bidPlayer)).findFirst().get().setPlayOrder(0);
        bidPlayer.AddToBidTaken();
        /*
        for (BidPlayer player: gamePlayers) {
            if (player.getId() == bidPlayer.getId()) {
                player.setPlayOrder(0);
                break;
            }
        }
        */
    }

    @Override
    public void TeamWonGameBid(int teamScore, BidPlayer winner) {
        System.out.println("Team "+ winner.getTeamId() +  ", you won!");
    }

    @Override
    public void TeamLostGameBid(int teamScore, BidPlayer winner) {
        System.out.println("You lost!");
    }

    public void ResetTeamTricksScore() {
        team1Score = 0;
        team2Score = 0;
    }

    public String ShowTeamTrickTakes() {
        String S1, S2, result;

        ResetTeamTricksScore();
        ;
        CalculateTeamsScores();

        S1 = "";
        S2 = "";

        StringBuilder sb = new StringBuilder();
        sb.append("    %1d         %2d");

        result = String.format(sb.toString(),
                team1Score,
                team2Score);
        return result;
    }


    public String ShowTeamScore() {
        String S1, S2, result;

        team1Score = 0;
        team2Score = 0;

        BidPlayer wbp = gamePlayers.stream()
                .filter(gp -> gp.isAwardedTheBid()).findFirst().get();

        CalculateTeamsScores();

        S1 = "";
        S2 = "";

        if (wbp.getTeamId() == 1)
            S1 = "*";
        else S2 = "*";

        StringBuilder sb = new StringBuilder();
        sb.append("\n\t\t   ");
        sb.append(GetGameBid() + "\n");
        sb.append("\t\t\t\t" + S1 + "Team 1\t\t\t" + S2 + "Team 2\n");
        sb.append("\t\t--------------------------------------\n");
        sb.append("\t\tTake(s)\t  %1d\t\t\t\t  %2s\n");

        result = String.format(sb.toString(),
                team1Score,
                team2Score);
        return result;
    }

    private void ShowPlayersGreeting(BidPlayer bidPlayer, CardSuit leadSuit) {
        StringBuilder sb = new StringBuilder();
        sb.append("Player %1s");
        if (leadSuit != null)
            sb.append(" - [%2s]");

        sb.append("    " + GetGameBid());


        System.out.println(String.format(sb.toString(),
                bidPlayer.getIndex(),
                leadSuit != null ? "Suit to follow: " + leadSuit.toString() : ""
        ));
    }

    private void ShowTableCards(CardSuit leadsuit) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\t\t %1s((", (leadsuit != null ? leadsuit : "")));
        int i = 0;
        for (CardPlay cp : tableHand) {
            if (i > 0) sb.append(", ");
            sb.append(String.format(" %1s: %2s",
                    cp.player.getPlayerName(),
                    cp.card.toStringBef()));
            i++;
        }
        sb.append(" ))");
        System.out.print(sb.toString());
    }

    public boolean isBgMusic() {
        return bgMusic;
    }

    public void setBgMusic(boolean bgMusic) {
        this.bgMusic = bgMusic;
    }

    public int getMinimalBid() {
        return minimalBid;
    }

    public void setMinimalBid(int minimalBid) {
        this.minimalBid = minimalBid;
    }

    public int getNoCardsInKitty() {
        return noCardsInKitty;
    }

    public void setNoCardsInKitty(int noCardsInKitty) {
        this.noCardsInKitty = noCardsInKitty;
    }

    public int getNoHandsToWin() {
        return noHandsToWin;
    }

    public void setNoHandsToWin(int noHandsToWin) {
        this.noHandsToWin = noHandsToWin;
    }

    public boolean isNoTrumpExchangeJokers() {
        return noTrumpExchangeJokers;
    }

    public void setNoTrumpExchangeJokers(boolean noTrumpExchangeJokers) {
        this.noTrumpExchangeJokers = noTrumpExchangeJokers;
    }

    public boolean isSoundFX() {
        return soundFX;
    }

    public void setSoundFX(boolean soundFX) {
        this.soundFX = soundFX;
    }

    @Override
    public String GetGameBid() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(String.format("%1s %2s\n",
                    Integer.toString(GAME_BOOKS),
                    GAME_DIRECTION.name())
            );
        } catch (Exception ex) {

        }
        return sb.toString();
    }

    @Override
    public void DeckCreated() {
        System.out.println("\n*** Deck created!");
    }

    @Override
    public void DeckShuffled() {
        System.out.println("\n*** Deck Shuffled!");
    }

    @Override
    public void JokersRemoved() {
        System.out.println("\n*** Jokers Removed!");
    }

    @Override
    public void DeckShuffling() {
        Assets.PlayDeckShuffling();
    }

    @Override
    public void KittyInitialized() {
        System.out.println("\n*** Kitty Initialized");
    }

    @Override
    public void PlayersInitialized() {
        System.out.println("\n*** Players Initialized");
    }

    private static void SetTrumpSuit(BidPlayer bidWinner) {
        bidWinner.getHand().ShowCards();
        System.out.println(String.format("\nOk %1s, select the game suit: 1=%2s, 2=%3s, 3=%4s, or 4=%5s ",
                bidWinner.getPlayerName(),
                CardSuit.Heart.toString().trim(),
                CardSuit.Diamond.toString().trim(),
                CardSuit.Spade.toString().trim(),
                CardSuit.Club.toString().trim()));

        boolean gameSuitSet = false;
        do {
            Scanner sc = new Scanner(System.in);
            int suitId = sc.nextInt();
            GAME_SUIT = CardSuit.fromValue(suitId);
            switch (GAME_SUIT) {
                case Heart:
                case Diamond:
                case Spade:
                case Club:
                    gameSuitSet = true;
                    break;
            }
            System.out.println();
        } while (!gameSuitSet);
    }

    @Override
    public void SetGameSuit(CardSuit gameSuit) {
        GAME_SUIT = gameSuit;
        System.out.println("\t*** Game Suit: " + gameSuit.toString());
        if (GAME_DIRECTION != BidRule_Direction.NoTrump) {
            SetJokerAndAcesSuit(gameSuit, GAME_DIRECTION);
        }
        System.out.println(GetGameBid());
    }

    private void SetJokerAndAcesSuit(CardSuit gameSuit, BidRule_Direction direction) {
        for (BidPlayer bp : gamePlayers) {
            bp.getHand().SetJokerSuit(gameSuit, direction);
        }
    }

    @Override
    public void AwardKittyToPlayer(BidPlayer bidWinner) {
        System.out.println();
        GetGameBid();

        System.out.println(String.format("\t*** %1s's awarded the KittyHand\n",bidWinner.getPlayerName()));
        KittyHand.ShowCards();
        bidWinner.getHand().SortCards(SortBy.DeckValue);
        System.out.println("\n\t*** Hand:");
        System.out.println(bidWinner.getHand().GetCardsString());

        //bidWinner.DiscardKittyTrades(KittyHand.size());

        //System.out.println(String.format("\n\t*** %1s's  new hand:  ", bidWinner.getPlayerName()));
    }

    @Override
    public boolean BidAwarded() {
        return GAME_BOOKS > 0 ? true : false;
    }

    private void AwardPlayerTheBid(BidPlayer p, boolean winnerResult) {
        if (winnerResult) {
            GAME_BOOKS = p.getBidHand_Books();
            GAME_DIRECTION = p.getBidDirection();
            GAME_SUIT = p.getBidSuit();
            ResetAllOtherBidAwards(p);
            bidWinner = p;
        }
        p.setAwardedTheBid(winnerResult);

    }

    private static void setGameDirection(BidPlayer bidWinner) {
        bidWinner.getHand().ShowCards();
        System.out.println(String.format("\nOk %1s, select the game direction: (D)owntown or (U)ptown?",
                bidWinner.getPlayerName()));

        boolean gameDirectionSet = false ;
        char choice;
        int directionId = 0;
        Scanner sc = new Scanner(System.in);
        do {
            choice = sc.next().charAt(0);
            switch (choice) {
                case 'd' :
                case 'D' :
                    directionId = 1;
                    gameDirectionSet = true;
                    break;
                case 'u' :
                case 'U' :
                    directionId = 2;
                    gameDirectionSet = true;
                    break;
                case 'q' :
                case 'Q' :
                    gameDirectionSet = true;
                    break;
            }
        } while (!gameDirectionSet);
        if (choice != 'q')
            if (!gameDirectionSet)  // game Direction wasn't set
                System.exit(0);

        GAME_DIRECTION = BidRule_Direction.fromValue(directionId);
        System.out.println();
    }

    @Override
    public void EndGame(ArrayList<BidPlayer> gamePlayers) {
        int teamScore;
        BidPlayer bidWinner = gamePlayers.stream()
                .filter(b -> b.isAwardedTheBid())
                .findFirst().get();

        teamScore = GetTeamScore(bidWinner);

        if (teamScore >= GAME_BOOKS + 6 )
            gameEvents.TeamWonGameBid(teamScore, bidWinner);
        else
            gameEvents.TeamLostGameBid(teamScore, bidWinner);
    }

    /*
     public void PlayTheGame() throws InterruptedException {

        int inputStr;
        Card cardSelected;
        CardSuit leadSuit = null;
        CardPlay cardPlayed;
        boolean playerHasPlayed;
        hasGameCompleted = true;
        gameTable = new GameTable();
        tableHand = new TableHand();

        System.out.println("Go\n\n");
        Scanner sc = new Scanner(System.in);

        int handRound = 1;
        boolean willLose = false;
        do {
            tableHand.clear();
            // play all cards
            for (int playerPlayCount = 0; playerPlayCount < MAX_PLAYER_HANDSIZE; playerPlayCount++) {
                System.out.println(ShowTeamScore());
                System.out.println("\nRound " + (playerPlayCount + 1));
                cutCardPlayed = false;
                for (BidPlayer currentPlayer : gamePlayers) {
                    currentPlayer.getHand().SortCards(SortBy.DeckValue);
                    playerHasPlayed = false;

                    do {
                        ShowPlayersGreeting(currentPlayer, leadSuit);
                        cardPlayed = null;
                        if (currentPlayer.getHand().getSize() > 0) {
                            System.out.println();
                            currentPlayer.getHand().ShowCards(SortBy.DeckValue);
                        }

                        if (handRound > 0)
                            ShowTableCards(leadSuit);
                        // 1.)   Pick a card by index value
                        System.out.print("\n\tselect card index:>   ");

                        if (currentPlayer.isHuman())
                            inputStr = sc.nextInt();
                        else
                            inputStr = currentPlayer.AutoPlayCard(leadSuit, handRound);

                        // force the selected card to be w/ in the hand range for selection
                        if (inputStr > currentPlayer.getHand().size() || inputStr < 0) continue;
                        if (inputStr <= MAX_PLAYER_HANDSIZE - handRound) {
                            cardSelected = currentPlayer.PlayCard(inputStr);
                            cardPlayed = new CardPlay(currentPlayer, cardSelected);
                        } else
                            continue;

                        CardSuit cardPlayedSuit = cardPlayed.card.getCardSuit();

                        // 2.)   Check to see if the selected card is playable
                        if (currentPlayer.isHandWinner()) {
                            leadSuit = cardPlayedSuit;
                            playerHasPlayed = gameEvents.PlayerPlays(cardPlayed, leadSuit);
                        } else if (cardPlayedSuit == leadSuit ) {
                            // 3.)   Place the selected/targeted card on the table. - following suit
                            playerHasPlayed = gameEvents.PlayerPlays(cardPlayed, leadSuit);
                        } else {
                            // 4.) Not - following suit
                            if (cardPlayed.player.HasSuit(leadSuit)){
                                // Player has renege, don't allow
                                playerHasPlayed = gameEvents.PlayerHasRenege(cardPlayed, leadSuit);
                            } else {
                                // Player is cutting
                                if (cardPlayed.card.getCardSuit() == GAME_SUIT) {
                                    cutCardPlayed = gameEvents.PlayerPlaysTrump(cardPlayed);
                                } else {
                                    // Player is simply throwing off
                                    gameEvents.PlayerThrewOffSuit(cardPlayed,leadSuit);
                                }
                                playerHasPlayed = gameEvents.PlayerPlays(cardPlayed, leadSuit);
                            }
                        }
                    } while (!playerHasPlayed);  // keep selecting a card until valid card selection.
                    if (playerHasPlayed & cardPlayed != null)
                        CardSelectedAndPlayed(cardPlayed);

                    System.out.println("");
                }  // next player takes a turn

                BidPlayer tableWinner = gameEvents.JudgeTable(playerPlayCount, leadSuit);
                tableWinner.AddToBidTaken();

                CalculateTeamsScores();

                willLose = WillBidWinnerActuallyLose();
                if (willLose) {
                    hasGameCompleted = ThrowInCardsForNextGame(bidWinner);
                    if (hasGameCompleted)
                        break;
                } else {
                    // player can win
                    hasGameCompleted = BidWinnerHasMadeBidButNoBoston(bidWinner);
                }

                setGamePlayerPlayOrder(tableWinner);
                leadSuit = null;
                handRound++;
            } // play the next card round

            System.out.println(ShowTeamScore());
            if (!hasGameCompleted)
                hasGameCompleted = handRound > MAX_PLAYER_HANDSIZE;
        } while (!hasGameCompleted);

        gameEvents.EndGame(gamePlayers);
        System.out.println("Done!");
    }
*/

    private boolean BidWinnerHasMadeBidButNoBoston(BidPlayer bidWinner) {
        boolean result = false;
        int otherTeamScore =  (bidWinner.getTeamId() == 1 ? team2Score : team1Score);
        if (otherTeamScore > 0 & GetTeamScore(bidWinner) >= 6 + GAME_BOOKS  ) {
            Scanner sc  = new Scanner(System.in);
            char choice = sc.next(".").charAt(0);
            System.out.println("\t\t ((( You've won this game, next hand? <y/N>");
            switch (choice) {
                case 'Y':
                case 'y':
                    result = true;
                    break;
                default:
                    break;

            }
        }
        return  result;
    }

    public boolean WillBidWinnerActuallyLose() {
        boolean result =  false;

        // [4] : 10 -> 4
        // [5] : 11 -> 3
        // [6] : 12 -> 2
        // [7] : 13 -> 1

        if (bidWinner.getTeamId() == 1)
            result = team2Score >= (8 - GAME_BOOKS);
        else
            result = team1Score >= (8 - GAME_BOOKS);

        return result;
    }

    // Give the bid player an opportunity to throw in his cards, because they will be game set
    private boolean ThrowInCardsForNextGame(BidPlayer bidWinner) throws InterruptedException {
        boolean result = false;

        System.out.println("You'll lose, Next hand! <Y/n>");
        Scanner sc = new Scanner(System.in);

        char answer = sc.next().charAt(0);
        switch (answer) {
            case 'Y':
            case 'y':
                bidWinner.ThrowInCards();
                result = true;
                break;
        }
        return result;
    }

    public static void RunQuitGame() {
        RunQuitGame(0);
    }

    public static void RunQuitGame(int exitCode) {
        System.exit(exitCode);
    }


    @Override
    public void CardSelectedAndPlayed(CardPlay cardPlay) {
        cardPlay.card.CardSelected(cardPlay);
    }

    @Override
    public void CardUnSelected(CardPlay cardPlay) {
        cardPlay.card.CardUnSelected(cardPlay);
    }

    // Tallies both team's final score
    public void CalculateTeamsScores() {
        team1Score = GetTeamScore(gamePlayers.get(0));
        team2Score = GetTeamScore(gamePlayers.get(1));
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    @Override
    public void PlayerHasBidded(BidPlayer biddingPlayer) {
        biddingPlayer.setPlayerHasBidded(true);
    }

    @Override
    public void PlayerHasPassed(BidPlayer biddingPlayer) {
        biddingPlayer.setPlayerHasBidded(true);
    }

    public CardSuit getLeadSuit() {
        return leadSuit;
    }

    public void AllPlayersPlayedReset() {
        gamePlayers.stream().forEach(p -> p.SetPlayerHasPlayed(false));
    }

}
