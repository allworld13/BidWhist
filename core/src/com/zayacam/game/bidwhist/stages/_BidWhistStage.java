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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    protected boolean hasStartedPlaying;
    protected GamePlay.BidRule_Direction bidDirection = null;

    Group grpKitty, grpSouthPlayer, grpTableHand, grpBiddingNumbers;
    int XPos;

    //region ctor
    _BidWhistStage(ScreenViewport vPort) {
        super(vPort);
        batch = new SpriteBatch();
        stageName = Utils.GetStageName(this.toString());

        screenTitle = new GlyphLayout();

        if (stageName.contains("MainMenuStage"))
            Assets.LoadGameScreen(true);
        else
            Assets.LoadGameScreen();

        Assets.sprite_background.setRegion(Assets.text_background);
        setKeyboardFocus(currentScreen);
        im = new InputMultiplexer(this);

        grpSouthPlayer = new Group();
        grpBiddingNumbers = new Group();
    }

    public _BidWhistStage(BidWhistGame bidWhistGame, ScreenViewport vPort) {
        this(vPort);
        this.bidWhistGame = bidWhistGame ;
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

    protected void ShowPlayersHand(Batch batch, BidPlayer biddingPlayer, float offSet) {
        float P1Width = this.getWidth() * .17f;
        float P1Height = this.getHeight() * .29f;

        int XPos = 0;
        int cardIndex = 0;
        if (biddingPlayer == null) return;

        for (Card c : biddingPlayer.getHand()) {
            if (c.IsAvailable()) {
                c.setGrpIndexName(++cardIndex);
                c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
                c.PlayingCard().setSize(P1Width, P1Height);
                c.PlayingCard().setUserObject(c);
                grpSouthPlayer.addActor(c.PlayingCard());
                XPos += (int) (this.getWidth() * .055F);
            }
        }
        grpSouthPlayer.setPosition(this.getWidth() / 8.5f, 2);// this.getHeight()  );
        grpSouthPlayer.draw(batch, 1F);
        this.addActor(grpSouthPlayer);
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

    protected void ConfigureAndShowKitty(float offSet) {
        float P1Width = this.getWidth() / 9.5F;
        float P1Height = this.getHeight() / 4.5F;

        grpKitty = new Group();
        grpKitty.setVisible(true);
        float XPos = 0;
        for (Card c : bidWhistGame.gamePlay.KittyHand) {
            c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
            c.PlayingCard().setSize(P1Width, P1Height);
            c.PlayingCard().setUserObject(c);
            grpKitty.addActor(c.PlayingCard());
            XPos += (int) (this.getWidth() * .045F);
        }
        grpKitty.setBounds(this.getWidth() / 2 - XPos / 2,
                this.getHeight() / 2,
                XPos, P1Height);

        grpKitty.setPosition((this.getWidth() / 2 - XPos / 2) - 30,
                this.getHeight() - P1Height - offSet);
        this.addActor(grpKitty);
    }

    @Override
    public boolean keyDown(int keyCode) {
        String fromStage, toStage = "";
        fromStage = this.getStageName();
        switch (keyCode) {
            //region move forward
            case Input.Keys.SPACE:
                Gdx.app.log(fromStage, "Leaving");
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
}
