package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.cards.CardSuit;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.CardPlay;
import com.zayacam.game.bidwhist.game.GamePlay;

public class GamePlayStage extends _BidWhistStage implements InputProcessor {

    private CardSuit leadSuit = null;
    BidPlayer lastRoundWinner = null;
    private boolean willLose, validCardPlayed, cutCardPlayed;
    CardPlay cardPlayed;


    public GamePlayStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) throws InterruptedException {
        super(bidWhistGame, sViewport);

        if (bidWinner == null)
            lastRoundWinner = bidWinner = bidWhistGame.gamePlay.bidWinner;

        bidWinner.SetHandWinner(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!bidWhistGame.gamePlay.gamePlayers.stream().allMatch(bp -> bp.HasPlayed())) {
            currentPlayer = GetNextPlayersPlay();

            if (currentPlayer.isHuman()) {
                // get handled by the touched event

            } else {
                System.out.println("\n" + currentPlayer.toString());
                currentPlayer.getHand().ShowCards();
                int playIndex = currentPlayer.AutoPlayCard(bidWhistGame.gamePlay.getLeadSuit(), playRound);
                selectedCard = currentPlayer.PlayCard(playIndex);
                cardPlayed = new CardPlay(currentPlayer, selectedCard);

                // 2.)   Check to see if the selected card is playable
                try {
                    validCardPlayed = bidWhistGame.gamePlay.PlaySelectedCard(cardPlayed);
                    if (validCardPlayed) {
                        AnimatePlayOfSelectedCard(cardPlayed);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utils.log(stageName, "Time to judge the table hand.");
            lastRoundWinner = bidWhistGame.gamePlay.JudgeTable(playRound);
            lastRoundWinner.AddToBidTaken();
            bidWhistGame.gamePlay.CalculateTeamsScores();

            willLose = bidWhistGame.gamePlay.WillBidWinnerActuallyLose();
            if (willLose) {

            }


            bidWhistGame.gamePlay.AllPlayersPlayedReset();
            bidWhistGame.gamePlay.PlayerOrderSet = false;
            playRound++;
            System.out.println(bidWhistGame.gamePlay.ShowTeamScore());
        }
    }

    private BidPlayer GetNextPlayersPlay() {
        if (!GamePlay.PlayerOrderSet) {
            bidWhistGame.gamePlay.SetGamePlayerPlayOrder(lastRoundWinner);
        }
        currentPlayer = bidWhistGame.gamePlay.gamePlayers.stream().filter(p -> !p.HasPlayed()).findFirst().get();
        return currentPlayer;
    }

    @Override
    public void draw() {
        //super.draw();

        //if (!gamePlay.isGameStarted() ) {
        grpSouthPlayer = new Group();
        grpTableHand = new Group();
        grpTableHand.setPosition(this.getWidth() / 2 - Assets.CardWidth / 2, this.getHeight() / 2);
        bidWhistGame.gamePlay.setGameStarted(true);
        //}

        batch.begin();
        batch.draw(Assets.text_background, 0, 0, this.getWidth(), this.getHeight());
        DrawGameScore(batch);
        ShowPlayersName(batch);
        DrawPlayerHand(batch, bidWinner);
        if (bidWhistGame.gamePlay.BidAwarded()) {
            DrawGameBidLegend();
        }
        DrawRoundCount(batch);
        DrawTableHand(batch);
        batch.end();
    }

    private void DrawRoundCount(SpriteBatch batch) {
        Assets.textBounds.setText(Assets.DefaultFont, "Round: " + playRound);
        Assets.DefaultFont.setColor(Color.PURPLE);
        Assets.DefaultFont.draw(batch, Assets.textBounds, (getWidth() - Assets.textBounds.width) / 2f,
                getHeight() * .7f);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchedVector = new Vector2((float) screenX, (float) screenY);
        touchCoord = this.screenToStageCoordinates(touchedVector);

        hitActor = this.hit(touchCoord.x, touchCoord.y, false);
        if (hitActor != null) {
            selectedCard = (Card) hitActor.getUserObject();
            ResetRaiseOnAllCardsX(selectedCard, false);
            int toRaised;

            if (selectedCard == null) return true;

            if (selectedCard.IsReadyToPlay() && selectedCard.IsRaised()) {
                try {
                    validCardPlayed = PlaySelectedCard(hitActor);
                    if (validCardPlayed) {
                        CardPlay cp = new CardPlay(currentPlayer, selectedCard);
                        AnimatePlayOfSelectedCard(cardPlayed);
                        grpTableHand.addActor(selectedCard.PlayingCard());
                        grpSouthPlayer.removeActor(hitActor);
                    } else {
                        ResetRaiseOnAllCardsX(selectedCard, true);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        } else {


        }
        return true;
    }

    private void AnimatePlayOfSelectedCard(CardPlay cardPlayed) {
        if (validCardPlayed) {
            float duration = 2.5f;
            if (cardPlayed == null) return; /*
            cardPlayed.card.PlayingCard().addAction(
                    parallel(
                            moveTo(this.getWidth() / 2 - Assets.CardWidth / 2,
                                    this.getHeight() / 2, duration),
                            scaleTo(.75f, .9f, duration),
                            rotateTo(360 * 3, .75f)
                    )
            );
            */
        }
    }

    void ResetRaiseOnAllCardsX(Card selectedCard, boolean includingThis) {
        Card c;

        for (Actor a : grpSouthPlayer.getChildren()) {
            c = ((Card) a.getUserObject());
            if (selectedCard == c && !includingThis)
                continue;
            c.SetIsRaised(false);
            c.SetReadyToPlay(false);
            a.setPosition(a.getX(), Assets.P1YBaseLine);
        }
    }

    void DrawTableHand(SpriteBatch batch) {
        grpTableHand.setPosition(this.getWidth() / 2, this.getHeight() / 2f);
        grpTableHand.setBounds(this.getWidth() / 4, 400f, this.getWidth() / 6, this.getHeight() / 2);
        grpTableHand.setVisible(true);
        float XPos = 200;
        for (CardPlay cp : bidWhistGame.gamePlay.tableHand) {
            cp.card.PlayingCard().setPosition(XPos, this.getHeight() / 2f);
            cp.card.PlayingCard().setBounds(0, 0, 60, 90);
            this.addActor(cp.card.PlayingCard());
            XPos += 10;
        }

        this.addActor(grpTableHand);
        grpTableHand.draw(batch, 1f);

    }

    boolean PlaySelectedCard(Actor hitActor) throws InterruptedException {
        Gdx.app.log("Playing...", selectedCard.toStringBef());
        Card c = (Card) hitActor.getUserObject();

        if (c != null) {
            validCardPlayed = bidWhistGame.gamePlay.PlaySelectedCard(new CardPlay(currentPlayer, c));
        }
        return validCardPlayed;
    }

}
