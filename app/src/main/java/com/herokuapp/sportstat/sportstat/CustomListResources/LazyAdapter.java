package com.herokuapp.sportstat.sportstat.CustomListResources;


        import java.util.ArrayList;
        import java.util.HashMap;

        import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.herokuapp.sportstat.sportstat.CustomListResources.ImageLoader;
        import com.herokuapp.sportstat.sportstat.MainActivity;
        import com.herokuapp.sportstat.sportstat.NewsfeedFragment;
        import com.herokuapp.sportstat.sportstat.R;


/**
* Created by johnrigby on 3/6/15.
*
* This class takes its name from and is based on code found here: http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
*/


public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private boolean mIsNewsfeed;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d, boolean isNewsfeed) {
        activity = a;
        data=d;
        mIsNewsfeed = isNewsfeed;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
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
        View vi=convertView;
        if(convertView==null){
           if(mIsNewsfeed){
            vi = inflater.inflate(R.layout.list_row, null);
            }else{
                vi = inflater.inflate(R.layout.history_list_row, null);
            }

        }


        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);


        if((TextView) vi.findViewById(R.id.nameText)!=null) {
            TextView name = (TextView) vi.findViewById(R.id.nameText);
            name.setText(song.get(NewsfeedFragment.KEY_USERNAME));
        }
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView assistsNum = (TextView)vi.findViewById(R.id.assistsNum); // title
        TextView twosNum = (TextView)vi.findViewById(R.id.twosNum); // title
        TextView threesNum = (TextView)vi.findViewById(R.id.threesNum); // title
//        TextView title = (TextView)vi.findViewById(R.id.title); // title
//        TextView title = (TextView)vi.findViewById(R.id.title); // title
        //TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        if((ImageView)vi.findViewById(R.id.list_image)!=null){
            ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
            imageLoader.DisplayImage(song.get(NewsfeedFragment.KEY_THUMB_URL), thumb_image);

        }




        // Setting all values in listview

        title.setText(song.get(NewsfeedFragment.KEY_TITLE));
        assistsNum.setText(song.get(NewsfeedFragment.KEY_ASSISTS));
        twosNum.setText(song.get(NewsfeedFragment.KEY_TWOS));
        threesNum.setText(song.get(NewsfeedFragment.KEY_THREES));
        //artist.setText(song.get(NewsfeedFragment.KEY_ARTIST));
        duration.setText(song.get(NewsfeedFragment.KEY_DURATION));

        return vi;
    }
}