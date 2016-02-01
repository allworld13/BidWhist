package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.stages._BidWhistStage;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GamePlayActor extends _BidActor implements InputProcessor {
    //Thread thread ;

    Vector2 touchCoord, touchedVector;
    boolean hasStartedPlaying;
    Card selectedCard;
    Group grpSouthPlayer, grpTableHand;

    int XPos, YPos;

    //region ctor

    GamePlayActor() {
        super();

        YPos = 0;
        XPos = 225;

        //Gdx.input.setInputProcessor(this);
    }

    GamePlayActor(BidWhistGame bidWhistGame) throws InterruptedException {
        this();
        this.bidWhistGame = bidWhistGame;

        //region thread stuff
        /*
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //gamePlay.InitializeGamePlay();
                try {
                    gamePlay.PlayTheGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        */
        //endregion
    }

    public GamePlayActor(BidWhistGame bidWhistGame, _BidWhistStage stage) throws InterruptedException {
        this(bidWhistGame);
        this.stage = stage;
    }

    //endregion

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        //if (!gamePlay.isGameStarted() ) {
            grpSouthPlayer = new Group();
            grpTableHand = new Group();
            grpTableHand.setPosition(stage.getWidth()/2 - Assets.CardWidth/2, stage.getHeight()/2);
            bidWhistGame.gamePlay.setGameStarted(true);
        //}


        batch.draw(Assets.text_background, 0,0, stage.getWidth(), stage.getHeight());
        ShowPlayersName(batch);
        DrawTableHand((SpriteBatch) batch);
        DrawPlayerHand((SpriteBatch) batch);
    }

    //region IP Overrides
    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchedVector = new Vector2((float)screenX, (float)screenY);
        touchCoord = stage.screenToStageCoordinates(touchedVector);

        hitActor = stage.hit(touchCoord.x, touchCoord.y,false);
        if (hitActor != null) {
            selectedCard = (Card) hitActor.getUserObject();
            if (selectedCard != null)
                Gdx.app.log("Hit", hitActor.getName() + " - Raised: " + !selectedCard.IsRaised());

            ResetRaiseOnAllCardsX(selectedCard);
            int toRaised;

            if (selectedCard == null) return true;

            if (selectedCard.IsReadyToPlay() && selectedCard.IsRaised() ) {
                if (PlaySelectedCard(hitActor)){
                    grpSouthPlayer.removeActor(hitActor);
                    AddToTableHand(hitActor);
                }
            }
            else {
                if (selectedCard != null) {
                    toRaised = !selectedCard.IsRaised() ? 1:-1 ;
                    float P1ActiveCardYPos = hitActor.getY();
                    switch (toRaised) {
                        case 1:
                            P1ActiveCardYPos += Assets.P1CardYLevitate ;
                            selectedCard.SetReadyToPlay(true);
                            break;
                        default:
                            P1ActiveCardYPos = Assets.P1YBaseLine ;
                            break;
                    }
                    selectedCard.SetIsRaised(toRaised == 1 ? true :false );
                    hitActor.setPosition(hitActor.getX(), P1ActiveCardYPos);
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
//endregion

    void ResetRaiseOnAllCardsX(Card selectedCard) {
        Card c;
        for (Actor a: grpSouthPlayer.getChildren()) {
            c = ((Card)a.getUserObject());
            if (selectedCard == c)
                continue;
            c.SetIsRaised(false);
            c.SetReadyToPlay(false);
            a.setPosition(a.getX(), Assets.P1YBaseLine );
        }
    }

    void DrawTableHand(SpriteBatch batch) {
        stage.addActor(grpTableHand);
        grpTableHand.setPosition(stage.getWidth()/2, 0);
        grpTableHand.setBounds(stage.getWidth()/4, 0,
                stage.getWidth()/6, stage.getHeight()/2);
    }

    void DrawPlayerHand(SpriteBatch batch) {
        float EWMargin = .2F;
        float P1Width = stage.getWidth()/8.5F;
        float P1Height = stage.getHeight()/4.5F;

        int cardIndex=0;
        for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers) {
            switch (bp.getIndex()) {
                //region South Player
                case 1:
                    //South Player
                    XPos = 0;
                    grpSouthPlayer.setPosition(stage.getWidth()/8+10 , 0);
                    for (Card c :bp.getHand()) {
                        if (c.IsAvailable()) {
                            c.setGrpIndexName(++cardIndex);
                            c.PlayingCard().setPosition(XPos, c.PlayingCard().getY() );
                            c.PlayingCard().setSize(P1Width, P1Height);
                            c.PlayingCard().setUserObject(c);
                            grpSouthPlayer.addActor(c.PlayingCard());
                            XPos += (int)(stage.getWidth() * .055F);
                        }
                    }
                    grpSouthPlayer.draw (batch,1F);
                    stage.addActor(grpSouthPlayer);


                    if (!hasStartedPlaying) {
                        System.out.println(String.format("%1f %2f  %3f",
                                stage.getWidth(),
                                grpSouthPlayer.getWidth(),
                                (stage.getWidth()/2 - grpSouthPlayer.getWidth()/2)));

                        System.out.println(bp.getHand().GetCardsString());
                    }
                    break;
                //endregion

                //region West Player
                case 2:
                    Assets.gfxDeck.get(Assets.CardBack).getDrawable()
                            .draw(batch,
                                    (20 * EWMargin),
                                    stage.getHeight() / Assets.PlayerCard_Y_Ratio,
                                    stage.getWidth() / Assets.PlayerCardWidthRatio,
                                    stage.getHeight() * Assets.PlayerCardHeightRatio);
                    break;

                //endregion

                //region East Player
                case 4:
                    Assets.gfxDeck.get(Assets.CardBack).getDrawable()
                            .draw(batch,
                                    stage.getWidth() - (stage.getWidth() * .120F) ,
                                    stage.getHeight() / Assets.PlayerCard_Y_Ratio,
                                    stage.getWidth() / Assets.PlayerCardWidthRatio,
                                    stage.getHeight() * Assets.PlayerCardHeightRatio);
                    break;
                //endregion
            }

            XPos += 120;
            hasStartedPlaying = true;
        }
    }

    boolean PlaySelectedCard(Actor hitActor) {
        boolean cardPlayed = false;
        Gdx.app.log("Play",selectedCard.toStringBef());
        Card c = (Card)hitActor.getUserObject();
        if (c != null) {
            cardPlayed = bidWhistGame.gamePlay.PlayThisCard(c);
            if (cardPlayed) {
                float duration = 1.5f;
                hitActor.addAction(
                        parallel(
                                moveTo(stage.getWidth() /2 - Assets.CardWidth / 2,
                                        stage.getHeight() /2, duration),
                                scaleTo(.75f, .9f, duration),
                                rotateTo(360*3, .75f)
                        )
                );
            }
        }
        return cardPlayed;
    }

    void AddToTableHand(Actor hitActor) {
        hitActor.setTouchable(Touchable.disabled);
        grpTableHand.addActor(hitActor);
        Gdx.app.log("toTable", hitActor.getName());
    }







}
