package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.actors.BiddingScreenActor;

public class BiddingStage extends _BidWhistStage {

    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);

        currentScreen = new BiddingScreenActor(bidWhistGame, this);
        Assets.LoadBidScreen();

        this.addActor(currentScreen);
    }

    @Override
    public void act() {
        super.act();



    }
}
