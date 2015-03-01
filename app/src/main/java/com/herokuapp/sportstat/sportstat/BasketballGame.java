package com.herokuapp.sportstat.sportstat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DavidHarmon on 2/16/15.
 */
public class BasketballGame implements Serializable{
    private long mUserId;
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;
    private int mShotsAttempted;
    private String mStartTime, mEndTime;
    private String mComment;


    private static final long serialVersionUID = 1L;
    private SharedPreferences mPreferences;

    public BasketballGame(Context context){

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Default set the user's id to this user's
        mUserId = mPreferences.getInt(Globals.USER_ID, 0);
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss MMM d yyyy");
        mStartTime = dateFormat.format(Calendar.getInstance().getTime());


        mEndTime = mStartTime;
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

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
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


    @Override
    public String toString() {
        String linesep = System.getProperty("line.separator");



        StringBuilder sb = new StringBuilder();
        sb.append(getUserName(mUserId, Globals.context)+" played from "+mStartTime+" to "+mEndTime+linesep
        + "Assists: "+mAssists+"  2-Points: "+mTwoPoints+ "  3-Points: "+mThreePoints);

        return(sb.toString());

    }


    private String getUserName(long id, Context context){
        String userNameStr;


        //If the id matches the user's return my username. Else, lookup in the cloud

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int uId = mPreferences.getInt(Globals.USER_ID, 0);
        if(id == uId){
            userNameStr =  mPreferences.getString(Globals.USERNAME, "");
        }else{
            userNameStr = "IDZ DONT MATCH";
            //GET USERNAME FROM CLOUD
        }


        return userNameStr;


    }
}
