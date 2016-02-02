package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.actors.BiddingScreenActor;
import com.zayacam.game.bidwhist.game.GamePlay;

public class BiddingStage extends _BidWhistStage {

    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        GamePlay.GAME_BOOKS = 0;
        currentScreen = new BiddingScreenActor(bidWhistGame, this);
        Assets.LoadBidScreen();

        this.addActor(currentScreen);
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.draw();
    }
}
