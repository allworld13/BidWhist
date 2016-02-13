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

        if (bidWinner == null)
            bidWinner = bidWhistGame.gamePlay.bidWinner;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!bidWhistGame.gamePlay.GamePlayerOrderSet()) {
            bidWhistGame.gamePlay.setGamePlayerPlayOrder(bidWinner);
            bidWhistGame.gamePlay.SetGamePlayerOrder(true);
        }
        for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers) {
            if (!bp.HasPlayed()) {
                currentPlayer = bp;
                break;
            }
            else {
                //bidWhistGame.gamePlay.ValidatePlayersPlay(bp);
                bp.PlayerHasPlayed(true, selectedCard);
            }
        }
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

        batch.begin();
        batch.draw(Assets.text_background, 0, 0, this.getWidth(), this.getHeight());
        ShowPlayersName(batch);
        DrawTableHand(batch);
        DrawPlayerHand(batch, bidWinner);
        if (bidWhistGame.gamePlay.BidAwarded()) {
            ShowGameBid();
        }
        batch.end();
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
                    currentPlayer.PlayerHasPlayed(true, selectedCard);

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
        } else {


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
