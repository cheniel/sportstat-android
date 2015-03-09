package com.herokuapp.sportstat.sportstat;

import android.content.res.Resources;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BasketballGame implements Serializable {
    private static final String TAG ="aah" ;
    private String mUsername;
    private long mUserId;
    private int mAssists;
    private int mTwoPoints;
    private int mThreePoints;
    private int mShotsAttempted;
    private int mPossessions;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mComment;

    private double mDistance;
    private long mDuration;

    private double mFirstLat;
    private double mFirstLon;
    private String mGeocodedString;

    private static final long serialVersionUID = 1L;

    public BasketballGame() {

        //Default set the user's id to this user's
        mUsername = "not set";
        mUserId = -1;
        mAssists = 0;
        mTwoPoints = 0;
        mThreePoints = 0;
        mStartTime = Calendar.getInstance(TimeZone.getTimeZone("Z"));
        mEndTime = mStartTime;
        mComment = "";

    }

    public JSONObject getJSONObject() {
        JSONObject basketballGame = new JSONObject();

        try {
            basketballGame.put("username", mUsername);
            basketballGame.put("user_id", mUserId);
            basketballGame.put("assists", mAssists);
            basketballGame.put("two_pointers", mTwoPoints);
            basketballGame.put("three_pointers", mThreePoints);
            basketballGame.put("start_time", getStartTimeISOString());
            basketballGame.put("end_time", getEndTimeISOString());
            basketballGame.put("possessions", mPossessions);
            basketballGame.put("distance", mDistance);
            basketballGame.put("duration", mDuration);
            basketballGame.put("shots_attempted", mShotsAttempted);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return basketballGame;
    }

    private String getEndTimeISOString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(mEndTime.getTime());
    }

    private String getStartTimeISOString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(mStartTime.getTime());
    }

    public static BasketballGame getBasketballGameFromJSONObject(JSONObject j) {
        BasketballGame bg = new BasketballGame();

        String username = j.optString("username");
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

    private void setEndTime(String endTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        try {
            Date date = df.parse(endTime);
            mEndTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setStartTime(String startTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        try {
            Date date = df.parse(startTime);
            mStartTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long Duration) {
        this.mDuration = Duration;
    }

    @Override
    public String toString() {
        String linesep = System.getProperty("line.separator");

       // DateFormat dateFormat = new SimpleDateFormat("HH:MM:SS");
        String startTimeStr = getStartTimeString().substring(0, 9);
        Log.d(TAG, getStartTimeString());
        Log.d(TAG, startTimeStr);



        return mUsername + " played from " + getPrettyTime() +
                linesep + Html.fromHtml("<b>" + "Assists: " + "</b>") + mAssists + "  2-Points: " + mTwoPoints +
                "  3-Points: " + mThreePoints;
    }

    public String toStringForNewsFeed() {
        return " played " + getPrettyTime();

    }

    public String toStringForHistory(){
        return getPrettyTimeForHistory();
    }


    private String getStartTimeString() {
        return String.format("%02d", mStartTime.get(Calendar.HOUR)) + ":"
                + String.format("%02d", mStartTime.get(Calendar.MINUTE)) + ":"
                + String.format("%02d", mStartTime.get(Calendar.SECOND)) + " "
                + mStartTime.getDisplayName(Calendar.MONTH, Calendar.LONG,
                new Locale("English")) + " "
                + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                + mStartTime.get(Calendar.YEAR);
    }

    public String getPrettyTime(){
        String dateStr;
        Calendar cur = Calendar.getInstance();
        if((mStartTime.get(Calendar.MONTH) == cur.get(Calendar.MONTH))&&
                (mStartTime.get(Calendar.DAY_OF_MONTH)==cur.get(Calendar.DAY_OF_MONTH))&&
                (mStartTime.get(Calendar.YEAR)==cur.get(Calendar.YEAR)) ){
            dateStr = "today";
        }else{
            dateStr = "on "+ String.format(mStartTime.getDisplayName(Calendar.MONTH, Calendar.LONG,
                    new Locale("English")) + " "
                    + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                    + mStartTime.get(Calendar.YEAR));
        }

        return dateStr;


    }

    public String getPrettyTimeForHistory(){
                return "Game played "+String.format(mStartTime.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        new Locale("English")) + " "
                        + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                        + mStartTime.get(Calendar.YEAR))+", from "+String.format("%02d", mStartTime.get(Calendar.HOUR)) + ":"
                + String.format("%02d", mStartTime.get(Calendar.MINUTE))
                + " to "+ String.format("%02d", mEndTime.get(Calendar.HOUR)) + ":"
                + String.format("%02d", mEndTime.get(Calendar.MINUTE));
    }




    private String getEndTimeString() {
        return String.format("%02d", mEndTime.get(Calendar.HOUR)) + ":"
                + String.format("%02d", mEndTime.get(Calendar.MINUTE)) + ":"
                + String.format("%02d", mEndTime.get(Calendar.SECOND)) + " "
                + mEndTime.getDisplayName(Calendar.MONTH, Calendar.LONG,
                new Locale("English")) + " "
                + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                + mEndTime.get(Calendar.YEAR);
    }

    public String getTimeString(){

        return ""+mEndTime.getDisplayName(Calendar.MONTH, Calendar.LONG,
                new Locale("English")) + " "
                + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                + mEndTime.get(Calendar.YEAR);
    }

    public int getPossessions() {
        return mPossessions;
    }

    public void setPossessions(int Possessions) {
        this.mPossessions = Possessions;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double Distance) {
        this.mDistance = Distance;
    }

    public double getFirstLat() {
        return mFirstLat;
    }

    public void setFirstLat(double mFirstLat) {
        this.mFirstLat = mFirstLat;
    }

    public double getFirstLon() {
        return mFirstLon;
    }

    public void setFirstLon(double mFirstLon) {
        this.mFirstLon = mFirstLon;
    }

    public String getGeocodedString() {
        return mGeocodedString;
    }

    public void setGeocodedString(String mGeocodedString) {
        this.mGeocodedString = mGeocodedString;
    }
}
