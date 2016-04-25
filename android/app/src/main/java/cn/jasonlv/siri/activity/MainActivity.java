package cn.jasonlv.siri.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.jasonlv.siri.Constant;
import cn.jasonlv.siri.R;
import cn.jasonlv.siri.utility.Synthesizer;
import cn.jasonlv.siri.fragment.DoubanFragment;
import cn.jasonlv.siri.fragment.NavigationDrawerFragment;
import cn.jasonlv.siri.fragment.SearchFragment;
import cn.jasonlv.siri.fragment.TextFragment;
import cn.jasonlv.siri.fragment.TodoEditorFragment;
import cn.jasonlv.siri.fragment.WeatherFragment;
import cn.jasonlv.siri.image.ImageGridFragment;
import cn.jasonlv.siri.image.SimpleImageActivity;
import cn.jasonlv.siri.utility.ContactsManager;
import cn.jasonlv.siri.utility.LocationDetactor;
import cn.jasonlv.siri.utility.MusicManager;
import cn.jasonlv.siri.utility.NativePackageManager;
import cn.jasonlv.siri.utility.SymaticParser;


public class MainActivity extends Activity implements RecognitionListener, TextFragment.OnFragmentInteractionListener , NavigationDrawerFragment.NavigationDrawerCallbacks{
    private static final String TAG = "Sdk2Api";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public void onNavigationDrawerItemSelected(int position) {

        if(position == 1){
            Intent intent = new Intent(this, TodoActivity.class);
            startActivity(intent);
        }
        if(position == 0){
            FrameLayout fragmentContainer = new FrameLayout(this);
            fragmentContainer.setId(fragmentConatainerId);

            container.addView(fragmentContainer);

            getFragmentManager().beginTransaction()
                    .replace(fragmentConatainerId, TodoEditorFragment.newInstance("a", "a"))
                    .commit();

            fragmentConatainerId++;
        }
        if(position == 2){
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        }

        if(position == 3){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    private static final int REQUEST_UI = 1;
    //private TextView txtLog;
    private ActionButton btn;

    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private SpeechRecognizer speechRecognizer;
    private int status = STATUS_None;
    private TextView txtResult;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;

    private NativePackageManager mPackageManager;
    private Synthesizer mSynthesizer;
    private TextFragment mTextFragment;

    private ContactsManager mContactManager;
    LocationDetactor.LocationInfo info;
    private LocationDetactor detactor;

    private MusicManager musicManager;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private LinearLayout container;
    private ScrollView scrollView;

    public static int fragmentConatainerId = 111111;

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 初始化本地音乐管理器， 管理本地音乐的搜索和播放
        musicManager = new MusicManager(getApplicationContext());

        // 初始化图片加载器，设置图片显示参数等
        initImageLoader(getApplicationContext());

        /* get the installed package list*/
        // 初始化本地应用管理器
        mPackageManager = new NativePackageManager(getApplicationContext());

        // 设置音频控制流
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 语音播放工具初始化
        mSynthesizer = new Synthesizer(getApplicationContext());

        //mSynthesizer.speak("欢迎使用EDI");

        // 本地通讯录工具 初始化
        mContactManager = new ContactsManager(getApplicationContext());

        mContactManager.getContactList();

        // 本地定位工具初始化
        detactor = new LocationDetactor(getApplicationContext());
        info = detactor.getLocationInfo();

        Log.d("location info", info.lat + ", " + info.lon);

        //for(Object o : mPackageManager.getPackageList()){
        //    System.out.println(o.toString());

        setContentView(R.layout.sdk2_api);

        //txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (ActionButton) findViewById(R.id.btn);

        // 语音识别工具初始化
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));

        speechRecognizer.setRecognitionListener(this);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean api = sp.getBoolean("api", false);
                if (api) {
                    switch (status) {
                        case STATUS_None:
                            start();
                            //btn.setText("取消");
                            status = STATUS_WaitingReady;
                            break;
                        case STATUS_WaitingReady:
                            cancel();
                            status = STATUS_None;
                            //btn.setText("开始");
                            break;
                        case STATUS_Ready:
                            cancel();
                            status = STATUS_None;
                            //btn.setText("开始");
                            break;
                        case STATUS_Speaking:
                            stop();
                            status = STATUS_Recognition;
                            //btn.setText("识别中");
                            break;
                        case STATUS_Recognition:
                            cancel();
                            status = STATUS_None;
                            //btn.setText("开始");
                            break;
                    }
                } else {
                    start();
                }
            }
        });

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        container = (LinearLayout) findViewById(R.id.container);
        scrollView = (ScrollView)findViewById(R.id.scroll);

        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }

    /**
     * 初始化设置
     * @param intent
     */
    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.getBoolean(Constant.EXTRA_INFILE, false)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            int sample = Constant.SAMPLE_8K;
            if (null != tmp && !"".equals(tmp)) {
                sample = Integer.parseInt(tmp);
            }
            if (sample == Constant.SAMPLE_8K) {
                intent.putExtra(Constant.EXTRA_INFILE, "res:///com/baidu/android/voicedemo/8k_test.pcm");
            } else if (sample == Constant.SAMPLE_16K)  {
                intent.putExtra(Constant.EXTRA_INFILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
            }
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private void start() {
        //txtLog.setText("");
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {

            String args = sp.getString("args", "");
            if (null != args) {
                print("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);
        if (api) {
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            startActivityForResult(intent, REQUEST_UI);
        }

        mSynthesizer.cancel();

    }

    private void stop() {
        speechRecognizer.stopListening();
        print("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        print("点击了“取消”");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        status = STATUS_Speaking;
        //btn.setText("说完了");
        print("检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        print("检测到用户的已经停止说话");
        //btn.setText("识别中");
    }

    @Override
    public void onError(int error) {
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
        //btn.setText("开始");
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        Log.e(LOG_TAG, json_res);


        View inputPanel = getLayoutInflater().inflate(R.layout.input_layout, null);
        TextView inputTextView = (TextView)inputPanel.findViewById(R.id.input_text);
        inputTextView.setText(nbest.get(0));
        inputPanel.setFocusable(true);
        inputPanel.setFocusableInTouchMode(true);
        container.addView(inputPanel);

        //View fragmentContainer = getLayoutInflater().inflate(R.layout.fragment_container_layout, null);
        //container.addView(fragmentContainer);

        FrameLayout fragmentContainer = new FrameLayout(this);
        fragmentContainer.setId(fragmentConatainerId);
        fragmentContainer.setFocusable(true);
        fragmentContainer.setFocusableInTouchMode(true);

        container.addView(fragmentContainer);
        onProcessingResult(nbest, json_res, fragmentConatainerId);
        fragmentConatainerId++;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }



    public void onProcessingResult(ArrayList<String> nbest, String json_res, int fragmentId){
        Intent intent = mPackageManager.getInstalledIntentByName(nbest.get(0));
        ContactsManager.Contact contact = mContactManager.getContactInfo(nbest);

        if(null != contact){
            Log.e("f**k", contact.name+ " : " +contact.number);

            getFragmentManager().beginTransaction()
                    .replace(fragmentId, TextFragment.newInstance(contact.name + "\n"
                            + contact.number + "\n", null))
                    .commit();

        }

        /**
         * 应用：
         *
         * 打开微信
         * 打开应用微信
         * 微信
         */

        else if(intent != null){

            //txtResult.setText("打开应用" + nbest.get(0) + "...");
            mSynthesizer.speak("打开应用"+nbest.get(0));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            getFragmentManager().beginTransaction()
                    .replace(fragmentId, TextFragment.newInstance("应用："+nbest.get(0), null))
                    .commit();

            startActivity(intent);
            //txtResult.setText("应用: " + nbest.get(0));
        }
        else if(nbest.get(0).startsWith("打开应用")){
            intent = mPackageManager.getInstalledIntentByName(nbest.get(0).substring(4));
            if(intent != null){
                getFragmentManager().beginTransaction()
                        .replace(fragmentId, TextFragment.newInstance(nbest.get(0).substring(2), null))
                        .commit();
                startActivity(intent);
            }
        }
        else if(nbest.get(0).startsWith("打开")){
            intent = mPackageManager.getInstalledIntentByName(nbest.get(0).substring(2));
            if(intent != null){
                startActivity(intent);
            }

        }
        else if(nbest.get(0).contains("介绍一下你自己")){
            mSynthesizer.speak("我是你的大宝贝");
            getFragmentManager().beginTransaction()
                    .replace(fragmentId, TextFragment.newInstance("我是你的大宝贝", null))
                    .commit();
        }

        else if(nbest.get(0).startsWith("给")){
            if(nbest.get(0).endsWith("打电话")){

                String number = mContactManager.getContactNumber(nbest.get(0).substring(1, nbest.get(0).length()-3));
                if(number != null) {

                    getFragmentManager().beginTransaction()
                            .replace(fragmentId, TextFragment.newInstance("Call: "+number, null))
                            .commit();

                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(i);
                }else{
                    getFragmentManager().beginTransaction()
                            .replace(fragmentId, TextFragment.newInstance("没有这个人哦~", null))
                            .commit();
                    mSynthesizer.speak("没有这个人哦");

                }
            } else if(nbest.get(0).endsWith("发短信")){
                getFragmentManager().beginTransaction()
                        .replace(fragmentId, TextFragment.newInstance("", null))
                        .commit();

            }

        }

        /**
         * 定时应用
         *
         *
         */
        else if(SymaticParser.isAlarmEvents(nbest.get(0))){
            SymaticParser.extractAlarmEvents(nbest.get(0), getApplicationContext());
        }
        else if(SymaticParser.isAboutTodo(nbest.get(0))){
            try {
                SymaticParser.extractTodoItem(nbest.get(0), getBaseContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * 放一首歌
         */
        else if(nbest.get(0).contains("放一首歌")){
            String songname = musicManager.playRandom();
            if(songname != null){
                getFragmentManager().beginTransaction()
                        .replace(fragmentId, TextFragment.newInstance(songname, null))
                        .commit();
            }

        }
        /**
         * 图片搜索：
         *
         * 搜索周杰伦的图片
         * 搜索美女图片
         * 美女图片
         *
         *
         */

        else if(nbest.get(0).endsWith("的图片") && nbest.get(0).startsWith("搜索")){
            String keyword = nbest.get(0).substring(2, nbest.get(0).length() - 3);
            Intent intent1 = new Intent(this, SimpleImageActivity.class);
            intent1.putExtra(Constant.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
            intent1.putExtra("KEYWORD", keyword);
            startActivity(intent1);
        }
        else if(nbest.get(0).endsWith("图片") && nbest.get(0).startsWith("搜索")){
            String keyword = nbest.get(0).substring(2, nbest.get(0).length() - 2);
            Intent intent1 = new Intent(this, SimpleImageActivity.class);
            intent1.putExtra(Constant.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
            intent1.putExtra("KEYWORD", keyword);
            startActivity(intent1);
        }
        else if(nbest.get(0).endsWith("图片")){
            String keyword = nbest.get(0).substring(0, nbest.get(0).length() - 2);
            Intent intent1 = new Intent(this, SimpleImageActivity.class);
            intent1.putExtra(Constant.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
            intent1.putExtra("KEYWORD", keyword);
            startActivity(intent1);
        }

        /**
         * 待办事项：
         *
         * 添加待办事项
         * 查看待办事项
         * 待办事项
         */

        else if(nbest.get(0).startsWith("添加待办事")){
            getFragmentManager().beginTransaction()
                    .replace(fragmentId, TodoEditorFragment.newInstance("a", "a"))
                    .commit();
        }
        else if(nbest.get(0).startsWith("查看待办事") || nbest.get(0).contains("待办事项")){
            Intent in = new Intent(this, TodoActivity.class);
            startActivity(in);
        }

        /**
         * 导航:
         *
         * 从哈工大到火车站怎么走
         */

        else if(SymaticParser.isAboutMapIntent(nbest.get(0))){
            ArrayList<String> ends = SymaticParser.extractMapEnds(nbest.get(0));

            //移动APP调起Android百度地图, 如果没有百度地图安装，打开网页.
            try {

                /*

                "intent://map/direction?origin=latlng:34.264642646862,108.95108518068
                |name:我家&destination=大雁塔&mode=driving&region=西安
                &src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"

                45.75， 126.63
                 */

                intent = Intent.getIntent(
                        "intent://map/direction?origin=latlng:45.75,126.63|name:"
                        + ends.get(0)+
                        "&destination="
                        + ends.get(1)+
                        "&mode=driving&region=哈尔滨&src=EDI#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            } catch (Exception e) {
                e.printStackTrace();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://api.map.baidu.com/direction?" +
                                "|name:"
                                +
                                ends.get(0)
                                +"&destination="
                                +ends.get(1)
                                +"&mode=driving&output=html&src=yourCompanyName|yourAppName"));
                startActivity(browserIntent);
            }
            startActivity(intent); //启动调用
        }

        /**
         * 天气和翻译.
         *
         */
        else if(nbest.get(0).contains("天气")){
            getFragmentManager().beginTransaction()
                    .replace(fragmentId, WeatherFragment.newInstance(info.lat, info.lon))
                    .commit();
        }
        else if(nbest.get(0).startsWith("翻译")){
            new TranslateGetter().execute(nbest.get(0).substring(2));
        }


        /**
         * 搜索
         *
         * 搜索刘翔
         * 周杰伦是谁
         * 或 周杰伦
         */

        else if(nbest.get(0).endsWith("是谁")){
            getFragmentManager().beginTransaction()
                    .replace(fragmentId, SearchFragment.newInstance(nbest.get(0).substring(0, nbest.get(0).length() - 2)))
                    .commit();
        }

        else if(nbest.get(0).startsWith("搜索")){
            getFragmentManager().beginTransaction()
                    .replace(fragmentId, SearchFragment.newInstance(nbest.get(0).substring(2)))
                    .commit();
        }

        /**
         *  语义化搜索
         *
         *  盗墓笔记
         *  围城
         *  让子弹飞
         *  周杰伦
         *  七里香
         *
         */
        else {

            try {
                String videoname = null;
                String bookname = null;
                String musicname = null;
                String person = null;
                videoname = SymaticParser.isMovieInfo(json_res);
                bookname = SymaticParser.isNovelInfo(json_res);
                musicname = SymaticParser.isMusicInfo(json_res);

                person = SymaticParser.isPersonInfo(json_res);

                if(videoname != null || bookname != null || musicname != null){
                    getFragmentManager().beginTransaction()
                            .replace(fragmentId, DoubanFragment.newInstance(videoname, musicname, bookname))
                            .commit();
                }
                else if(person != null){
                    getFragmentManager().beginTransaction()
                            .replace(fragmentId, SearchFragment.newInstance(person))
                            .replace(fragmentId, SearchFragment.newInstance(person))
                            .commit();
                }

                /**
                 * 图灵机器人应答.
                 */

                else{
                    new RobotGetter().execute(nbest.get(0));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                ;
            }

        }
            /*else {
                new WikiGetter().execute(nbest.get(0));
            }
            */
    }


    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            //txtResult.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    private void print(String msg) {
        //txtLog.append(msg + "\n");
        //ScrollView sv = (ScrollView) txtLog.getParent();
        //sv.smoothScrollTo(0, 1000000);
        Log.d(TAG, "----" + msg);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * 翻译，有道翻译.
     */
    public class TranslateGetter extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = TranslateGetter.class.getName();
        private final String mTranslateUrl = "http://jasonlv.cn/translate/";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null) {
                s = s.replace("<br>", "");
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, TextFragment.newInstance(s, null))
                        .commit();
            }
            mSynthesizer.speak(s);
        }

        protected String doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL(mTranslateUrl+query)
                        .openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.connect();
                inputStream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        inputStream, "UTF-8"));
                StringBuffer outBuffer = new StringBuffer();
                String string;
                while ((string = br.readLine()) != null)
                    outBuffer.append(string);
                String content = outBuffer.toString();
                inputStream.close();
                Log.v(LOG_TAG, content);
                JSONObject jc = new JSONObject(content);
                return jc.getJSONArray("translation").getString(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * 抓取图灵机器人应答.
     */
    public class RobotGetter extends AsyncTask<String, Void, String> {
        private final String LOG_TAG;
        private final String mRobotUrl = "http://jasonlv.cn/robot/";
        {
            LOG_TAG = RobotGetter.class.getName().toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null) {
                try{
                    s = s.replace("<br>", "");
                    s = s.replace("图灵机器人", "EDI语音助手");
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, TextFragment.newInstance(s, null))
                            .commit();
                    mSynthesizer.speak(s);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "网络状况不佳...",
                            Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "网络状况不佳...",
                        Toast.LENGTH_LONG).show();
            }

        }

        protected String doInBackground(String... params) {
            Log.v(LOG_TAG, params[0]);
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                String query = URLEncoder.encode(params[0], "utf-8");
                conn = (HttpURLConnection) new URL("http://jasonlv.cn/robot/"+query)
                            .openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.connect();
                inputStream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        inputStream, "UTF-8"));
                StringBuffer outBuffer = new StringBuffer();
                String string;
                while ((string = br.readLine()) != null)
                    outBuffer.append(string);
                String content = outBuffer.toString();
                inputStream.close();
                Log.v(LOG_TAG, content);
                JSONObject jc = new JSONObject(content);
                return jc.getString("text");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
