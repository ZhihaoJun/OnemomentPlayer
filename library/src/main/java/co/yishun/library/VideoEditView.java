package co.yishun.library;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import co.yishun.library.tag.VideoTag;

/**
 * Created by jay on 10/10/15.
 */
public class VideoEditView extends FrameLayout implements SquareLayout.OnMeasuredListener {
    public static final String TAG_VIEW_TAG_PREFIX = "tag";

    private ImageView mVideoFrame;
    private SquareLayout mVideoFrameContainer;
    private Uri mVideoUri;
    private int mVideoFrameWidth;
    private int mVideoFrameHeight;
    private List<VideoTag> mTags;
    private OnTagDeletedListener mOnTagDeletedListener;

    public VideoEditView(Context context) {
        super(context);
        init(null, 0);
    }

    public VideoEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VideoEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mTags = new ArrayList<VideoTag>();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.video_edit_view, this);

        mVideoFrame = (ImageView) findViewById(R.id.om_video_frame);
        mVideoFrameContainer = (SquareLayout) findViewById(R.id.om_video_frame_container);

        mVideoFrameContainer.setOnMeasuredListener(this);
    }

    public void showTag(VideoTag tag) {
        VideoTagView tagView = new VideoTagView(getContext());
        tagView.setVideoTag(tag);
        tagView.setTag(VideoEditView.TAG_VIEW_TAG_PREFIX + tag.hashCode());
        addView(tagView);
    }

    public void addTag(VideoTag tag) {
        mTags.add(tag);
        showTag(tag);
    }

    public void removeTag(VideoTag tag) {
        mTags.remove(tag);
        if (mOnTagDeletedListener != null) {
            mOnTagDeletedListener.onTagDeleted(tag);
        }
    }

    public void setOnTagDeletedListener(OnTagDeletedListener listener) {
        mOnTagDeletedListener = listener;
    }

    public void setVideo(Uri uri) {
        mVideoUri = uri;
        setVideoFrame();
    }

    public void setVideo(int resId) {
        mVideoUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resId);
        setVideoFrame();
    }

    public void setVideoFrameOnTouchListener(OnTouchListener listener) {
        mVideoFrame.setOnTouchListener(listener);
    }

    private void setVideoFrame() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(), mVideoUri);
        Bitmap thumb = mmr.getFrameAtTime(0L);
        mVideoFrame.setImageBitmap(thumb);
    }

    @Override
    public void onMeasured(int width, int height) {
        mVideoFrameWidth = width;
        mVideoFrameHeight = height;
    }

    public interface OnTagDeletedListener {
        void onTagDeleted(VideoTag tag);
    }
}
