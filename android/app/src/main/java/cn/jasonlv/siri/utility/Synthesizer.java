package cn.jasonlv.siri.utility;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.baidu.speechsynthesizer.publicutility.SpeechLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jasonlv.siri.R;


/**
 * Created by Administrator on 2015/7/8.
 */
public class Synthesizer implements SpeechSynthesizerListener {
    private final String LOG_TAG = getClass().getName().toString();

    private SpeechSynthesizer speechSynthesizer;

    Context mContext;

    /** 指定license路径，需要保证该路径的可读写权限 */
    private static final String LICENCE_FILE_NAME = Environment.getExternalStorageDirectory()
            + "/tts/baidu_tts_licence.dat";

    public Synthesizer(Context context){

        mContext = context;

        // 部分版本不需要BDSpeechDecoder_V1
        try {
            System.loadLibrary("BDSpeechDecoder_V1");
        } catch (UnsatisfiedLinkError e) {
            SpeechLogger.logD("load BDSpeechDecoder_V1 failed, ignore");
        }
        System.loadLibrary("bd_etts");
        System.loadLibrary("bds");

        if (!new File(LICENCE_FILE_NAME).getParentFile().exists()) {
            new File(LICENCE_FILE_NAME).getParentFile().mkdirs();
        }
        // 复制license到指定路径
        InputStream licenseInputStream = mContext.getResources().openRawResource(R.raw.trial_license_20150530);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(LICENCE_FILE_NAME);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = licenseInputStream.read(buffer, 0, 1024)) >= 0) {
                SpeechLogger.logD("size written: " + size);
                fos.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                licenseInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 第二个参数当前请传入任意非空字符串即可
        speechSynthesizer = SpeechSynthesizer.newInstance(SpeechSynthesizer.SYNTHESIZER_AUTO,
                mContext, "holder", this);
        // 请替换为在百度开发者中心注册应用得到的 apikey 和 secretkey
        speechSynthesizer.setApiKey("S5WfXaUZrzblHr9AfMquKRn9", "b07abb0e4c123b7f4b87a93ba3b0392b");
        // 设置授权文件路径，LICENCE_FILE_NAME 请替换成实际路径
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE,
                LICENCE_FILE_NAME);
        // TTS 所需的资源文件，可以放在任意可读目录，可以任意改名
        String  ttsDataFilePath  =  mContext.getApplicationInfo().dataDir  +
                "/lib/libbd_tts_female.dat.so";

        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // 以下参数仅对在线引擎生效
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,
                SpeechSynthesizer .SPEAKER_FEMALE);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE,
                SpeechSynthesizer.AUDIO_ENCODE_AMR);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE,
                SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);

    }

    public boolean speak(String text){
        Log.v(LOG_TAG, text);
        int ret = speechSynthesizer.speak(text);

        if (ret != 0) {
            Log.e("fff", "开始合成器失败：" + errorCodeAndDescription(ret));
        } else {
            Log.v("fff", "开始工作，请等待数据...");
        }

        return false;
    }


    public void cancel(){
        speechSynthesizer.cancel();

    }

    @Override
    public void onStartWorking(SpeechSynthesizer speechSynthesizer) {


    }

    @Override
    public void onSpeechStart(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onNewDataArrive(SpeechSynthesizer speechSynthesizer, byte[] bytes, boolean b) {

    }

    @Override
    public void onBufferProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {

    }

    @Override
    public void onSpeechProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {

    }

    @Override
    public void onSpeechPause(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onSpeechResume(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onCancel(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onSynthesizeFinish(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onSpeechFinish(SpeechSynthesizer speechSynthesizer) {

    }

    @Override
    public void onError(SpeechSynthesizer speechSynthesizer, SpeechError speechError) {

    }

    private String errorCodeAndDescription(int errorCode) {
        String errorDescription = "错误码：";
        return errorDescription + "(" + errorCode + ")";
    }
}

