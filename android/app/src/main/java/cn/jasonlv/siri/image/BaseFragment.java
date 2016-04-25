package cn.jasonlv.siri.image;

import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * @author Jason Lyu (jasonlyuhit@outlook.com)
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

}