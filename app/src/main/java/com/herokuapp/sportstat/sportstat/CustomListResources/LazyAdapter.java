package com.herokuapp.sportstat.sportstat.CustomListResources;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.herokuapp.sportstat.sportstat.BasketballGame;
import com.herokuapp.sportstat.sportstat.CloudUtilities;
import com.herokuapp.sportstat.sportstat.CustomListResources.ImageLoader;
import com.herokuapp.sportstat.sportstat.Globals;
import com.herokuapp.sportstat.sportstat.LeaderBoardFragment;
import com.herokuapp.sportstat.sportstat.MainActivity;
import com.herokuapp.sportstat.sportstat.NewsfeedFragment;
import com.herokuapp.sportstat.sportstat.R;
import com.herokuapp.sportstat.sportstat.SettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by johnrigby on 3/6/15.
 * <p/>
 * This class takes its name from and is based on code found here: http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
 */


public class LazyAdapter extends BaseAdapter {

    private static final String TAG = "LazyAdapter";
    private Activity activity;
    private boolean mIsNewsfeed, mIsLeaderBoard;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    HashMap<String, String> song;
    private Context mContext;
    private ArrayList<BasketballGame> mBasketballGames;
    private String mStatScore;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d, boolean isNewsfeed, boolean isLeaderBoard, Context c) {
        activity = a;
        data = d;
        mIsNewsfeed = isNewsfeed;
        mIsLeaderBoard = isLeaderBoard;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
        song = new HashMap<String, String>();
        mContext = c;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            if (mIsNewsfeed) {
                vi = inflater.inflate(R.layout.list_row, null);
            } else {
                if (mIsLeaderBoard) {
                    vi = inflater.inflate(R.layout.leader_board_list_row, null);
                } else {
                    vi = inflater.inflate(R.layout.history_list_row, null);
                }
            }

        }

        song = data.get(position);


        if (!mIsLeaderBoard) {
            if ((TextView) vi.findViewById(R.id.nameText) != null) {
                TextView name = (TextView) vi.findViewById(R.id.nameText);
                name.setText(song.get(NewsfeedFragment.KEY_USERNAME));
            }
            TextView title = (TextView) vi.findViewById(R.id.title); // title
            TextView assistsNum = (TextView) vi.findViewById(R.id.assistsNum); // title
            TextView twosNum = (TextView) vi.findViewById(R.id.twosNum); // title
            TextView threesNum = (TextView) vi.findViewById(R.id.threesNum); // title

            TextView duration = (TextView) vi.findViewById(R.id.duration); // duration
            if ((ImageView) vi.findViewById(R.id.list_image) != null) {
                //Log.d(TAG, "WHY COMP WHY: " + song.get(NewsfeedFragment.KEY_THUMB_URL));
                ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);

                try {
                    setImage(Integer.parseInt(song.get(NewsfeedFragment.KEY_THUMB_URL)), thumb_image);
                } catch (Exception e) {
                    setImage(9, thumb_image);
                }

            }


            // Setting all values in listview

            title.setText(song.get(NewsfeedFragment.KEY_TITLE));
            assistsNum.setText(song.get(NewsfeedFragment.KEY_ASSISTS));
            twosNum.setText(song.get(NewsfeedFragment.KEY_TWOS));
            threesNum.setText(song.get(NewsfeedFragment.KEY_THREES));
            //artist.setText(song.get(NewsfeedFragment.KEY_ARTIST));
            duration.setText(song.get(NewsfeedFragment.KEY_DURATION));

        } else {

            TextView scoreNum = (TextView) vi.findViewById(R.id.stat_score_num);
            TextView userNameText = (TextView) vi.findViewById(R.id.nameText);
            ImageView userProf = (ImageView) vi.findViewById(R.id.list_image);


            String userName = song.get(LeaderBoardFragment.KEY_USERNAME);
            String statScore = song.get(LeaderBoardFragment.KEY_STATSCORE);
            String imgId = song.get(LeaderBoardFragment.KEY_THUMB_URL);
            if(imgId == null) imgId = "9";

            try {
                setImage(Integer.parseInt(song.get(NewsfeedFragment.KEY_THUMB_URL)), userProf);
            } catch (Exception e) {
                setImage(9, userProf);
            }

            scoreNum.setText(statScore);
            userNameText.setText(userName);

        }

        return vi;
    }


    public static void setImage(int pos, ImageView imageView) {

        int imageId;
        switch (pos) {
            case 0:
                imageId = R.drawable.sample_1;
                break;
            case 1:
                imageId = R.drawable.sample_2;
                break;
            case 2:
                imageId = R.drawable.sample_3;
                break;
            case 3:
                imageId = R.drawable.sample_4;
                break;
            case 4:
                imageId = R.drawable.sample_5;
                break;
            case 5:
                imageId = R.drawable.sample_6;
                break;
            default:
                imageId = R.drawable.blank_profile;
                break;

        }
        imageView.setImageResource(imageId);
    }


}
