package cn.jasonlv.siri;

public class Constant {
    public static final String EXTRA_KEY = "key";
    public static final String EXTRA_SECRET = "secret";
    public static final String EXTRA_SAMPLE = "sample";
    public static final String EXTRA_SOUND_START = "sound_start";
    public static final String EXTRA_SOUND_END = "sound_end";
    public static final String EXTRA_SOUND_SUCCESS = "sound_success";
    public static final String EXTRA_SOUND_ERROR = "sound_error";
    public static final String EXTRA_SOUND_CANCEL = "sound_cancel";
    public static final String EXTRA_INFILE = "infile";
    public static final String EXTRA_OUTFILE = "outfile";

    public static final String EXTRA_LANGUAGE = "language";
    public static final String EXTRA_NLU = "nlu";
    public static final String EXTRA_VAD = "vad";
    public static final String EXTRA_PROP = "prop";

    public static final String EXTRA_OFFLINE_ASR_BASE_FILE_PATH = "asr-base-file-path";
    public static final String EXTRA_LICENSE_FILE_PATH = "license-file-path";
    public static final String EXTRA_OFFLINE_LM_RES_FILE_PATH = "lm-res-file-path";
    public static final String EXTRA_OFFLINE_SLOT_DATA = "slot-data";
    public static final String EXTRA_OFFLINE_SLOT_NAME = "name";
    public static final String EXTRA_OFFLINE_SLOT_SONG = "song";
    public static final String EXTRA_OFFLINE_SLOT_ARTIST = "artist";
    public static final String EXTRA_OFFLINE_SLOT_APP = "app";
    public static final String EXTRA_OFFLINE_SLOT_USERCOMMAND = "usercommand";

    public static final int SAMPLE_8K = 8000;
    public static final int SAMPLE_16K = 16000;

    public static final String VAD_SEARCH = "search";
    public static final String VAD_INPUT = "input";

    public static int IMAGE_POSITION = 0;

    private Constant() {
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    public static class Extra {
        public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    }
}
