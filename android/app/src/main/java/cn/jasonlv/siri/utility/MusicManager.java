package cn.jasonlv.siri.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.Random;

/**
 * Created by Administrator on 2015/7/12.
 */
public class MusicManager {

    Context context;
    BackgroundSound mBackgroundSound = new BackgroundSound();
    String playtitle = null;

    public MusicManager(Context c){
        context = c;
    }

    /**
     * 随机选取本地音乐，播放. 返回播放歌曲的歌曲名.
     * @return
     */
    public String playRandom(){
        playtitle = null;
        mBackgroundSound.execute(getMediaContent());
        return playtitle;
    }


    /**
     * 在min和max间随机生成一个整数.
     * @param min 最小值
     * @param max 最大界
     * @return 选取整数
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


    /**
     * 随机选取一首本地音乐，返回相应的资源URI.
     * @return
     */
    public Uri getMediaContent(){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        int count = cursor.getCount();

        // 选取随机数.
        int pong = randInt(0, count);
        int i = 0;

        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                String thisId = cursor.getString(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                if (i == pong) {
                    Uri playableUri
                            = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                    // ...process entry...
                    playtitle = thisTitle;
                    return playableUri;
                }
                i++;
            } while (cursor.moveToNext());
        }
        return null;
    }

    /**
     * 音乐播放任务.接受相应的URI， 播放歌曲
     */
    public class BackgroundSound extends AsyncTask<Uri, Void, Void> {

        @Override
        protected Void doInBackground(Uri... params) {


            MediaPlayer player = MediaPlayer.create(context, params[0]);
            player.setLooping(true); // Set looping
            player.setVolume(100, 100);
            player.start();

            return null;
        }

    }
}
