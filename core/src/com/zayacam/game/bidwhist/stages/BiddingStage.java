package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
                    if (bidWinner.getBidDirection() != GamePlay.BidRule_Direction.NoTrump) {
                        bidWhistGame.ChangeScreenTo("DetermineTrumpStage");
                    } else {
                        bidWhistGame.ChangeScreenTo("GamePlayStage");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        if (!finishedBidding) {
            if (bidWinner == null) {
                LoadBidNumberButtons();
            }
        }
        ShowPlayersHand(batch, biddingPlayer, 65f);
        ShowPlayersName(batch);
        batch.end();
    }

}
