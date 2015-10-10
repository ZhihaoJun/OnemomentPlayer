package co.yishun.onemomentplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import co.yishun.library.OnemomentPlayerView;
import co.yishun.library.resource.ResVideo;
import co.yishun.library.resource.BaseVideoResource;
import co.yishun.library.resource.TaggedVideo;
import co.yishun.library.resource.VideoResource;
import co.yishun.library.tag.BaseVideoTag;
import co.yishun.library.tag.VideoTag;

public class MainActivity extends Activity implements View.OnTouchListener {
    private OnemomentPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = (OnemomentPlayerView) findViewById(R.id.video);
        playerView.setEditMode(true);
        playerView.setOnTouchListener(this);

        VideoResource vr1 = new ResVideo(new BaseVideoResource(), this, R.raw.v1);
        TaggedVideo tv1 = new TaggedVideo(vr1);
        tv1.addTag(new BaseVideoTag("a1", 0.3f, 0.3f));
//                .addTag(new BaseVideoTag("a2", 0.7f, 0.3f));
//                .addTag(new BaseVideoTag("a3", 0.3f, 0.7f));

        VideoResource vr4 = new ResVideo(new BaseVideoResource(), this, R.raw.v4);
        TaggedVideo tv4 = new TaggedVideo(vr4);
        tv4.addTag(new BaseVideoTag("b1", 0.5f, 0.5f));
//                .addTag(new BaseVideoTag("b2", 0.7f, 0.7f))
//                .addTag(new BaseVideoTag("b3", 0.3f, 0.3f));

        playerView.addVideoResource(tv1);
        playerView.addVideoResource(new ResVideo(new BaseVideoResource(), this, R.raw.v2));
        playerView.addVideoResource(new ResVideo(new BaseVideoResource(), this, R.raw.v3));
        playerView.addVideoResource(tv4);
        playerView.addVideoResource(new ResVideo(new BaseVideoResource(), this, R.raw.v5));
        playerView.addVideoResource(new ResVideo(new BaseVideoResource(), this, R.raw.v6));

        try {
            playerView.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (playerView.isPlaying()) {
            playerView.pause();
        } else {
            playerView.start();
        }
        return false;
    }
}
