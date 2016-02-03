package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.InputProcessor;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public class BiddingScreenActor extends _BidActor implements InputProcessor {


    //region ctor

    BiddingScreenActor() {
        super();
    }

    public BiddingScreenActor(BidWhistGame bidWhistGame, _BidWhistStage stage) {
        this();
        this.bidWhistGame = bidWhistGame;
        this.stage = stage;

        if (stage != null)
            stage.setKeyboardFocus(this);

        Assets.PlayDeckShuffling();
    }

    //endregion

    //region IP Overrides

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
    //endregion

}