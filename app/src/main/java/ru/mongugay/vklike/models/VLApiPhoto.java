package ru.mongugay.vklike.models;

import com.vk.sdk.api.model.VKApiPhoto;

/**
 * Created by user on 29.09.2015.
 */
public class VLApiPhoto extends VLApiModel {
    public VKApiPhoto photo;
    public VLApiPhoto(VKApiPhoto raw)
    {
        photo = raw;
    }

    @Override
    public int getId() {
        return photo.id;
    }

    @Override
    public int getOwnerId() {
        return photo.owner_id;
    }
}
