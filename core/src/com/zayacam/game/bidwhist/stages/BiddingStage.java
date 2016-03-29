package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.stream.Collectors;

public class BiddingStage extends _BidWhistStage {

    boolean resetBidActors = true;

    //region ctors
    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Place your bid";

        GamePlay.GAME_BOOKS = 0;
        Assets.PlayDeckShuffling();

        tblBiddingNumbers = new Table();
        tblBiddingNumbers.setName("tblBiddingNumbers");

        grpBiddingNumbers = new Group();
        grpBiddingNumbers.addActor(tblBiddingNumbers);
        this.addActor(grpBiddingNumbers);
    }
    //endregion

    @Override
    public void act(float delta) {
        Assets.ClearScreen();
        finishedBidding = bidWhistGame.gamePlay.gamePlayers
                .stream().allMatch(p -> p.PlayerHasBidded());

        if (!finishedBidding) {
            if (resetBidActors) {
                CreateBidNumberActor(minBid);
            }
            resetBidActors = false;

            for (BidPlayer bp : bidWhistGame.gamePlay.gamePlayers
                    .stream().filter(p -> !p.PlayerHasBidded()).collect(Collectors.toList())) {
                bidWhistGame.PlayersTurnToBid(bp);
                biddingPlayer = bp;
                finishedBidding = false;
                break;
            }
        }
        if (finishedBidding) {
            bidWinner = bidWhistGame.DetermineBidWinner();
            if (bidWinner == null) {
                try {
                    Utils.Beep();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                biddingPlayer = bidWhistGame.gamePlay.gamePlayers.get(bidWhistGame.gamePlay.MAX_NO_PLAYERS - 1);
                biddingPlayer.setPlayerHasBidded(false);
                bidWhistGame.YouMustBid(biddingPlayer);
                btnPass.setColor(Color.DARK_GRAY);
                btnPass.setDisabled(true);
                btnPass.clearListeners();
                finishedBidding = false;
            } else {
                biddingPlayer = bidWinner;
                try {
                    bidWhistGame.ChangeScreenTo("TrumpSelectStage");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();

        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);

        if (!finishedBidding) {
            if (bidWinner == null) {
                DrawBidNumberButtons(batch);
            }
            DrawPlayerHand(batch, biddingPlayer);
            ShowPlayersName(batch);
        }
        if (bidWhistGame.gamePlay.BidAwarded()) {
            DrawGameBidLegend();
        }
        DrawGameScore(batch);
        batch.end();
    }

    @Override
    public boolean keyDown(int keyCode) {
        super.keyDown(keyCode);

        if (biddingPlayer.isHuman()) {
            switch (keyCode) {
                case Input.Keys.NUM_4:
                case Input.Keys.NUMPAD_4:
                    biddingBooks = 4;
                    biddingPlayer.setBidHand_Books(4);
                    break;
                case Input.Keys.NUM_5:
                case Input.Keys.NUMPAD_5:
                    biddingBooks = 5;
                    biddingPlayer.setBidHand_Books(5);
                    break;
                case Input.Keys.NUM_6:
                case Input.Keys.NUMPAD_6:
                    biddingBooks = 6;
                    biddingPlayer.setBidHand_Books(6);
                    break;
                case Input.Keys.NUM_7:
                case Input.Keys.NUMPAD_7:
                    biddingBooks = 7;
                    biddingPlayer.setBidHand_Books(7);
                    break;
                case Input.Keys.D:
                    bidDirection = GamePlay.BidRule_Direction.Downtown;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.Downtown);
                    break;
                case Input.Keys.U:
                    bidDirection = GamePlay.BidRule_Direction.Uptown;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.Uptown);
                    break;
                case Input.Keys.X:
                case Input.Keys.N:
                    bidDirection = GamePlay.BidRule_Direction.NoTrump;
                    biddingPlayer.setBidHand_Direction(GamePlay.BidRule_Direction.NoTrump);
                    break;
                case Input.Keys.P:
                    btnPass.getClickListener().clicked(null, 0, 0);
                    break;
            }
        }
        return true;
    }

    private void CreateBidNumberActor(int minBid) {
        grpBiddingNumbers.clear();
        tblBiddingNumbers.clear();

        Table tblDirection, tblOutter;
        tblOutter = new Table();

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
            btnNumber.addListener(new BidPressedClickListener());

            if (GamePlay.GAME_BOOKS == 0 || i > GamePlay.GAME_BOOKS ||
                    i == GamePlay.GAME_BOOKS && GamePlay.GAME_DIRECTION != GamePlay.BidRule_Direction.NoTrump) {
                tblNumbers.add(btnNumber);
            }
        }
        tblBiddingNumbers.add(tblNumbers);
        tblBiddingNumbers.row();
        //endregion

        //region bid or pass buttons
        btnPass = new TextButton("Pass", Assets.Skins);
        btnPass.setName("Pass");
        btnPass.pad(0f, 20f, 0f, 20f);
        btnPass.addListener(new PassOrBidPlayClickListener());
        tblOutter.add(btnPass);

        //region bid direction
        tblDirection = new Table();
        tblDirection.setName("tblDirection");
        tblDirection.pad(0f, 20f, 0f, 20f);

        btnNumber = new TextButton("Dn", Assets.Skins);
        btnNumber.setName("Downtown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked(tblDirection));
        tblDirection.add(btnNumber);

        btnNumber = new TextButton("Up", Assets.Skins);
        btnNumber.setName("Uptown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked(tblDirection));
        tblDirection.add(btnNumber);

        btnNumber = new TextButton(" X ", Assets.Skins);
        btnNumber.setName("NoTrump");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked(tblDirection));
        tblDirection.add(btnNumber);

        //endregion

        tblOutter.add(tblDirection);

        btnBid = new TextButton("Bid", Assets.Skins);
        btnBid.setName("Bid");
        btnBid.pad(0f, 35f, 0f, 35f);
        btnBid.addListener(new PassOrBidPlayClickListener());
        tblOutter.add(btnBid);

        //endregion

        tblBiddingNumbers.add(tblOutter);
        //endregion

        tblBiddingNumbers.pad(Value.percentWidth(.20f));
        grpBiddingNumbers.addActor(tblBiddingNumbers);
    }

    private void DrawBidNumberButtons(SpriteBatch batch) {

        grpBiddingNumbers.setPosition(getWidth() / 2 - tblBiddingNumbers.getWidth() / 2, getHeight() * .55f);
        grpBiddingNumbers.setBounds(grpBiddingNumbers.getX(), grpBiddingNumbers.getY(),
                grpBiddingNumbers.getWidth(), grpBiddingNumbers.getHeight());
        grpBiddingNumbers.draw(batch, 1f);
    }

    //handles when (pass or bid) button is pressed
    class PassOrBidPlayClickListener extends ClickListener {
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
                            resetBidActors = true;
                        } else {
                            biddingPlayer.setPlayerHasBidded(false);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Pass":
                    HilightPressedButton(null, tblNumbers);
                    bidWhistGame.PlayerPassed(biddingPlayer);
                    break;
            }
        }
    }

    //handles when bid (number) button is pressed
    private class BidPressedClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.app.log(getStageName(), " - " + event.getListenerActor().getName());
            biddingBooks = Integer.parseInt(event.getListenerActor().getUserObject().toString());
            HilightPressedButton(event, tblNumbers);
        }
    }


}
