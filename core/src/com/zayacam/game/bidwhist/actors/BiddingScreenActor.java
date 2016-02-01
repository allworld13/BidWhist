package com.zayacam.game.bidwhist.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

import java.util.stream.Collectors;

public class BiddingScreenActor extends _BidActor implements InputProcessor {

    BidPlayer biddingPlayer = null;
    Group grpKitty, grpSouthPlayer, grpBidding;
    TextButton btnNumber;
    private GamePlay.BidRule_Direction bidDirection = null;
    private int biddingBooks = 0;
    boolean finishedBidding = false;

    //region ctor
    BiddingScreenActor() {
        super();
    }

    public BiddingScreenActor(BidWhistGame bidWhistGame, _BidWhistStage stage) {
        this();
        this.bidWhistGame = bidWhistGame;
        this.stage = stage;

        ConfigureAndShowKitty(310);
        grpSouthPlayer = new Group();
        grpBidding = new Group();
        if (stage != null)
            stage.setKeyboardFocus(this);
    }

    //endregion


    @Override
    public void act(float delta) {
        super.act(delta);

        finishedBidding = bidWhistGame.gamePlay.gamePlayers
                .stream().allMatch(p->p.PlayerHasBidded());

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
            bidWhistGame.DetermineBidWinner();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.draw(Assets.text_background, 0,0, stage.getWidth(), stage.getHeight());

        if (bidWhistGame.gamePlay.ShowKitty || bidWhistGame.gamePlay.SportKitty) {
            grpKitty.draw(batch, 1F);
            //bidWhistGame.gamePlay.ShowKitty = false;
        }

        LoadBidNumberButtons();
        ShowPlayersName(batch);
        ShowPlayersHand(batch, biddingPlayer , 65 );
    }

    private void ConfigureAndShowKitty(float offSet) {
        float P1Width = stage.getWidth()/9.5F;
        float P1Height = stage.getHeight()/4.5F;

        grpKitty = new Group();
        grpKitty.setVisible(true);
        float XPos = 0;
        for (Card c : bidWhistGame.gamePlay.KittyHand) {
            c.PlayingCard().setPosition(XPos, c.PlayingCard().getY() );
            c.PlayingCard().setSize(P1Width, P1Height);
            c.PlayingCard().setUserObject(c);
            grpKitty.addActor(c.PlayingCard());
            XPos += (int)(stage.getWidth() * .045F);
        }
        grpKitty.setBounds(stage.getWidth()/2 - XPos/2,
                stage.getHeight()/2,
                XPos, P1Height );

        grpKitty.setPosition((stage.getWidth()/2 - XPos/2)-30,
                stage.getHeight()-P1Height-offSet);
        stage.addActor(grpKitty);
    }

    private void LoadBidNumberButtons() {
        Table tblNumbers, tblDirection, tblOutter, tblMaster ;

        tblOutter = new Table();

        tblMaster = new Table();
        tblMaster.setName("tblMaster");

        //region main table layout

        //region bidding numbers
        tblNumbers = new Table();
        tblNumbers.setName("tblBidNumbers");

        for (int i = 3; i < 8 ; i++) {
            btnNumber = new TextButton(Integer.toString(i), Assets.Skins);
            btnNumber.setName(Integer.toString(i));
            btnNumber.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getWidth() / 10);
            btnNumber.setBounds(0, 0, btnNumber.getWidth(), btnNumber.getHeight());
            btnNumber.align(Align.center|Align.center);
            btnNumber.pad(0f,30f,0f,30f);
            //btnNumber.addListener(new BidAmountClickListener()
            btnNumber.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Gdx.app.log("BiddingScreenActor", " - " + event.getListenerActor().getName());
                }
            });
            tblNumbers.add(btnNumber);
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
        btnNumber.setName("UpTown");
        btnNumber.pad(0f, 10f, 0f, 10f);
        tblDirection.add(btnNumber);

        btnNumber.addListener(new BidDirectionClicked());

        tblOutter.add(tblDirection);

        btnNumber = new TextButton("Dn", Assets.Skins);
        btnNumber.setName("DownTown");
        btnNumber.pad(0f, 10f, 0f, 10f);

        //btnNumber.addListener(new BidDirectionEvent()
        btnNumber.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log("BiddingScreenActor -> Direction", " - " + event.getListenerActor().getName());
            }
        });

        tblDirection.add(btnNumber);

        btnNumber = new TextButton(" X ", Assets.Skins);
        btnNumber.setName("NoTrump");
        btnNumber.pad(0f, 10f, 0f, 10f);

        //btnNumber.addListener(new BidDirectionEvent()
        btnNumber.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log("BiddingScreenActor -> Direction:", " - " + event.getListenerActor().getName());
            }
        });

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
        setBounds(grpBidding.getX(), grpBidding.getY(), grpBidding.getWidth(), grpBidding.getHeight());
        stage.addActor(grpBidding);

    }

    private void ShowPlayersHand(Batch batch, BidPlayer bidWinner, float offSet) {
        float P1Width = stage.getWidth() / 8.5F;
        float P1Height = stage.getHeight() / 4.5F;

        int XPos = 0;
        int cardIndex = 0;
        if (biddingPlayer == null) return;

        for (Card c : bidWinner.getHand()) {
            if (c.IsAvailable()) {
                c.setGrpIndexName(++cardIndex);
                c.PlayingCard().setPosition(XPos, c.PlayingCard().getY());
                c.PlayingCard().setSize(P1Width, P1Height);
                c.PlayingCard().setUserObject(c);
                grpSouthPlayer.addActor(c.PlayingCard());
                XPos += (int) (stage.getWidth() * .055F);
            }
        }
        grpSouthPlayer.setPosition(stage.getWidth() / 8 + 10, 0);// stage.getHeight()  );
        grpSouthPlayer.draw(batch, 1F);
        stage.addActor(grpSouthPlayer);
    }

    //region IP Overrides

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
    //endregion

    private class PassOrBidPlayClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            boolean validBid = false;

            String btnName =  event.getListenerActor().getName();
            Gdx.app.log("BiddingScreenActor -> Play", " - " + btnName );

            switch (btnName) {
                case "Bid":
                    biddingPlayer.setBidHand_Direction(bidDirection);
                    biddingPlayer.setBidHand_Books(biddingBooks);
                    try {
                        validBid = bidWhistGame.PlayerHasBidded(biddingPlayer);
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

    private class BidDirectionClicked  extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Gdx.app.log("BiddingScreenActor -> Direction", " - " + event.getListenerActor().getName());
            String btnName =  event.getListenerActor().getName();

            bidDirection = GamePlay.BidRule_Direction.valueOf(btnName);
        }
    }
}