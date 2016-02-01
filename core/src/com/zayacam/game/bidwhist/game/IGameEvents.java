package com.zayacam.game.bidwhist.game;

import com.zayacam.game.bidwhist.cards.*;

import java.util.ArrayList;
import java.util.UUID;

public interface IGameEvents {
    void KittyInitialized();
    void PlayersInitialized();
    UUID StartingNewGame();
    void SetGameSuit(CardSuit gameSuit);
    void AwardKittyToPlayer(BidPlayer bidWinner);
    String GetGameBid();
    void PlayerHasBidded(BidPlayer biddingPlayer);
    boolean PlayerPlays(CardPlay played, CardSuit leadSuit);
    BidPlayer JudgeTable(int gameRound, CardSuit leadSuit);
    boolean PlayerHasRenege(CardPlay cardPlayed, CardSuit leadSuit) throws InterruptedException;
    boolean PlayerThrewOffSuit(CardPlay cardPlay, CardSuit leadSuit);
    boolean PlayerPlaysTrump(CardPlay cardPlayed);

    void WonThisHand(BidPlayer bidPlayer);

    void BidAwarded();
    void EndGame(ArrayList<BidPlayer> gamePlayers);
    void TeamWonGameBid(int teamScore, BidPlayer winner);

    void TeamLostGameBid(int teamScore, BidPlayer winner);

    boolean ValidatePlayersBid(BidPlayer bidPlayer) throws InterruptedException;

    void ResetAllOtherBidAwards(BidPlayer bidPlayer);

    void ValidBid_BidNotExceedLeader(BidPlayer bidPlayer) throws InterruptedException;

    int GetTeamScore(BidPlayer bidPlayer);

    boolean PlayThisCard(Card c);

    void DeckShuffled();

    void DeckCreated();

    void PlayerHasPassed(BidPlayer biddingPlayer);
}
