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
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;

import java.util.stream.Collectors;

public class BiddingStage extends _BidWhistStage {

    Button btnNumber;
    private int minBid, biddingBooks = 0;
    boolean finishedBidding = false;

    Table tblBiddingNumbers;

    public BiddingStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);
        ScreenTitleLabel = "Place your bid";

        GamePlay.GAME_BOOKS = 0;
        minBid = bidWhistGame.gamePlay.getMinimalBid();
        Assets.PlayDeckShuffling();
    }

    @Override
    public void act(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
            if (bidWinner == null) {
                biddingPlayer = bidWhistGame.gamePlay.gamePlayers.get(bidWhistGame.gamePlay.MAX_NO_PLAYERS - 1);
                biddingPlayer.setPlayerHasBidded(false);
                bidWhistGame.YouMustBid(biddingPlayer);
                finishedBidding = false;
            } else {
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
    }

    @Override
    public void draw() {
        super.draw();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(Assets.sprite_background, 0, 0, this.getWidth(), this.getHeight());
        DrawTitle(batch);
        if (bidWinner != null) {
            grpKitty.draw(batch, 1F);
        }

        if (!finishedBidding) {
            if (bidWinner == null) {
                LoadBidNumberButtons();
            }
        }
        ShowPlayersName(batch);
        ShowPlayersHand(batch, biddingPlayer, 65f);
        batch.end();
    }


    private void LoadBidNumberButtons() {
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
            if (GamePlay.GAME_BOOKS == 0 || i >= GamePlay.GAME_BOOKS)
                tblNumbers.add(btnNumber);
            else {
                btnNumber.setVisible(false);
                tblNumbers.removeActor(btnNumber);
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

        tblBiddingNumbers.add(tblOutter);
        //endregion

        tblBiddingNumbers.pad(Value.percentWidth(.20f));

        grpBiddingNumbers.setPosition(Gdx.graphics.getWidth() / 2 - tblBiddingNumbers.getWidth() / 2,
                Gdx.graphics.getHeight() * .41f);
        grpBiddingNumbers.setBounds(grpBiddingNumbers.getX(), grpBiddingNumbers.getY(),
                grpBiddingNumbers.getWidth(), grpBiddingNumbers.getHeight());
        grpBiddingNumbers.addActor(tblBiddingNumbers);
        this.addActor(grpBiddingNumbers);
        grpBiddingNumbers.draw(batch, 1f);
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
