package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.cards.CardSuit;
import com.zayacam.game.bidwhist.cards.SortBy;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.game.IKittyEvents;

public class TrumpSelectStage extends _BidWhistStage implements IKittyEvents {
    TextButton btnGoPlay;
    BidPlayer biddingPlayer;
    Group grpTrumps;
    Image imgTrump = null;
    float TrumpHeight;
    boolean showTrump = true;
    private boolean CardsBaseLined;


    //region ctors
    public TrumpSelectStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Pick your trump";
        CardsBaseLined = false;
        noDiscards = 0;

        CardSelectedAdded = false;
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
        if (showTrump) {
            LoadTrumps();
        }
        else {
            LoadDirection();
        }
        this.addActor(btnGoPlay);
    }

    //endregion
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
            imgTrump.setSize(getWidth() * .13f, TrumpHeight);
            imgTrump.setName(((CardSuit) imgTrump.getUserObject()).name());
            counter++;
            XPos += 150;
        }

        grpTrumps.setBounds(0, 0, (getWidth() / 2) + 125f, TrumpHeight + 500);
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
        if (!CardsBaseLined)
            BaseLineAllCards();
        CardsBaseLined = true;

        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        ShowPlayersName(batch);
        ShowPickTrumpSelection();
        DrawPlayerHand(batch, bidWinner);
        CardSelectedAdded = true;
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
                ConfigureAndShowKitty();
                ShowKitty = true;
                ScreenTitleLabel = "Discard  6  Cards";
                grpTrumps.clear();
                grpTrumps.remove();
            }
            grpTrumps = null;
            btnGoPlay.setText("Play");
            btnGoPlay.clearListeners();
            btnGoPlay.addListener(new PlayClickListener());
            btnGoPlay.setVisible(false);
        }
    }


    void ConfigureAndShowKitty() {
        float baseLine = Assets.P1YBaseLine;//bidWhistGame.gamePlay.KittyHand.get(0).PlayingCard().getY();
        grpKitty = new Group();
        grpKitty.setVisible(true);
        float XPos = 0;
        bidWhistGame.gamePlay.KittyHand.SortCards(SortBy.DeckValue);
        for (Card c : bidWhistGame.gamePlay.KittyHand) {
            c.PlayingCard().setPosition(XPos, Assets.P1YBaseLine);
            c.SetIsRaised(false);
            c.SetReadyToPlay(true);
            c.PlayingCard().setSize(Assets.FirstPlayerCardWidth, Assets.FirstPlayerCardHeight);
            c.PlayingCard().setUserObject(c);
            c.PlayingCard().addListener(new CardClickListener(baseLine));
            grpKitty.addActor(c.PlayingCard());
            XPos += (int) (this.getWidth() * .048F);
        }
        grpKitty.setBounds(this.getWidth() / 2 - XPos / 2,
                this.getHeight() / 2,
                XPos, Assets.FirstPlayerCardHeight);

        grpKitty.setPosition((this.getWidth() / 2 - XPos / 2) - 70, this.getHeight() * 0.5f);
        this.addActor(grpKitty);
        grpKitty.setVisible(true);
        grpKitty.setTouchable(Touchable.childrenOnly);
    }

    private class PlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Utils.log("Trump Selected", "Ready to play");

            bidWhistGame.gamePlay.KittyHand.getCards().removeIf(c -> c.IsRaised());
            Utils.log("Kitty Remains", Integer.toString(bidWhistGame.gamePlay.KittyHand.getCards().size()));
            bidWinner.getHand().getCards().removeIf(c -> c.IsRaised());
            Utils.log("BidWinner Remains", Integer.toString(bidWinner.getHand().getCards().size()));
            bidWinner.getHand().getCards().addAll(bidWhistGame.gamePlay.KittyHand.getCards());
            Utils.log("BidWinner Remains Final", Integer.toString(bidWinner.getHand().getCards().size()));

            bidWinner.getHand().SortCards(SortBy.DeckValue);
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

    @Override
    public void KittyCardPlayed(_BidWhistStage stage, Card selectedCard) {
        noDiscards += selectedCard.IsRaised() ? 1 : -1;
        Utils.log(stageName, "\t**Card selected**  : " + selectedCard.IsRaised() + " : " + noDiscards);
    }

    @Override
    public void ReadyToDiscard(boolean b) {
        Utils.log(stageName, GamePlay.MAX_CARDS_TO_DISCARD + " selected, ready to discard!");
        btnGoPlay.setVisible(b);
    }

}
