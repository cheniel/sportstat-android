package com.herokuapp.sportstat.sportstat.CustomListResources;

/**
 * Created by johnrigby on 3/6/15.
 *
 *
 */

import java.io.File;
import android.content.Context;
/*
This class takes its name from and is based on code found here: http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
 */

public class FileCache {

    private File cacheDir;

    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}
