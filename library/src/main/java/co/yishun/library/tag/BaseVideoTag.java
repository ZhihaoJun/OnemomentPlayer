package co.yishun.library.tag;

/**
 * Created by jay on 10/3/15.
 */
public class BaseVideoTag implements VideoTag {
    private String mText;
    private float mX;
    private float mY;

    public BaseVideoTag(String text, float x, float y) {
        mText = text;
        mX = x;
        mY = y;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public void setX(float x) {
        mX = x;
    }

    @Override
    public float getY() {
        return mY;
    }

    @Override
    public void setY(float y) {
        mY = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoTag)) return false;

        VideoTag that = (VideoTag) o;

        if (Float.compare(that.getX(), mX) != 0) return false;
        if (Float.compare(that.getY(), mY) != 0) return false;
        return mText.equals(that.getText());
    }

    @Override
    public int hashCode() {
        int result = mText.hashCode();
        result = 31 * result + (mX != +0.0f ? Float.floatToIntBits(mX) : 0);
        result = 31 * result + (mY != +0.0f ? Float.floatToIntBits(mY) : 0);
        return result;
    }
}
