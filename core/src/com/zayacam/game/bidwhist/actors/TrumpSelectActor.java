package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public class TrumpSelectActor extends _BidActor implements InputProcessor {

    Group grpSouthPlayer, grpBidding;
    TextButton btnNumber;
    private GamePlay.BidRule_Direction bidDirection = null;

    //region ctor


    TrumpSelectActor() {
        super();
    }

    public TrumpSelectActor(BidWhistGame bidWhistGame, _BidWhistStage stage) {
        this();
        this.bidWhistGame = bidWhistGame;
        this.stage = stage;

        grpSouthPlayer = new Group();
        grpBidding = new Group();
        bidWinner = biddingPlayer = bidWhistGame.gamePlay.bidWinner;

        if (stage != null)
            stage.setKeyboardFocus(this);

    }

    //endregion

    @Override
    public void act(float delta) {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(Assets.text_background, 0, 0, stage.getWidth(), stage.getHeight());

        //ShowPlayersName(batch);
        ShowPlayersHand(batch, bidWinner, 65f);
    }


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