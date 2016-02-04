package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.game.BidPlayer;

public class TrumpSelectStage extends _BidWhistStage {

    BidPlayer biddingPlayer;
    Group grpSouthPlayer;
    TextButton btnGameButton;

    public TrumpSelectStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);

        ScreenTitleLabel = "Pick your trump";
        biddingPlayer = bidWinner = bidWhistGame.gamePlay.bidWinner;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);
        ShowPlayersName(batch);
        ShowPickTrumpSelection();
        ShowPlayersHand(batch, bidWinner, 65f);
        batch.end();
    }

    /*
        Gives the bid winner an opportunity to select a trump.
     */
    private void ShowPickTrumpSelection() {
        System.out.println();
    }
}
