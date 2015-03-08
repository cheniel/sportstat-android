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
        import com.herokuapp.sportstat.sportstat.MainActivity;
        import com.herokuapp.sportstat.sportstat.NewsfeedFragment;
        import com.herokuapp.sportstat.sportstat.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;


/**
* Created by johnrigby on 3/6/15.
*
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


        Log.d(TAG, "WHY");

        song = data.get(position);


        if(!mIsLeaderBoard) {
            if ((TextView) vi.findViewById(R.id.nameText) != null) {
                TextView name = (TextView) vi.findViewById(R.id.nameText);
                name.setText(song.get(NewsfeedFragment.KEY_USERNAME));
            }
            TextView title = (TextView) vi.findViewById(R.id.title); // title
            TextView assistsNum = (TextView) vi.findViewById(R.id.assistsNum); // title
            TextView twosNum = (TextView) vi.findViewById(R.id.twosNum); // title
            TextView threesNum = (TextView) vi.findViewById(R.id.threesNum); // title
//        TextView title = (TextView)vi.findViewById(R.id.title); // title
//        TextView title = (TextView)vi.findViewById(R.id.title); // title
            //TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
            TextView duration = (TextView) vi.findViewById(R.id.duration); // duration
            if ((ImageView) vi.findViewById(R.id.list_image) != null) {
                ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
                imageLoader.DisplayImage(song.get(NewsfeedFragment.KEY_THUMB_URL), thumb_image);

            }


            // Setting all values in listview

            title.setText(song.get(NewsfeedFragment.KEY_TITLE));
            assistsNum.setText(song.get(NewsfeedFragment.KEY_ASSISTS));
            twosNum.setText(song.get(NewsfeedFragment.KEY_TWOS));
            threesNum.setText(song.get(NewsfeedFragment.KEY_THREES));
            //artist.setText(song.get(NewsfeedFragment.KEY_ARTIST));
            duration.setText(song.get(NewsfeedFragment.KEY_DURATION));

        }else {

            Log.d(TAG, "deez nuts");
           getStatScore(vi);


        }

        return vi;
    }

    private void getStatScore(final View v) {
        final int userId = Integer.parseInt(song.get(NewsfeedFragment.KEY_ID));

        if (userId == -1) {
            Log.d(TAG, "preference error");
            return;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String newsfeedString = CloudUtilities.getJSON(
                        mContext.getString(R.string.sportstat_url) + "users/" + userId
                                + "/basketball_games.json");

                Log.d(TAG, newsfeedString);

                try {
                    final JSONArray newsfeed = new JSONArray(newsfeedString);

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {

                                    Log.d(TAG, "deez nuts 27");
                                    mBasketballGames = getBasketballGameListFromJSONArray(newsfeed);

                                    findStatScore(mBasketballGames);

                                    TextView scoreNum = (TextView) v.findViewById(R.id.stat_score_num);
                                    TextView userNameText = (TextView) v.findViewById(R.id.nameText);

                                    //String linesep = System.getProperty("line.separator");
                                    String userName = song.get(NewsfeedFragment.KEY_USERNAME);

                                    scoreNum.setText(mStatScore);
                                    userNameText.setText(userName+"  |");

                                }
                            }
                    );


                    return "success";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "failure";
            }

        }.execute();

    }


        private ArrayList<BasketballGame> getBasketballGameListFromJSONArray(JSONArray newsfeed) {
            ArrayList<BasketballGame> feed = new ArrayList<>();

            for (int i = 0; i < newsfeed.length(); i++) {
                try {
                    JSONObject basketballObject = newsfeed.getJSONObject(i);
                    feed.add(feed.size()-i,
                            BasketballGame.getBasketballGameFromJSONObject(
                                    basketballObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return feed;
        }


        //Take the array of basketball games stored in the user's history and calculate StatScore
    private void findStatScore(ArrayList<BasketballGame> mBasketballGames) {

        double avgAssists, avgTwos, avgThrees;

        int assistsSum = 0;
        int twosSum = 0;
        int threesSum = 0;
        int count = 0;
        //TextView avgTextView = (TextView) getView().findViewById(R.id.avg_stats_text_view);

        for(BasketballGame game : mBasketballGames){
            count++;
            assistsSum+=game.getAssists();
            twosSum+=game.getTwoPoints();
            threesSum+=game.getThreePoints();
        }

        avgAssists = assistsSum/((double)count);
        avgTwos = twosSum/((double)count);
        avgThrees = threesSum/((double)count);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");


        mStatScore = decimalFormat.format(avgAssists+avgTwos+avgThrees);


        String linesep = System.getProperty("line.separator");
        //avgTextView.setText("Avg Assists: "+decimalFormat.format(avgAssists)+linesep+"Avg 2-Pointer's: "
        //+decimalFormat.format(avgTwos)+linesep+"Avg 3-Pointer's: "+decimalFormat.format(avgThrees));

    }

    }
