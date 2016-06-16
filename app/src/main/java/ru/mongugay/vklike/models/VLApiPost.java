package ru.mongugay.vklike.models;

import com.vk.sdk.api.model.VKApiPost;

/**
 * Created by user on 12.10.2015.
 */
public class VLApiPost extends VLApiModel {
    public VKApiPost post;
    public VLApiPost(VKApiPost raw)
    {
        post = raw;
    }

    @Override
    public int getId() {
        return post.id;
    }

    @Override
    public int getOwnerId() {
        return post.from_id;
    }
}
