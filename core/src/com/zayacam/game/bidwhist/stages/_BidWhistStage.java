package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.cards.CardSuit;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;


public abstract class _BidWhistStage extends Stage implements InputProcessor {

    public InputMultiplexer im;
    protected BidPlayer currentPlayer;
    protected BidPlayer bidWinner, biddingPlayer = null;

    protected String stageName;
    protected String ScreenTitleLabel = "";
    protected GlyphLayout screenTitle;
    protected SpriteBatch batch;
    protected BidWhistGame bidWhistGame;
    protected Actor currentScreen;
    protected Vector2 touchCoord, touchedVector;
    protected Actor hitActor;
    protected Card selectedCard;
    protected CardSuit cardPlayedSuit;
    protected boolean hasStartedPlaying, finishedBidding = false;
    protected GamePlay.BidRule_Direction bidDirection = null;
    protected boolean ShowKitty = false;
    protected int noOfSelectedDiscards = 0;
    protected boolean CardSelectedAdded;
    protected Button btnPass, btnBid;
    protected int playRound = 1;

    Group grpKitty, grpSouthPlayer, grpTableHand, grpBiddingNumbers;
    Table tblBiddingNumbers, tblNumbers;

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
    }

    public _BidWhistStage(BidWhistGame bidWhistGame, ScreenViewport vPort) {
        this(vPort);
        this.bidWhistGame = bidWhistGame ;
        minBid = bidWhistGame.gamePlay.getMinimalBid();
    }
    //endregion

    @Override
    public void draw() {
        Assets.ClearScreen();
        super.draw();

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
        float baseLine = 0;
        try {
            baseLine = bidPlayer.getHand().get(0).PlayingCard().getY();
        } catch (Exception ex) {
            if (bidPlayer == null)
                System.out.println("Bid Player null game finished!");
            return;

        }
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
                if (stageName.equals("TrumpSelectStage") && !CardSelectedAdded) {
                    c.PlayingCard().addListener(new CardClickListener());
                }

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

        if (playRound <= GamePlay.MAX_PLAYER_HANDSIZE) {
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
            if (stageName.equals("GamePlayStage")) {
                if (currentPlayer == bp)
                    Assets.PlayerNameFont.setColor(Color.GOLD);
            } else {
                if (biddingPlayer == null && bidWinner != null && bidWinner.getPlayerName() == playerName)
                    Assets.PlayerNameFont.setColor(Color.GOLD);
                else if (biddingPlayer == null) {
                } else if (biddingPlayer.getPlayerName() == playerName)
                    Assets.PlayerNameFont.setColor(Color.GOLD);
            }
            Assets.PlayerNameFont.draw(batch, playerName, X, Y);
        }
    }

    @Override
    public boolean keyDown(int keyCode) {
        String fromStage, toStage = "";
        fromStage = this.getStageName();
        switch (keyCode) {
            case Input.Keys.ESCAPE:
                Gdx.app.log(fromStage, "Leaving");
                toStage = "MainMenuStage";
                MainMenuStage.MainMenuReset = false;
                break;
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
                        MainMenuStage.MainMenuReset = false;
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

    protected boolean ToggleRaiseOnCardsX(Card selectedCard, float baseLine) {
        boolean isRaised = !selectedCard.IsRaised();
        selectedCard.SetIsRaised(isRaised);
        selectedCard.SetReadyToPlay(isRaised);
        selectedCard.PlayingCard().setPosition(selectedCard.PlayingCard().getX(),
                isRaised ? selectedCard.PlayingCard().getY() + Assets.P1CardYLevitate : baseLine);
        return isRaised;
    }

    protected void BaseLineAllCards() {
        for (BidPlayer p : bidWhistGame.gamePlay.gamePlayers) {
            for (Card c : p.getHand().getCards()) {
                c.PlayingCard().setPosition(c.PlayingCard().getX(), Assets.P1YBaseLine);
                c.SetReadyToPlay(false);
                c.SetAvailable(true);
            }
        }
    }

    protected void DrawGameBidLegend() {
        Assets.textBounds = new GlyphLayout();
        Assets.DefaultFont.setColor(Color.WHITE);
        Assets.textBounds.setText(Assets.DefaultFont, "( " + bidWhistGame.gamePlay.bidWinner.getPlayerName() + " )");
        Assets.DefaultFont.draw(batch, Assets.textBounds, this.getWidth() * .85f, getHeight() * 0.98f);
        Assets.textBounds.setText(Assets.DefaultFont, bidWhistGame.gamePlay.GetGameBid());
        float XPos = this.getWidth() - Assets.textBounds.width - 8f;
        Assets.DefaultFont.draw(batch, Assets.textBounds, XPos, getHeight() * 0.94f);
        if (GamePlay.GAME_SUIT != null) {
            Assets.textBounds.setText(Assets.DefaultFont, GamePlay.GAME_SUIT.name());
            Assets.DefaultFont.draw(batch, Assets.textBounds, XPos, getHeight() * 0.905f);
        }
    }

    public void DrawGameScore(SpriteBatch batch) {
        Assets.textBounds = new GlyphLayout();
        Assets.DefaultFont.setColor(Color.WHITE);

        Assets.textBounds.setText(Assets.DefaultFont, "Us    Them");
        Assets.DefaultFont.draw(batch, Assets.textBounds, this.getWidth() * .094f, getHeight() * 0.98f);

        Assets.textBounds.setText(Assets.DefaultFont, "Score" + bidWhistGame.gamePlay.ShowTeamGameScore());
        Assets.DefaultFont.draw(batch, Assets.textBounds, this.getWidth() * .012f, getHeight() * 0.93f);

        Assets.textBounds.setText(Assets.DefaultFont, "Tricks" + bidWhistGame.gamePlay.ShowTeamTrickTakes());
        Assets.DefaultFont.draw(batch, Assets.textBounds, this.getWidth() * .012f, getHeight() * 0.89f);
    }

    protected void HilightPressedButton(Group grpParent) {
        HilightPressedButton(null, grpParent);
    }

    protected void HilightPressedButton(InputEvent event, Group grpParent) {
        for (Actor a : grpParent.getChildren()) {
            a.setColor(1, 1, 1, 1);
        }
        if (event != null)
            event.getListenerActor().setColor(Color.GOLD);
    }

    protected class BidDirectionClicked extends ClickListener {
        Table parentTable;

        public BidDirectionClicked(Table tblDirection) {
            parentTable = tblDirection;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.app.log(getStageName() + " -> Direction", " - " + event.getListenerActor().getName());
            String btnName = event.getListenerActor().getName();

            bidDirection = GamePlay.BidRule_Direction.valueOf(btnName);
            HilightPressedButton(event, parentTable);
        }
    }

    protected class CardClickListener extends ClickListener {
        float baseLine;

        public CardClickListener() {
            super();
            baseLine = 0;
        }

        public CardClickListener(float baseLine) {
            this();
            this.baseLine = baseLine;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            selectedCard = (Card) event.getTarget().getUserObject();
            boolean isRaised = ToggleRaiseOnCardsX(selectedCard, baseLine);
            Gdx.app.log("Card Pressed", selectedCard.toString());
            switch (stageName) {
                case "TrumpSelectStage":
                    if (noOfSelectedDiscards <= GamePlay.MAX_CARDS_TO_DISCARD) {
                        ((TrumpSelectStage) (_BidWhistStage.this)).KittyCardPlayed(_BidWhistStage.this, selectedCard);
                    }
                    if (noOfSelectedDiscards == GamePlay.MAX_CARDS_TO_DISCARD) {
                        ((TrumpSelectStage) (_BidWhistStage.this)).ReadyToDiscard(true);
                    } else {
                        ((TrumpSelectStage) (_BidWhistStage.this)).ReadyToDiscard(false);
                    }
                    break;
            }
        }
    }
}
