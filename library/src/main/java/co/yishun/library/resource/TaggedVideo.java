package co.yishun.library.resource;

import android.net.Uri;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import co.yishun.library.tag.VideoTag;

/**
 * Created by jay on 10/3/15.
 */
public class TaggedVideo implements VideoResource {
    private VideoResource mVideoResource;
    private List<VideoTag> mTags;

    public TaggedVideo(VideoResource videoResource, List<VideoTag> tags) {
        mVideoResource = videoResource;
        mTags = tags;
    }

    public TaggedVideo(VideoResource videoResource) {
        mVideoResource = videoResource;
        mTags = new LinkedList<VideoTag>();
    }

    public TaggedVideo addTag(VideoTag tag) {
        if (mTags != null) {
            mTags.add(tag);
        } else {
            Log.w("[TaggedVideo]", "tags list is null");
        }
        return this;
    }

    @Override
    public Uri getVideoUri() {
        return mVideoResource.getVideoUri();
    }

    @Override
    public Uri getAvatarUri() {
        return mVideoResource.getAvatarUri();
    }

    @Override
    public List<VideoTag> getVideoTags() {
        return mTags;
    }
}
