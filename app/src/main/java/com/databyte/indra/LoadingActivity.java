package com.databyte.indra;

/**
 * Created by satra_000 on 01-03-2018.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
//import android.content.Context;
//import android.content.Intent;
//import android.support.graphics.drawable.AnimationUtilsCompat;
//import java.util.TimerTask;
//import android.view.animation.AnimationSet;
//import android.view.animation.AnimationUtils;
//import android.view.animation.LinearInterpolator;
//import android.util.Log;
import com.databyte.indra.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScaleAnimation growAnim =
                new ScaleAnimation(
                        1.0f, 1.15f,
                        1.0f, 2.0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1.5f);
        final ScaleAnimation shrinkAnim = new ScaleAnimation(1.15f, 1.0f, 2.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1.5f);

        growAnim.setDuration(2000);
        shrinkAnim.setDuration(2000);


        final ImageView anim1 = findViewById(R.id.anim1);
        final ImageView anim2 = findViewById(R.id.anim2);
        final ImageView anim3 = findViewById(R.id.anim3);

        anim2.setAnimation(shrinkAnim);
        anim1.setAnimation(growAnim);
        anim3.setAnimation(growAnim);
        shrinkAnim.start();
        growAnim.start();
        growAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim1.setAnimation(shrinkAnim);
                anim3.setAnimation(shrinkAnim);
                shrinkAnim.start();    //start growth of others from here
//                shrinkAnim.start();
                anim2.setAnimation(growAnim);
                growAnim.start();
            }
        });
        shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim1.setAnimation(growAnim);
                anim3.setAnimation(growAnim);
                growAnim.start();
                anim2.setAnimation(shrinkAnim);
                shrinkAnim.start();
            }
        });
    }
}
