package com.herokuapp.sportstat.sportstat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BasketballGame implements Serializable {
    private String mUsername;
    private long mUserId;
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;
    private int mShotsAttempted;
    private String mStartTime, mEndTime;
    private String mComment;


    private static final long serialVersionUID = 1L;

    public BasketballGame() {

        //Default set the user's id to this user's
        mUsername = "not set";
        mUserId = -1;
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss MMM d yyyy");
        mStartTime = dateFormat.format(Calendar.getInstance().getTime());
        mEndTime = mStartTime;
        mComment = "";

    }

    public static BasketballGame getBasketballGameFromJSONObject(JSONObject j) {
        BasketballGame bg = new BasketballGame();

        String username = j.optString("user_name");
        if (username != null) {
            bg.setUsername(username);
        }

        int userId = j.optInt("user_id", -1);
        if (userId != -1) {
            bg.setUserId(userId);
        }

        bg.setAssists(j.optInt("assists", 0));
        bg.setTwoPoints(j.optInt("two_pointers", 0));
        bg.setThreePoints(j.optInt("three_pointers", 0));

        String startTime = j.optString("start_time");
        if (startTime != null) {
            bg.setStartTime(startTime);
        }

        String endTime = j.optString("end_time");
        if (endTime != null) {
            bg.setEndTime(endTime);
        }

        return bg;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        mUserId = userId;
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

        return mUsername + " played from " + mStartTime + " to " + mEndTime +
                linesep + "Assists: " + mAssists + "  2-Points: " + mTwoPoints +
                "  3-Points: " + mThreePoints;
    }

}
