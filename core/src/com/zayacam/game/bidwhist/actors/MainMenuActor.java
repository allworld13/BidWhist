package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public class MainMenuActor extends _BidActor implements InputProcessor {

    //region ctor
    private MainMenuActor() {
        super();
    }

    public MainMenuActor(BidWhistGame bidWhistGame, _BidWhistStage stage) {
        this();
        this.bidWhistGame = bidWhistGame;
        this.stage = stage;
    }
    //endregion

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.draw(Assets.text_background, 0,0, stage.getWidth(), stage.getHeight());
    }

    @Override
    public boolean keyDown(int keyCode) {
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
}