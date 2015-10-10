package co.yishun.library;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * show avatar view
 * @author ZhihaoJun
 */
public class AvatarView extends View {
    public static final int PLAYING_PADDING_H_DP = 0;
    public static final int PLAYING_PADDING_V_DP = 10;
    public static final int PLAYING_AVATAR_SIZE_DP = 40;
    public static final int READY_PADDING_H_DP = 0;
    public static final int READY_PADDING_V_DP = 15;
    public static final int READY_AVATAR_SIZE_DP = 30;

    private Paint mAvatarPaint;
    private Bitmap mBitmap;
    private Bitmap mOriginalBitmap;

    private int mPlayingAvatarSizePx;
    private int mPlayingPaddingHPx;
    private int mPlayingPaddingVPx;

    private int mReadyAvatarSizePx;
    private int mReadyPaddingHPx;
    private int mReadyPaddingVPx;

    private RectF mReadyAvatarRectF;
    private RectF mPlayingAvatarRectF;
    private boolean mPlaying = false;

    private LinearLayout.LayoutParams mPlayingLayoutParams;
    private LinearLayout.LayoutParams mReadyLayoutParams;

    public AvatarView(Context context) {
        super(context);
        init(null, 0);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setImage(Uri uri) throws IOException {
        mOriginalBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
        setupPaint(true);
    }

    public void setImage(int resId) {
        mOriginalBitmap = BitmapFactory.decodeResource(getContext().getResources(), resId);
        setupPaint(true);
    }

    public void setBitmap(Bitmap bitmap) {
        mOriginalBitmap = bitmap;
        setupPaint(false);
    }

    public void setPlaying(boolean playing) {
        mPlaying = playing;
        if (mPlaying) {
            setLayoutParams(mReadyLayoutParams);
        } else {
            setLayoutParams(mPlayingLayoutParams);
        }
    }

    private void setupPaint(boolean recycle) {
        mBitmap = Bitmap.createScaledBitmap(
                mOriginalBitmap, mPlayingAvatarSizePx, mPlayingAvatarSizePx, false);
        BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mAvatarPaint = new Paint();
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setDither(true);
        mAvatarPaint.setShader(shader);

        if (recycle) {
            mOriginalBitmap.recycle();
        }
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        mReadyAvatarSizePx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, READY_AVATAR_SIZE_DP, dm));
        mReadyPaddingHPx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, READY_PADDING_H_DP, dm));
        mReadyPaddingVPx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, READY_PADDING_V_DP, dm));

        mPlayingAvatarSizePx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PLAYING_AVATAR_SIZE_DP, dm));
        mPlayingPaddingHPx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PLAYING_PADDING_H_DP, dm));
        mPlayingPaddingVPx = (int)(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PLAYING_PADDING_V_DP, dm));

        mReadyLayoutParams = new LinearLayout.LayoutParams(
                mReadyAvatarSizePx + mReadyPaddingHPx + mReadyPaddingHPx,
                mReadyAvatarSizePx + mReadyPaddingVPx + mReadyPaddingVPx);
        mPlayingLayoutParams = new LinearLayout.LayoutParams(
                mPlayingAvatarSizePx + mPlayingPaddingHPx + mPlayingPaddingHPx,
                mPlayingAvatarSizePx + mPlayingPaddingVPx + mPlayingPaddingVPx);

        mReadyAvatarRectF = new RectF(
                mReadyPaddingHPx, mReadyPaddingVPx,
                mReadyPaddingHPx + mReadyAvatarSizePx,
                mReadyPaddingVPx + mReadyAvatarSizePx);
        mPlayingAvatarRectF = new RectF(
                mPlayingPaddingHPx, mPlayingPaddingVPx,
                mPlayingPaddingHPx + mPlayingAvatarSizePx,
                mPlayingPaddingVPx + mPlayingAvatarSizePx);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAvatarPaint != null) {
            if (mPlaying) {
                canvas.drawOval(mPlayingAvatarRectF, mAvatarPaint);
            } else {
                canvas.drawOval(mReadyAvatarRectF, mAvatarPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalWidth;
        int finalHeight;
        if (mPlaying) {
            finalWidth = mPlayingAvatarSizePx + mPlayingPaddingHPx + mPlayingPaddingHPx;
            finalHeight = mPlayingAvatarSizePx + mPlayingPaddingVPx + mPlayingPaddingVPx;
        } else {
            finalWidth = mReadyAvatarSizePx + mReadyPaddingHPx + mReadyPaddingHPx;
            finalHeight = mReadyAvatarSizePx + mReadyPaddingVPx + mReadyPaddingVPx;
        }
        Log.i("[AV]", "playing " + mPlaying);
        finalWidth = MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY);
        finalHeight = MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY);
        super.onMeasure(finalWidth, finalHeight);
    }
}
