package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.CardSuit;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

public class TrumpSelectStage extends _BidWhistStage {
    TextButton btnGoPlay;
    BidPlayer biddingPlayer;
    Group grpTrumps;
    Image imgTrump = null;
    float TrumpHeight;

    public TrumpSelectStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Pick your trump";
        biddingPlayer = bidWinner = bidWhistGame.gamePlay.bidWinner;
        GamePlay.GAME_SUIT = null;

        btnGoPlay = new TextButton("Play", Assets.Skins);
        btnGoPlay.setName("Play");
        btnGoPlay.setSize(150, 46);
        btnGoPlay.setPosition(getWidth() / 2 - btnGoPlay.getWidth() / 2f, getHeight() * .38f);
        btnGoPlay.pad(13f, 0, 13f, 0);
        btnGoPlay.addListener(new PlayClickListener());
        btnGoPlay.setVisible(false);
        LoadTrumps();
        this.addActor(btnGoPlay);
    }

    private void LoadTrumps() {
        grpTrumps = new Group();
        grpTrumps.setName("grpTrumps");
        int XPos = 0, counter = 0;
        for (TextureRegion tr :
                Assets.cardSuits) {
            imgTrump = new Image(tr);
            imgTrump.addListener(new TrumpClickListener());
            switch (counter) {
                case 0:
                    imgTrump.setUserObject(CardSuit.Heart);
                    break;
                case 1:
                    imgTrump.setUserObject(CardSuit.Spade);
                    break;
                case 2:
                    imgTrump.setUserObject(CardSuit.Diamond);
                    break;
                case 3:
                    imgTrump.setUserObject(CardSuit.Club);
                    break;
            }
            grpTrumps.addActor(imgTrump);//.padRight(counter==0?0:30f);
            this.addActor(grpTrumps);
            imgTrump.setPosition(XPos, 200f);
            TrumpHeight = getHeight() * .20f;
            imgTrump.setSize(getWidth() * .17f, TrumpHeight);
            imgTrump.setName(((CardSuit) imgTrump.getUserObject()).name());
            counter++;
            XPos += 170;
        }

        grpTrumps.setBounds(0, 0, getWidth() * .195f * 4, TrumpHeight + 500);
        grpTrumps.setPosition(getWidth() / 2f - grpTrumps.getWidth() / 2f, 100f);
        //grpTrumps.setSize(getWidth()*.,300f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        ShowPlayersName(batch);
        ShowPickTrumpSelection();
        ShowPlayersHand(batch, bidWinner, 65f);
        if (btnGoPlay.isVisible())
            btnGoPlay.draw(batch, 1f);
        batch.end();
    }

    /*
        Gives the bid winner an opportunity to select a trump.
     */
    private void ShowPickTrumpSelection() {
        //System.out.println();
        grpTrumps.draw(batch, 1f);
    }

    private class PlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {

            Gdx.app.log("Trump Selected", "Ready to play");
        }
    }


    private class TrumpClickListener extends ClickListener {
        CardSuit suitSelected = null;

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            suitSelected = (CardSuit) event.getListenerActor().getUserObject();
            GamePlay.GAME_SUIT = suitSelected;
            btnGoPlay.setVisible(true);

            Gdx.app.log("Trump Selected", suitSelected.toString() + " - " + suitSelected.name());
        }
    }
}
