package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
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


    protected void ShowPlayersName(Batch batch) {
        String playerName = "";
        int playersIndex;
        float X=0,Y=0;

        for (int i=0;i< bidWhistGame.gamePlay.MAX_NO_PLAYERS;i++ ) {
            playerName = bidWhistGame.gamePlay.gamePlayers.get(i).getPlayerName();
            playersIndex =  bidWhistGame.gamePlay.gamePlayers.get(i).getIndex();
            //bounds = new Rectangle(0,0, playerName.length(), Assets.PlayerNameFont.getXHeight());
            if (playersIndex % 2 == 0)  {
                Y = stage.getHeight() * .38f;
            }

            switch (playersIndex) {
                case 1:
                    X = stage.getWidth() / 2f;
                    Y = stage.getHeight() * .30f;
                    break;
                case 2:
                    X = 3f;
                    break;
                case 3:
                    X = stage.getWidth() / 2f;
                    Y = Math.abs(Gdx.graphics.getHeight() - 3f);
                    break;
                case 4:
                    X = stage.getWidth() - 107f;
                    break;
            }
            Assets.PlayerNameFont.draw(batch, playerName, X, Y);
        }
    }

    protected void ShowPlayersHand(Batch batch, BidPlayer bidWinner, float offSet) {
        float P1Width = stage.getWidth() / 8.5F;
        float P1Height = stage.getHeight() / 4.5F;

        int XPos = 0;
        int cardIndex = 0;
        if (biddingPlayer == null) return;

        for (Card c : bidWinner.getHand()) {
            if (c.IsAvailable()) {
                c.setGrpIndexName(++cardIndex);
                c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
                c.PlayingCard().setSize(P1Width, P1Height);
                c.PlayingCard().setUserObject(c);
                grpSouthPlayer.addActor(c.PlayingCard());
                XPos += (int) (stage.getWidth() * .055F);
            }
        }
        grpSouthPlayer.setPosition(stage.getWidth() / 8 + 10, 0);// stage.getHeight()  );
        grpSouthPlayer.draw(batch, 1F);
        stage.addActor(grpSouthPlayer);
    }

}
