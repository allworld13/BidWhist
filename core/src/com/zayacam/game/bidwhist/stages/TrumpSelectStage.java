package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
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
    boolean showTrump = true;

    public TrumpSelectStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Pick your trump";
        if (GamePlay.GAME_DIRECTION == GamePlay.BidRule_Direction.NoTrump) {
            ScreenTitleLabel = "Select your direction";
            showTrump = false;
        }
        GamePlay.GAME_SUIT = null;
        biddingPlayer = bidWinner = bidWhistGame.gamePlay.bidWinner;

        btnGoPlay = new TextButton("Ok", Assets.Skins);
        btnGoPlay.setName("Ok");
        btnGoPlay.setSize(150, 46);
        btnGoPlay.setPosition(getWidth() / 2 - btnGoPlay.getWidth() / 2f, getHeight() * .38f);
        btnGoPlay.pad(13f, 0, 13f, 0);
        btnGoPlay.addListener(new OkClickListener());
        btnGoPlay.setVisible(false);
        if (showTrump)
            LoadTrumps();
        else {
            LoadDirection();
        }

        this.addActor(btnGoPlay);
    }

    private void LoadDirection() {
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
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw() {
        super.draw();
        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        ShowPlayersName(batch);
        ShowPickTrumpSelection();
        ShowPlayersHand(batch, bidWinner, 65f);
        if (ShowKitty)
            grpKitty.draw(batch, 1f);

        if (btnGoPlay.isVisible())
            btnGoPlay.draw(batch, 1f);
        batch.end();
    }

    /*
        Gives the bid winner an opportunity to select a trump.
     */
    private void ShowPickTrumpSelection() {
        //System.out.println();
        if (GamePlay.GAME_DIRECTION != GamePlay.BidRule_Direction.NoTrump) {
            if (grpTrumps != null)
                grpTrumps.draw(batch, 1f);
        }
        else
            grpBiddingNumbers.draw(batch, 1f);
    }

    private class OkClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.log("Trump/Direction selected", "Ok");
            if (showTrump) {
                ConfigureAndShowKitty(200);
                ShowKitty = true;
                ScreenTitleLabel = "";
            }
            grpTrumps = null;
            btnGoPlay.setText("Play");
            btnGoPlay.clearListeners();
            btnGoPlay.addListener(new PlayClickListener());
        }
    }

    private class PlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.log("Trump Selected", "Ready to play");

            try {
                bidWhistGame.ChangeScreenTo("GamePlayStage");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
