package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.stream.Collectors;

public class BiddingStage extends _BidWhistStage {

    //region ctors
    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Place your bid";

        GamePlay.GAME_BOOKS = 0;
        minBid = bidWhistGame.gamePlay.getMinimalBid();
        Assets.PlayDeckShuffling();

    }
    //endregion

    @Override
    public void act(float delta) {
        Assets.ClearScreen();
        finishedBidding = bidWhistGame.gamePlay.gamePlayers
                .stream().allMatch(p -> p.PlayerHasBidded());

        if (!finishedBidding) {
            for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers
                    .stream().filter(p -> !p.PlayerHasBidded()).collect(Collectors.toList())) {
                bidWhistGame.PlayersTurnToBid(bp);
                biddingPlayer = bp;
                finishedBidding = false;
                break;
            }
        }
        if (finishedBidding) {
            bidWinner = bidWhistGame.DetermineBidWinner();
            if (bidWinner == null) {
                biddingPlayer = bidWhistGame.gamePlay.gamePlayers.get(bidWhistGame.gamePlay.MAX_NO_PLAYERS - 1);
                biddingPlayer.setPlayerHasBidded(false);
                bidWhistGame.YouMustBid(biddingPlayer);
                finishedBidding = false;
            } else {
                biddingPlayer = bidWinner;
                try {
                    bidWhistGame.ChangeScreenTo("TrumpSelectStage");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        Assets.ClearScreen();

        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        if (!finishedBidding) {
            if (bidWinner == null) {
                LoadBidNumberButtons();
            }
            DrawPlayerHand(batch, biddingPlayer);
            ShowPlayersName(batch);
        }
        if (bidWhistGame.gamePlay.BidAwarded()) {
            DrawGameBidLegend();
        }
        DrawGameScore(batch);
        batch.end();
    }

    @Override
    public boolean keyDown(int keyCode) {
        super.keyDown(keyCode);

        if (biddingPlayer.isHuman()) {
            switch (keyCode) {
                case Input.Keys.NUM_4:
                case Input.Keys.NUMPAD_4:
                    biddingBooks = 4;
                    biddingPlayer.setBidHand_Books(4);
                    break;
                case Input.Keys.NUM_5:
                case Input.Keys.NUMPAD_5:
                    biddingBooks = 5;
                    biddingPlayer.setBidHand_Books(5);
                    break;
                case Input.Keys.NUM_6:
                case Input.Keys.NUMPAD_6:
                    biddingBooks = 6;
                    biddingPlayer.setBidHand_Books(6);
                    break;
                case Input.Keys.NUM_7:
                case Input.Keys.NUMPAD_7:
                    biddingBooks = 7;
                    biddingPlayer.setBidHand_Books(7);
                    break;
                case Input.Keys.D:
                    bidDirection = GamePlay.BidRule_Direction.Downtown;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.Downtown);
                    break;
                case Input.Keys.U:
                    bidDirection = GamePlay.BidRule_Direction.Uptown;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.Uptown);
                    break;
                case Input.Keys.N:
                    bidDirection = GamePlay.BidRule_Direction.NoTrump;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.NoTrump);
                    break;
                case Input.Keys.P:
                    btnPass.getClickListener().clicked(null, 0, 0);
                    break;
            }

        }
        return true;
    }
}
