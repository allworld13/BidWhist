package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.actors.BiddingScreenActor;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.stream.Collectors;

public class BiddingStage extends _BidWhistStage {

    Button btnNumber, button1, button2, button3, button4;
    private int minBid, biddingBooks = 0;
    boolean finishedBidding = false;

    Table tblMaster;

    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        GamePlay.GAME_BOOKS = 0;

        currentScreen = new BiddingScreenActor(bidWhistGame, this);
        Assets.LoadBidScreen();
        minBid = bidWhistGame.gamePlay.getMinimalBid();
        this.addActor(currentScreen);
    }

    @Override
    public void act(float delta) {
        finishedBidding = bidWhistGame.gamePlay.gamePlayers
                .stream().allMatch(p -> p.PlayerHasBidded());

        if (!finishedBidding) {
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
            biddingPlayer = bidWinner;
            try {
                if (bidWinner.getBidDirection() != GamePlay.BidRule_Direction.NoTrump) {
                    bidWhistGame.ChangeScreenTo("DetermineTrumpStage");
                } else {
                    bidWhistGame.ChangeScreenTo("GamePlayStage");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();

        getBatch().begin();
        this.getBatch().draw(Assets.text_background, 0, 0, this.getWidth(), this.getHeight());

        if (bidWinner != null) {
            grpKitty.draw(this.getBatch(), 1F);
            }

        if (!finishedBidding) {
            if (bidWinner == null) {
                LoadBidNumberButtons();
            }
        } else {
            this.getActors().removeValue(tblMaster, true);
            tblMaster.setVisible(false);
            //tblMaster = null;
        }

        ShowPlayersName(this.getBatch());
        ShowPlayersHand(this.getBatch(), biddingPlayer, 65f);
        getBatch().end();

    }

    private void LoadBidNumberButtons() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (finishedBidding) {
            return;
        }
        Table tblNumbers, tblDirection, tblOutter;

        tblOutter = new Table();
        tblMaster = new Table();
        tblMaster.setName("tblMaster");

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
                    Gdx.app.log("BiddingScreenActor", " - " + event.getListenerActor().getName());
                    biddingBooks = Integer.parseInt(event.getListenerActor().getUserObject().toString());
                }
            });
            if (GamePlay.GAME_BOOKS == 0 || i >= GamePlay.GAME_BOOKS)
                tblNumbers.add(btnNumber);
            else {
                btnNumber.setVisible(false);
                tblNumbers.removeActor(btnNumber);
            }

        }
        tblMaster.add(tblNumbers);
        tblMaster.row();
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

        btnNumber = new TextButton("Up", Assets.Skins);
        btnNumber.setName("Uptown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        tblDirection.add(btnNumber);
        btnNumber.addListener(new BidDirectionClicked());

        tblOutter.add(tblDirection);

        btnNumber = new TextButton("Dn", Assets.Skins);
        btnNumber.setName("Downtown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        btnNumber.addListener(new BidDirectionClicked());

        tblDirection.add(btnNumber);

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

        tblMaster.add(tblOutter);
        //endregion

        tblMaster.pad(Value.percentWidth(.20f));
        tblMaster.setPosition(Gdx.graphics.getWidth() / 2 - tblMaster.getWidth() / 2, Gdx.graphics.getHeight() * .37f);

        grpBidding.addActor(tblMaster);
        grpBidding.setBounds(grpBidding.getX(), grpBidding.getY(), grpBidding.getWidth(), grpBidding.getHeight());
        this.addActor(grpBidding);
        grpBidding.draw(this.getBatch(), 1f);
    }

    private class BidDirectionClicked extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.app.log("BiddingScreenActor -> Direction", " - " + event.getListenerActor().getName());
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
            Gdx.app.log("BiddingScreenActor -> Play", " - " + btnName);

            switch (btnName) {
                case "Bid":
                    biddingPlayer.setBidHand_Direction(bidDirection);
                    biddingPlayer.setBidHand_Books(biddingBooks);
                    try {
                        validBid = bidWhistGame.PlayerHasBidded(biddingPlayer);
                        if (validBid && biddingPlayer.getBidDirection() != GamePlay.BidRule_Direction.NoTrump) {
                            minBid = biddingPlayer.getBidHand_Books();
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
}
