package co.yishun.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import co.yishun.library.resource.VideoResource;
import co.yishun.library.tag.VideoTag;

/**
 * OnemomentPlayerView
 * @author ZhihaoJun
 */
public class OnemomentPlayerView extends RelativeLayout
         implements SurfaceHolder.Callback, MediaPlayer.OnErrorListener,
         MediaPlayer.OnCompletionListener, VideoSquareContainer.OnMeasuredListener, View.OnTouchListener {
    public final static String VIDEO_TAG_VIEW_TAG = "video_tag";

    private SurfaceHolder mVideoSurfaceHolder;
    private SurfaceView mVideo;
    private ImageView mPlayBtn;
    private RelativeLayout mTagsContainer;
    private VideoSquareContainer mVideoContainer;
    private LinearLayout mAvatarContainer;
    private List<VideoResource> mVideoResources = new LinkedList<VideoResource>();
    private MediaPlayer mActiveMediaPlayer;
    private int mVideoIndex = 0;
    private boolean mShowPlayBtn = true;
    private boolean mAutoplay = false;
    private boolean mShowTags = true;
    private boolean mEditMode = false;
    private int mLastX = 0;
    private int mLastY = 0;

    public OnemomentPlayerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OnemomentPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OnemomentPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public void init(Context context, AttributeSet attrs, int defStyle) {
        mActiveMediaPlayer = new MediaPlayer();
        if (attrs != null) {
            TypedArray ta = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.OnemomentPlayerView,
                    0, 0);

            try {
                mShowPlayBtn = ta.getBoolean(R.styleable.OnemomentPlayerView_showPlayButton, true);
                mAutoplay = ta.getBoolean(R.styleable.OnemomentPlayerView_autoplay, false);
                mShowTags = ta.getBoolean(R.styleable.OnemomentPlayerView_showTags, true);
            } finally {
                ta.recycle();
            }
        }
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.onemoment_player_view, this);
        }

        // get views
        mVideo = (SurfaceView) findViewById(R.id.om_video_surface);
        mPlayBtn = (ImageView) findViewById(R.id.om_play_btn);
        mTagsContainer = (RelativeLayout) findViewById(R.id.om_tags_container);
        mVideoContainer = (VideoSquareContainer) findViewById(R.id.om_video_container);
        mAvatarContainer = (LinearLayout) findViewById(R.id.om_heads_container);

        AvatarView av = new AvatarView(getContext());
        try {
            av.setImage(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.avatar_test));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("[OPV]", "set image from uri failed", e);
        }
        mAvatarContainer.addView(av);

        av = new AvatarView(getContext());
        av.setPlaying(true);
        try {
            av.setImage(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.avatar_test));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("[OPV]", "set image from uri failed", e);
        }
        mAvatarContainer.addView(av);

        av = new AvatarView(getContext());
        try {
            av.setImage(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.avatar_test));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("[OPV]", "set image from uri failed", e);
        }
        mAvatarContainer.addView(av);

        // add listeners
        mVideoContainer.setOnMeasuredListener(this);
        mVideoSurfaceHolder = mVideo.getHolder();
        mVideoSurfaceHolder.addCallback(this);
        mActiveMediaPlayer.setOnCompletionListener(this);
    }

    private void addTag(VideoTag tag) {
        VideoTagView v = new VideoTagView(getContext());
        v.setTag(VIDEO_TAG_VIEW_TAG);
        v.setVideoTag(tag);

        // position
        int left = (int)(tag.getX()*mVideoContainer.getSize());
        int top = (int)(tag.getY()*mVideoContainer.getSize());

        Log.i("[OPV]", "add tag at (" + left + "," + top + ")");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, 0, 0);
        v.setLayoutParams(params);
        v.setOnTouchListener(this);
        if (mEditMode) {
            v.setEditMode(true);
        }
        mTagsContainer.addView(v);
    }

    public boolean isPlaying() {
        return mActiveMediaPlayer.isPlaying();
    }

    public void start() {
        if (mEditMode) return;
        if (!mActiveMediaPlayer.isPlaying()) {
            mActiveMediaPlayer.start();
        }
        if (mShowPlayBtn) {
            mPlayBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void pause() {
        if (mEditMode) return;
        if (mActiveMediaPlayer.isPlaying()) {
            mActiveMediaPlayer.pause();
        }
        if (mShowPlayBtn) {
            mPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    public void addVideoResource(VideoResource videoResource) {
        Log.i("[OPV]", "add resource " + videoResource);
        mVideoResources.add(videoResource);
    }

    public void prepare() throws IOException {
        if (mVideoResources.size() <= 0)
            return;
        VideoResource first = mVideoResources.get(0);
        mActiveMediaPlayer.setDataSource(getContext(), first.getVideoUri());
        mActiveMediaPlayer.prepare();
    }

    private void showTags(int index) {
        clearTags();
        if (!mShowTags) return;
        List<VideoTag> tags = mVideoResources.get(index).getVideoTags();
        if (tags == null) return;

        Log.i("[OPV]", "video " + index + " has tags");

        for (VideoTag tag: tags) {
            addTag(tag);
        }
    }

    private void clearTags() {
        int childCount = mTagsContainer.getChildCount();
        Log.i("[OPV]", "child view count: " + childCount);
        for (int i=0; i<childCount; i++) {
            View v = mTagsContainer.getChildAt(i);
            if (v != null) {
                Object tag = v.getTag();
                if (tag != null && tag.equals(VIDEO_TAG_VIEW_TAG)) {
                    mTagsContainer.removeViewAt(i);
                }
            } else {
                Log.w("[OPV]", "child view is null at " + i);
            }
        }
    }

    public void setShowPlayBtn(boolean mShowPlayBtn) {
        this.mShowPlayBtn = mShowPlayBtn;
        mPlayBtn.setVisibility(View.INVISIBLE);
        invalidate();
        requestLayout();
    }

    public boolean isAutoplay() {
        return mAutoplay;
    }

    public void setAutoplay(boolean mAutoplay) {
        this.mAutoplay = mAutoplay;
    }

    public boolean isShowTags() {
        return mShowTags;
    }

    public void setShowTags(boolean mShowTags) {
        this.mShowTags = mShowTags;
        invalidate();
        requestLayout();
    }

    public int getVideoIndex() {
        return this.mVideoIndex;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("[OPV]", "SurfaceCreated");
        mActiveMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i("[OPV]", "SurfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("[OPV]", "SurfaceDestroyed");
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.i("[OPV]", "mp error" + what + "");
        Log.i("[OPV]", "mp error" + extra + "");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i("[OPV]", "mp complete");
        if (mEditMode) return;
        if (mVideoIndex < mVideoResources.size() - 1) {
            mVideoIndex ++;
        } else {
            mVideoIndex = 0;
        }

        Log.i("[OPV]", "next video index: " + mVideoIndex);
        VideoResource vr = mVideoResources.get(mVideoIndex);
        mActiveMediaPlayer.reset();
        try {
            mActiveMediaPlayer.setDataSource(getContext(), vr.getVideoUri());
        } catch (IOException e) {
            Log.i("[OPV]", "media player set data source failed");
            e.printStackTrace();
        }
        try {
            mActiveMediaPlayer.prepare();
        } catch (IOException e) {
            Log.i("[OPV]", "media player prepare failed");
            e.printStackTrace();
        }

        showTags(mVideoIndex);

        if (mVideoIndex != 0) {
            mActiveMediaPlayer.start();
        }

        if (mShowPlayBtn && mVideoIndex == 0) {
            mPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setOnTouchListener(View.OnTouchListener listener) {
        mVideo.setOnTouchListener(listener);
    }

    public void setEditMode(boolean inEditMode) {
        mEditMode = inEditMode;
        if (mEditMode) {
            setShowPlayBtn(false);
        }
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    @Override
    public void onMeasured(int width, int height) {
        showTags(mVideoIndex);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!mEditMode) return false;
        if (view.getTag().equals(OnemomentPlayerView.VIDEO_TAG_VIEW_TAG)) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("[OPV]", "touch down " + view);
                    mLastX = (int)motionEvent.getRawX();
                    mLastY = (int)motionEvent.getRawY();
                    Log.i("[OPV]", "last " + mLastX + "," + mLastY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("[OPV]", "touch move " + view);
                    int dx = (int)motionEvent.getRawX() - mLastX;
                    int dy = (int)motionEvent.getRawY() - mLastY;
                    int left = view.getLeft() + dx;
                    int top = view.getTop() + dy;
                    int right = view.getRight() + dx;
                    int bottom = view.getBottom() + dy;
                    Log.i("[OPV]", "new position: " + left + "," + top + "," + right + "," + bottom);
                    view.layout(left, top, right, bottom);

                    mLastX = (int)motionEvent.getRawX();
                    mLastY = (int)motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("[OPV]", "touch up " + view);
                    break;
            }
        }
        return true;
    }
}
