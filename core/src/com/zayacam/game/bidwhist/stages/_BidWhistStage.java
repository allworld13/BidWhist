package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

public abstract class _BidWhistStage extends Stage implements InputProcessor {

    public InputMultiplexer im;

    protected String stageName;
    protected String ScreenTitleLabel = "";
    protected GlyphLayout screenTitle;
    protected SpriteBatch batch;
    protected BidPlayer bidWinner, biddingPlayer = null;
    protected BidWhistGame bidWhistGame;
    protected Actor currentScreen;
    protected Vector2 touchCoord, touchedVector;
    protected Actor hitActor;
    protected Card selectedCard;
    protected boolean hasStartedPlaying, finishedBidding = false;
    protected GamePlay.BidRule_Direction bidDirection = null;
    protected boolean ShowKitty = false;

    Group grpKitty, grpSouthPlayer, grpTableHand, grpBiddingNumbers;
    Table tblBiddingNumbers;
    Button btnNumber;

    int biddingBooks = 0, minBid, XPos;

    //region ctor
    _BidWhistStage(ScreenViewport vPort) {
        super(vPort);
        batch = new SpriteBatch();
        stageName = Utils.GetStageName(this.toString());

        screenTitle = new GlyphLayout();

        Assets.FirstPlayerCardWidth = this.getWidth() * .20f;
        Assets.FirstPlayerCardHeight = this.getHeight() * .28f;

        if (stageName.contains("MainMenuStage"))
            Assets.LoadGameScreen(true);
        else
            Assets.LoadGameScreen();

        Assets.sprite_background.setRegion(Assets.text_background);
        setKeyboardFocus(currentScreen);

        grpSouthPlayer = new Group();
        grpBiddingNumbers = new Group();
    }

    public _BidWhistStage(BidWhistGame bidWhistGame, ScreenViewport vPort) {
        this(vPort);
        this.bidWhistGame = bidWhistGame ;
        minBid = bidWhistGame.gamePlay.getMinimalBid();
    }
    //endregion

    @Override
    public void draw() {
        super.draw();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public String getStageName() {
        return stageName;
    }

    protected void DrawTitle(SpriteBatch batch) {
        screenTitle.setText(Assets.ScreenTitleFont, ScreenTitleLabel);

        Assets.ScreenTitleFont.draw(batch, screenTitle,
                getWidth() / 2 - screenTitle.width / 2,
                getHeight() * Assets.ScreenTitleYPos);
    }

    protected void DrawPlayerHand(SpriteBatch batch, BidPlayer bidPlayer) {
        float EWMargin = .2F;

        int cardIndex = 0;

        //region South Player
        //South Player
        XPos = 0;
        grpSouthPlayer.setPosition(this.getWidth() / 10f, 0);
        for (Card c : bidPlayer.getHand()) {
            if (c.IsAvailable()) {
                c.setGrpIndexName(++cardIndex);
                c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
                c.PlayingCard().setSize(Assets.FirstPlayerCardWidth, Assets.FirstPlayerCardHeight);
                c.PlayingCard().setUserObject(c);
                grpSouthPlayer.addActor(c.PlayingCard());
                XPos += (int) (this.getWidth() * .055F);
            }
        }
        grpSouthPlayer.draw(batch, 1F);
        this.addActor(grpSouthPlayer);

        if (!hasStartedPlaying) {
            System.out.println(String.format("%1f %2f  %3f",
                    this.getWidth(),
                    grpSouthPlayer.getWidth(),
                    (this.getWidth() / 2 - grpSouthPlayer.getWidth() / 2)));

            System.out.println(bidPlayer.getHand().GetCardsString());
        }
        //endregion

        for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers) {
            switch (bp.getIndex()) {
                //region West Player
                case 2:
                    Assets.gfxDeck.get(Assets.CardBack).getDrawable()
                            .draw(batch,
                                    (20 * EWMargin),
                                    this.getHeight() / Assets.PlayerCard_Y_Ratio,
                                    this.getWidth() / Assets.PlayerCardWidthRatio,
                                    this.getHeight() * Assets.PlayerCardHeightRatio);
                    break;

                //endregion

                //region East Player
                case 4:
                    Assets.gfxDeck.get(Assets.CardBack).getDrawable()
                            .draw(batch,
                                    this.getWidth() - (this.getWidth() * .120F),
                                    this.getHeight() / Assets.PlayerCard_Y_Ratio,
                                    this.getWidth() / Assets.PlayerCardWidthRatio,
                                    this.getHeight() * Assets.PlayerCardHeightRatio);
                    break;
                //endregion
            }

            XPos += 120;
            hasStartedPlaying = true;
        }
    }

    protected void ShowPlayersName(Batch batch) {
        String playerName = "";
        int playersIndex;
        float X = 0, Y = 0;
        BidPlayer bp;
        for (int i = 0; i < bidWhistGame.gamePlay.MAX_NO_PLAYERS; i++) {
            bp = bidWhistGame.gamePlay.gamePlayers.get(i);
            playerName = bp.getPlayerName();
            playersIndex = bp.getIndex();
            Assets.textBounds = new GlyphLayout();
            Assets.textBounds.setText(Assets.PlayerNameFont, playerName);

            if (playersIndex % 2 == 0) {
                Y = this.getHeight() * Assets.PlayerNameEastWest;
            }

            X = this.getWidth() / 2f - Assets.textBounds.width / 2;
            switch (playersIndex) {
                case 1:
                    Y = this.getHeight() * .355f;
                    break;
                case 2:
                    X = 3f;
                    break;
                case 3:
                    Y = Math.abs(Gdx.graphics.getHeight() - 3f);
                    break;
                case 4:
                    X = this.getWidth() - 107f;
                    break;
            }
            Assets.PlayerNameFont.setColor(Color.WHITE);
            if (biddingPlayer == null && bidWinner != null && bidWinner.getPlayerName() == playerName)
                Assets.PlayerNameFont.setColor(Color.GOLD);
            else if (biddingPlayer == null) {
            } else if (biddingPlayer.getPlayerName() == playerName)
                Assets.PlayerNameFont.setColor(Color.GOLD);
            Assets.PlayerNameFont.draw(batch, playerName, X, Y);
        }
    }

    protected void LoadBidNumberButtons() {
        if (finishedBidding) {
            return;
        }
        Table tblNumbers, tblDirection, tblOutter;

        tblOutter = new Table();
        tblBiddingNumbers = new Table();
        tblBiddingNumbers.setName("tblBiddingNumbers");

        //region main table layout

        //region bidding numbers
        tblNumbers = new Table();
        tblNumbers.setName("tblBidNumbers");

        for (int i = minBid; i < 8; i++) {
            btnNumber = new TextButton(Integer.toString(i), Assets.Skins);
            btnNumber.setName(Integer.toString(i));
            btnNumber.setUserObject(Integer.toString(i));
            btnNumber.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getWidth() / 10);
            btnNumber.setBounds(0, 0, btnNumber.getWidth(), btnNumber.getHeight());
            btnNumber.align(Align.center | Align.center);
            btnNumber.pad(0f, 30f, 0f, 30f);
            btnNumber.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Gdx.app.log(getStageName(), " - " + event.getListenerActor().getName());
                    biddingBooks = Integer.parseInt(event.getListenerActor().getUserObject().toString());
                }
            });
            if (GamePlay.GAME_BOOKS == 0 || i > GamePlay.GAME_BOOKS ||
                    i == GamePlay.GAME_BOOKS && GamePlay.GAME_DIRECTION != GamePlay.BidRule_Direction.NoTrump) {
                tblNumbers.add(btnNumber);
            } else {
                btnNumber.remove();
            }
        }
        tblBiddingNumbers.add(tblNumbers);
        tblBiddingNumbers.row();
        //endregion

        //region bid or pass buttons
        btnNumber = new TextButton("Pass", Assets.Skins);
        btnNumber.setName("Pass");
        btnNumber.pad(0f, 20f, 0f, 20f);
        btnNumber.addListener(new PassOrBidPlayClickListener());

        tblOutter.add(btnNumber);

        //region bid direction
        tblDirection = new Table();

        tblDirection.setName("tblDirection");
        tblDirection.pad(0f, 20f, 0f, 20f);

        btnNumber = new TextButton("Dn", Assets.Skins);
        btnNumber.setName("Downtown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked());

        tblDirection.add(btnNumber);

        btnNumber = new TextButton("Up", Assets.Skins);
        btnNumber.setName("Uptown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        tblDirection.add(btnNumber);
        btnNumber.addListener(new BidDirectionClicked());

        tblOutter.add(tblDirection);

        btnNumber = new TextButton(" X ", Assets.Skins);
        btnNumber.setName("NoTrump");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked());

        tblDirection.add(btnNumber);

        //endregion

        btnNumber = new TextButton("Bid", Assets.Skins);
        btnNumber.setName("Bid");
        btnNumber.pad(0f, 35f, 0f, 35f);
        btnNumber.addListener(new PassOrBidPlayClickListener());

        tblOutter.add(btnNumber);

        //endregion

        tblBiddingNumbers.add(tblOutter);
        //endregion

        tblBiddingNumbers.pad(Value.percentWidth(.20f));

        grpBiddingNumbers.setPosition(Gdx.graphics.getWidth() / 2 - tblBiddingNumbers.getWidth() / 2,
                Gdx.graphics.getHeight() * .465f);
        grpBiddingNumbers.setBounds(grpBiddingNumbers.getX(), grpBiddingNumbers.getY(),
                grpBiddingNumbers.getWidth(), grpBiddingNumbers.getHeight());
        grpBiddingNumbers.addActor(tblBiddingNumbers);
        this.addActor(grpBiddingNumbers);
        grpBiddingNumbers.draw(batch, 1f);
    }

    protected void ConfigureAndShowKitty() {
        float baseLine = Assets.P1YBaseLine;//bidWhistGame.gamePlay.KittyHand.get(0).PlayingCard().getY();
        grpKitty = new Group();
        grpKitty.setVisible(true);
        float XPos = 0;
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

    private class BidDirectionClicked extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.app.log(getStageName() + " -> Direction", " - " + event.getListenerActor().getName());
            String btnName = event.getListenerActor().getName();

            bidDirection = GamePlay.BidRule_Direction.valueOf(btnName);
        }
    }

    private class PassOrBidPlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            boolean validBid = false;

            String btnName = event.getListenerActor().getName();
            Gdx.app.log(getStageName() + " -> Play", " - " + btnName);

            switch (btnName) {
                case "Bid":
                    biddingPlayer.setBidHand_Direction(bidDirection);
                    biddingPlayer.setBidHand_Books(biddingBooks);
                    try {
                        validBid = bidWhistGame.PlayerHasBidded(biddingPlayer);
                        if (validBid) {
                            minBid = biddingPlayer.getBidHand_Books();
                        } else {
                            biddingPlayer.setPlayerHasBidded(false);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Pass":
                    bidWhistGame.PlayerPassed(biddingPlayer);
                    break;
            }
        }
    }

    @Override
    public boolean keyDown(int keyCode) {
        String fromStage, toStage = "";
        fromStage = this.getStageName();
        switch (keyCode) {
            //region move forward
            case Input.Keys.SPACE:
                Gdx.app.log(fromStage, "Leaving");
                Assets.ClearScreen();
                switch (fromStage) {
                    case "MainMenuStage":
                        toStage = "BiddingStage";
                        break;
                    case "BiddingStage":
                        toStage = "TrumpSelectStage";
                        break;
                    case "TrumpSelectStage":
                        toStage = "GamePlayStage";
                        break;
                    case "GamePlayStage":
                        toStage = "BiddingStage";
                        break;
                }
                break;
            //endregion

            //region move backward
            case Input.Keys.B:
                switch (fromStage) {
                    case "MainMenuStage":
                        toStage = "";
                        break;
                    case "BiddingStage":
                        toStage = "MainMenuStage";
                        break;
                    case "TrumpSelectStage":
                    case "GamePlayStage":
                        toStage = "BiddingStage";
                        break;
                }
                break;
            //endregion
        }
        if (toStage != "") {
            try {
                bidWhistGame.ChangeScreenTo(toStage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    protected class CardClickListener extends ClickListener {
        float baseLine = 0;

        public CardClickListener(float baseLine) {
            super();
            this.baseLine = baseLine;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            event.cancel();
            selectedCard = (Card) event.getTarget().getUserObject();
            Gdx.app.log("Card Pressed", selectedCard.toString());

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isRaised = ToggleRaiseOnCardsX(selectedCard, baseLine);
            if (stageName.equals("TrumpSelectStage")) {
                ((TrumpSelectStage) (_BidWhistStage.this)).KittyCardPlayed(_BidWhistStage.this, selectedCard);
            }
        }
    }

    protected boolean ToggleRaiseOnCardsX(Card selectedCard, float baseLine) {
        boolean isRaised = !selectedCard.IsRaised();
        selectedCard.SetIsRaised(isRaised);
        selectedCard.SetReadyToPlay(isRaised);
        selectedCard.PlayingCard().setPosition(selectedCard.PlayingCard().getX(),
                isRaised ? selectedCard.PlayingCard().getY() + Assets.P1CardYLevitate : baseLine);
        return isRaised;
    }
}
