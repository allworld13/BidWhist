package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.actors.GamePlayActor;

public class GamePlayStage extends _BidWhistStage implements InputProcessor {

    public GamePlayStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) throws InterruptedException {
        super(bidWhistGame, sViewport);

        currentScreen = new GamePlayActor(bidWhistGame, this);
        Assets.LoadGamePlayScreen();

        this.addActor(currentScreen);
        this.setKeyboardFocus(currentScreen);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
