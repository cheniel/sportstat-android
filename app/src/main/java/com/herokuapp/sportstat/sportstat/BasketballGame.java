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
    private Date mGameTime;

    private static final long serialVersionUID = 1L;

    public BasketballGame(){
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
        mGameTime = Calendar.getInstance().getTime();
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
}
