package com.herokuapp.sportstat.sportstat;

/**
 * Created by DavidHarmon on 2/16/15.
 */
public class GameBasketball {
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;

    public GameBasketball(){
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
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
