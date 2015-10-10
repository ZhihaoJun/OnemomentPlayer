package co.yishun.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import co.yishun.library.tag.VideoTag;

/**
 * TODO: document your custom view class.
 */
public class VideoTagView extends View {
    public final int PADDING_VERTICAL = 15;
    public final int PADDING_HORIZONTAL = 25;

    private VideoTag mVideoTag;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private int mFontSizeDip = 20;
    private Rect mTextBounds = new Rect();
    private RectF mBackgroundBox;
    private boolean mDrag = true;
    private boolean mEditMode = false;

    public VideoTagView(Context context) {
        super(context);
        init(null, 0);
    }

    public VideoTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VideoTagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setVideoTag(VideoTag tag) {
        mVideoTag = tag;
        String text = tag.getText();
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        mBackgroundBox = new RectF(
                0, 0,
                mTextBounds.width() + PADDING_HORIZONTAL + PADDING_HORIZONTAL,
                mTextBounds.height() + PADDING_VERTICAL + PADDING_VERTICAL);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        int fontSizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mFontSizeDip,
                getResources().getDisplayMetrics());
        Log.i("[VTV]", "font size in px " + fontSizePx);
        mTextPaint.setTextSize(fontSizePx);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.argb(128, 0, 0, 0));
    }

    public void setEditMode(boolean inEditMode) {
        mEditMode = inEditMode;
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("[VTV]", "start draw");

//        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);
        if (mBackgroundBox != null) {
            float radius = getHeight()/2.0f;
            canvas.drawRoundRect(mBackgroundBox, radius, radius, mBackgroundPaint);
        }
        if (mVideoTag != null) {
            canvas.drawText(
                    mVideoTag.getText(),
                    getWidth()/2, (getHeight()+mTextBounds.height())/2,
                    mTextPaint);
            Log.i("[VTV]", "video tag " + mVideoTag.getText());
        } else {
            Log.i("[VTV]", "video tag is null");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("[VTV]", "onSizeChanged " + w + "," + h + " old " + oldw + "," + oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("[VTV]", "onMeasure" + widthMeasureSpec + "," + heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.i("[VTV]", "widthSize " + widthSize);
        Log.i("[VTV]", "widthMode " + widthMode);
        Log.i("[VTV]", "heightSize " + heightSize);
        Log.i("[VTV]", "heightMode " + heightMode);

        int finalWidth = widthMeasureSpec;
        int finalHeight = heightMeasureSpec;

        if (mTextBounds != null) {
            finalWidth = MeasureSpec.makeMeasureSpec(
                    mTextBounds.width() + PADDING_HORIZONTAL + PADDING_HORIZONTAL,
                    MeasureSpec.EXACTLY);
            finalHeight = MeasureSpec.makeMeasureSpec(
                    mTextBounds.height() + PADDING_VERTICAL + PADDING_VERTICAL,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(finalWidth, finalHeight);
    }
}
