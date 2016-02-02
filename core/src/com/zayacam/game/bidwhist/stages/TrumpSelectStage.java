package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;

public class TrumpSelectStage extends _BidWhistStage {

    BidPlayer biddingPlayer;

    Group grpSouthPlayer;
    TextButton btnGameButton;

    public TrumpSelectStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);

        Assets.LoadTrumpSelectScreen();
        grpSouthPlayer = new Group();
        biddingPlayer = bidWhistGame.gamePlay.bidWinner;
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        this.getBatch().begin();
        this.getBatch().draw(Assets.text_background, 0, 0, this.getWidth(), this.getHeight());
        ShowPlayersHand(this.getBatch(), biddingPlayer, 65f);
        this.getBatch().end();
    }


    protected void ShowPlayersHand(Batch batch, BidPlayer bidWinner, float offSet) {
        float P1Width = this.getWidth() / 8.5F;
        float P1Height = this.getHeight() / 4.5F;

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
                XPos += (int) (this.getWidth() * .055F);
            }
        }
        grpSouthPlayer.setPosition(this.getWidth() / 8 + 10, 0);// stage.getHeight()  );
        grpSouthPlayer.draw(batch, 1F);
        this.addActor(grpSouthPlayer);
    }

}
