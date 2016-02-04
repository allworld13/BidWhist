package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GamePlayStage extends _BidWhistStage implements InputProcessor {

    public GamePlayStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) throws InterruptedException {
        super(bidWhistGame, sViewport);

        this.addActor(currentScreen);
        this.setKeyboardFocus(currentScreen);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw() {
        super.draw();

        //if (!gamePlay.isGameStarted() ) {
        grpSouthPlayer = new Group();
        grpTableHand = new Group();
        grpTableHand.setPosition(this.getWidth() / 2 - Assets.CardWidth / 2, this.getHeight() / 2);
        bidWhistGame.gamePlay.setGameStarted(true);
        //}

        this.getBatch().begin();
        this.getBatch().draw(Assets.text_background, 0, 0, this.getWidth(), this.getHeight());
        ShowPlayersName(this.getBatch());
        DrawTableHand((SpriteBatch) this.getBatch());
        DrawPlayerHand((SpriteBatch) this.getBatch());
        this.getBatch().end();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchedVector = new Vector2((float) screenX, (float) screenY);
        touchCoord = this.screenToStageCoordinates(touchedVector);

        hitActor = this.hit(touchCoord.x, touchCoord.y, false);
        if (hitActor != null) {
            selectedCard = (Card) hitActor.getUserObject();
            if (selectedCard != null)
                Gdx.app.log("Hit", hitActor.getName() + " - Raised: " + !selectedCard.IsRaised());

            ResetRaiseOnAllCardsX(selectedCard);
            int toRaised;

            if (selectedCard == null) return true;

            if (selectedCard.IsReadyToPlay() && selectedCard.IsRaised()) {
                if (PlaySelectedCard(hitActor)) {
                    grpSouthPlayer.removeActor(hitActor);
                    AddToTableHand(hitActor);
                }
            } else {
                if (selectedCard != null) {
                    toRaised = !selectedCard.IsRaised() ? 1 : -1;
                    float P1ActiveCardYPos = hitActor.getY();
                    switch (toRaised) {
                        case 1:
                            P1ActiveCardYPos += Assets.P1CardYLevitate;
                            selectedCard.SetReadyToPlay(true);
                            break;
                        default:
                            P1ActiveCardYPos = Assets.P1YBaseLine;
                            break;
                    }
                    selectedCard.SetIsRaised(toRaised == 1 ? true : false);
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

    void ResetRaiseOnAllCardsX(Card selectedCard) {
        Card c;
        for (Actor a : grpSouthPlayer.getChildren()) {
            c = ((Card) a.getUserObject());
            if (selectedCard == c)
                continue;
            c.SetIsRaised(false);
            c.SetReadyToPlay(false);
            a.setPosition(a.getX(), Assets.P1YBaseLine);
        }
    }

    void DrawTableHand(SpriteBatch batch) {
        this.addActor(grpTableHand);
        grpTableHand.setPosition(this.getWidth() / 2, 0);
        grpTableHand.setBounds(this.getWidth() / 4, 0,
                this.getWidth() / 6, this.getHeight() / 2);
    }

    void DrawPlayerHand(SpriteBatch batch) {
        float EWMargin = .2F;
        float P1Width = this.getWidth() / 8.5F;
        float P1Height = this.getHeight() / 4.5F;

        int cardIndex = 0;
        for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers) {
            switch (bp.getIndex()) {
                //region South Player
                case 1:
                    //South Player
                    XPos = 0;
                    grpSouthPlayer.setPosition(this.getWidth() / 8 + 10, 0);
                    for (Card c : bp.getHand()) {
                        if (c.IsAvailable()) {
                            c.setGrpIndexName(++cardIndex);
                            c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
                            c.PlayingCard().setSize(P1Width, P1Height);
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

                        System.out.println(bp.getHand().GetCardsString());
                    }
                    break;
                //endregion

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

    boolean PlaySelectedCard(Actor hitActor) {
        boolean cardPlayed = false;
        Gdx.app.log("Play", selectedCard.toStringBef());
        Card c = (Card) hitActor.getUserObject();
        if (c != null) {
            cardPlayed = bidWhistGame.gamePlay.PlayThisCard(c);
            if (cardPlayed) {
                float duration = 1.5f;
                hitActor.addAction(
                        parallel(
                                moveTo(this.getWidth() / 2 - Assets.CardWidth / 2,
                                        this.getHeight() / 2, duration),
                                scaleTo(.75f, .9f, duration),
                                rotateTo(360 * 3, .75f)
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
