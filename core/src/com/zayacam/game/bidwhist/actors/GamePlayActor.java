package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public class GamePlayActor extends _BidActor implements InputProcessor {

    //region ctor

    GamePlayActor() {
        super();
    }

    public GamePlayActor(BidWhistGame bidWhistGame, _BidWhistStage stage) throws InterruptedException {
        this();
        this.bidWhistGame = bidWhistGame;
        this.stage = stage;
    }

    //endregion

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(Assets.text_background, 0,0, stage.getWidth(), stage.getHeight());
    }

    //region IP Overrides
    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
//endregion
}
