package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.game.BidPlayer;

public abstract class _BidActor extends Actor implements InputProcessor  {
    //Thread thread ;

    protected BidPlayer bidWinner, biddingPlayer = null;
    protected Group grpSouthPlayer;
    protected BidWhistGame bidWhistGame;
    protected Stage stage;
    protected Actor hitActor;

    //region ctor

    _BidActor() {
        super();
        this.setTouchable(Touchable.enabled);
        Gdx.input.setInputProcessor(this);
        grpSouthPlayer = new Group();
    }

    _BidActor(BidWhistGame bidWhistGame, Stage stage) throws InterruptedException {
        this();
        this.stage = stage;
        this.bidWhistGame = bidWhistGame;
        bidWinner = bidWhistGame.gamePlay.bidWinner;
    }

    //endregion

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }




}
