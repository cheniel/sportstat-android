package com.herokuapp.sportstat.sportstat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by DavidHarmon on 2/16/15.
 */
public class BasketballGame implements Serializable{
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;
    private int mShotsAttempted;
    private String mGameTime;
    private String mComment;


    private static final long serialVersionUID = 1L;

    public BasketballGame(){
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
        mGameTime = Calendar.getInstance().getTime().toString();
        mComment = "";
    }



    public int getAssists() {
        return mAssists;
    }

    public void setAssists(int Assists) {
        this.mAssists = Assists;
    }

    public int getTwoPoints() {
        return mTwoPoints;
    }

    public void setTwoPoints(int TwoPoints) {
        this.mTwoPoints = TwoPoints;
    }

    public int getThreePoints() {
        return mThreePoints;
    }

    public void setThreePoints(int ThreePoints) {
        this.mThreePoints = ThreePoints;
    }

    public String getGameTime() {
        return mGameTime;
    }

    public void setGameTime(String mGameTime) {
        this.mGameTime = mGameTime;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public int getShotsAttempted() {
        return mShotsAttempted;
    }

    public void setShotsAttempted(int mShotsAttempted) {
        this.mShotsAttempted = mShotsAttempted;
    }
}
