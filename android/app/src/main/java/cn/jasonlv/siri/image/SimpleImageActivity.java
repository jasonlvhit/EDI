package cn.jasonlv.siri.image;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import cn.jasonlv.siri.Constant;
import cn.jasonlv.siri.R;

/**
 * Created by Administrator on 2015/7/08.
 */
public class SimpleImageActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int frIndex = getIntent().getIntExtra(Constant.Extra.FRAGMENT_INDEX, 0);

        Fragment fr;
        String tag;
        int titleRes;
        switch (frIndex) {
            default:
            case ImageGridFragment.INDEX:
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                    Bundle b = new Bundle();
                    b.putString("KEYWORD", getIntent().getStringExtra("KEYWORD"));
                    fr.setArguments(b);
                }
                titleRes = R.string.ac_name_image_grid;
                break;
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                    Bundle b = new Bundle();
                    b.putStringArrayList("IMAGE_URL", ImageGridFragment.IMAGE_URLS);
                    fr.setArguments(b);
                }
                titleRes = R.string.ac_name_image_pager;
                break;
        }

        setTitle(titleRes);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }
}