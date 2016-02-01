package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;

public abstract class _BidActor extends Actor implements InputProcessor  {
    //Thread thread ;

    protected BidWhistGame bidWhistGame;
    protected Stage stage;
    protected Actor hitActor;

    //region ctor

    _BidActor() {
        super();
        this.setTouchable(Touchable.enabled);
        Gdx.input.setInputProcessor(this);
    }

    _BidActor(BidWhistGame bidWhistGame, Stage stage) throws InterruptedException {
        this();
        this.stage = stage;
        this.bidWhistGame = bidWhistGame;
    }

    //endregion

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected void ShowPlayersName(Batch batch) {
        String playerName = "";
        int playersIndex;
        float X=0,Y=0;

        for (int i=0;i< bidWhistGame.gamePlay.MAX_NO_PLAYERS;i++ ) {
            playerName = bidWhistGame.gamePlay.gamePlayers.get(i).getPlayerName();
            playersIndex =  bidWhistGame.gamePlay.gamePlayers.get(i).getIndex();
            //bounds = new Rectangle(0,0, playerName.length(), Assets.PlayerNameFont.getXHeight());
            if (playersIndex % 2 == 0)  {
                Y = Gdx.graphics.getHeight() * .38f;
            }

            switch (playersIndex) {
                case 1:
                    X = Gdx.graphics.getWidth() /2f ;
                    Y = Gdx.graphics.getHeight() * .30f;
                    break;
                case 2:
                    X = 3f;
                    break;
                case 3:
                    X = Gdx.graphics.getWidth() / 2f;
                    Y = Math.abs(Gdx.graphics.getHeight() - 3f);
                    break;
                case 4:
                    X = Gdx.graphics.getWidth() - 107f;
                    break;
            }
            Assets.PlayerNameFont.draw(batch, playerName, X, Y);
        }
    }


}
