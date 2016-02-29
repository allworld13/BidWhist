package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
        GamePlay.team1GameScore = GamePlay.team2GameScore = 0;

        CardSelectedAdded = false;
        if (GamePlay.GAME_DIRECTION == GamePlay.BidRule_Direction.NoTrump) {
            ScreenTitleLabel = "Select your direction";
            showTrump = false;
        }
        GamePlay.GAME_SUIT = null;
        biddingPlayer = bidWinner = bidWhistGame.gamePlay.bidWinner;

        grpTrumps = new Group();
        grpTrumps.setName("grpTrumps");
        if (!showTrump) {
            LoadDirection();
        } else {
            LoadTrumps();
        }
        grpTrumps.setBounds(0, 0, (getWidth() / 2) + 125f, TrumpHeight + 500);
        grpTrumps.setPosition(getWidth() / 2f - grpTrumps.getWidth() / 2f, 100f);
        this.addActor(grpTrumps);
    }
    //endregion

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

        if (btnGoPlay != null && btnGoPlay.isVisible())
            btnGoPlay.draw(batch, 1f);

        if (bidWhistGame.gamePlay.BidAwarded()) {
            DrawGameBidLegend();
        }
        DrawGameScore(batch);
        batch.end();
    }


    /*
        Gives the bid winner an opportunity to select a trump.
     */
    private void ShowPickTrumpSelection() {
        //System.out.println();
        if (grpTrumps != null)
            grpTrumps.draw(batch, 1f);
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
        bidWhistGame.gamePlay.AwardKittyToPlayer(bidWinner);
    }

    @Override
    public void KittyCardPlayed(_BidWhistStage stage, Card selectedCard) {
        noDiscards += selectedCard.IsRaised() ? 1 : -1;
        Utils.log(stageName, "\t**Card selected**  : " + selectedCard.IsRaised() + " : " + noDiscards);
    }

    @Override
    public void ReadyToDiscard(boolean b) {
        Utils.log(stageName, GamePlay.MAX_CARDS_TO_DISCARD + " selected, ready to discard!");
        if (btnGoPlay != null)
            btnGoPlay.setVisible(b);
    }

    private void LoadDirection() {
        Image j, i = new Image(new Texture(Gdx.files.internal("downArrow.png")));
        i.setBounds(0, 0, 75, 75);
        i.setSize(75, 75);
        i.setPosition(getWidth() * .18f, getHeight() * .48f);
        i.addListener(new TrumpClickListener());
        i.setUserObject(GamePlay.BidRule_Direction.Downtown);
        grpTrumps.addActor(i);

        j = new Image(new Texture(Gdx.files.internal("downArrow.png")));
        j.setBounds(0, 0, getWidth() * .04f, getHeight() * .75f);
        j.setSize(75, 75);
        j.setPosition(getWidth() * .44f, getHeight() * .61f);
        j.setRotation(180f);
        j.addListener(new TrumpClickListener());
        j.setUserObject(GamePlay.BidRule_Direction.Uptown);
        grpTrumps.addActor(j);
    }

    private void LoadTrumps() {
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
            imgTrump.setPosition(XPos, 200f);
            TrumpHeight = getHeight() * .20f;
            imgTrump.setSize(getWidth() * .13f, TrumpHeight);
            imgTrump.setName(((CardSuit) imgTrump.getUserObject()).name());
            counter++;
            XPos += 150;
        }
    }

    private class OkClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.log("Trump/Direction selected", "Ok");
            //if (showTrump) {
            ConfigureAndShowKitty();
            ShowKitty = true;
            ScreenTitleLabel = "Discard  6  Cards";
            grpTrumps.clear();
            grpTrumps.remove();
            //}
            grpTrumps = null;
            btnGoPlay.setText("Play");
            btnGoPlay.clearListeners();
            btnGoPlay.addListener(new PlayClickListener());
            btnGoPlay.setVisible(false);
        }
    }

    private class TrumpClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);

            btnGoPlay = new TextButton("Ok", Assets.Skins);
            btnGoPlay.setName("Ok");
            btnGoPlay.setSize(150, 46);
            btnGoPlay.setPosition(getWidth() / 2 - btnGoPlay.getWidth() / 2f, getHeight() * .38f);
            btnGoPlay.pad(13f, 0, 13f, 0);
            btnGoPlay.addListener(new OkClickListener());
            btnGoPlay.setVisible(false);
            TrumpSelectStage.this.addActor(btnGoPlay);


            switch (GamePlay.GAME_DIRECTION) {
                case NoTrump:
                    GamePlay.GAME_DIRECTION = (GamePlay.BidRule_Direction) event.getListenerActor().getUserObject();
                    GamePlay.GAME_SUIT = null;
                    break;
                default:
                    GamePlay.GAME_SUIT = (CardSuit) event.getListenerActor().getUserObject();
                    break;
            }
            btnGoPlay.setVisible(true);
        }
    }

    private class PlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            boolean f = GamePlay.GAME_SUIT == null;
            Utils.log(!f ? "\nTrump" : "\nDirection" + " Selected", "Ready to play, " + GamePlay.GAME_DIRECTION.toString());

            bidWhistGame.gamePlay.KittyHand.getCards().removeIf(c -> c.IsRaised());
            Utils.log("Kitty Remains", Integer.toString(bidWhistGame.gamePlay.KittyHand.getCards().size()));
            bidWinner.getHand().getCards().removeIf(c -> c.IsRaised());
            Utils.log("BidWinner Remains", Integer.toString(bidWinner.getHand().getCards().size()));
            bidWinner.getHand().getCards().addAll(bidWhistGame.gamePlay.KittyHand.getCards());
            Utils.log("BidWinner Remains Final", Integer.toString(bidWinner.getHand().getCards().size()));

            bidWinner.getHand().SortCards(SortBy.DeckValue);
            try {
                bidWhistGame.gamePlay.SetGameSuit(GamePlay.GAME_SUIT);
                bidWhistGame.ChangeScreenTo("GamePlayStage");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
