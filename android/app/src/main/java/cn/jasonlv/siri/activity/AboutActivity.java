package cn.jasonlv.siri.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import cn.jasonlv.siri.R;

/**
 * Created by Administrator on 2015/7/14.
 */
public class AboutActivity extends Activity {
    private ImageView foundDevice;

    private TextView author1;
    private TextView author2;
    private TextView author3;
    private TextView author4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

        foundDevice=(ImageView)findViewById(R.id.foundDevice);

        author1 = (TextView)findViewById(R.id.author_1);
        author2 = (TextView)findViewById(R.id.author_2);
        author3 = (TextView)findViewById(R.id.author_3);
        author4 = (TextView)findViewById(R.id.author_4);

        ImageView button=(ImageView)findViewById(R.id.centerImage);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundDevice();
                    }
                },3000);
            }
        });
        */
        rippleBackground.startRippleAnimation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                foundAuthorOne();
            }
        },3000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                foundAuthorTwo();
            }
        },4000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                foundAuthorThree();
            }
        },6000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                foundAuthorFour();
            }
        },8000);

    }

    private void foundDevice(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }


    private void foundAuthorOne(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(author1, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(author1, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        author1.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    private void foundAuthorTwo(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(author2, "ScaleX", 0f, 1.3f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(author2, "ScaleY", 0f, 1.3f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        author2.setVisibility(View.VISIBLE);
        animatorSet.start();

    }
    private void foundAuthorThree(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(600);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(author3, "ScaleX", 0f, 1.5f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(author3, "ScaleY", 0f, 1.6f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        author3.setVisibility(View.VISIBLE);
        animatorSet.start();
    }
    private void foundAuthorFour(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(author4, "ScaleX", 0f, 1.3f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(author4, "ScaleY", 0f, 1.3f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        author4.setVisibility(View.VISIBLE);
        animatorSet.start();

    }
}
