package ru.mongugay.vklike.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiPostedPhoto;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKAttachments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.mongugay.vklike.R;
import ru.mongugay.vklike.ViewHolder;
import ru.mongugay.vklike.models.VLApiModel;
import ru.mongugay.vklike.models.VLApiPost;

/**
 * Created by user on 12.10.2015.
 */
public class VLListPostAdapter extends VLAdapter {

    Context _context;
    ArrayList<VLApiModel> _posts;
    LayoutInflater _layout;
    ImageLoader imageLoader;

    public VLListPostAdapter(Context context, ArrayList<VLApiModel> posts)
    {
        _context    = context;
        _posts      = posts;
        _layout     = (LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return _posts.size();
    }

    @Override
    public Object getItem(int position) {
        return _posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = _layout.inflate(R.layout.vl_photo_list_adapter, viewGroup, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.timestamp = (TextView) view.findViewById(R.id.secondLine);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final VLApiPost post = (VLApiPost) getPost(position);

        String name = String.valueOf(post.post.text);
        ((TextView) view.findViewById(R.id.firstLine)).setText(name);

        String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(post.post.date*1000));
        holder.timestamp.setText(dateString);

        //ImageView ico = (ImageView) view.findViewById(R.id.icon);
        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.checkBox);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangList);
        // пишем позицию
        cbBuy.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        cbBuy.setChecked(post.box);

        VKAttachments attachments;
        final VLApiPost historypost;
        if (post.post.copy_history.size() != 0)
        {
            historypost = new VLApiPost(post.post.copy_history.get(0));
            attachments = historypost.post.attachments;

            name = String.valueOf(historypost.post.text);
            ((TextView) view.findViewById(R.id.firstLine)).setText(name);
        }
        else
        {
            attachments = post.post.attachments;
        }

        String attachPhoto = "";
        if (attachments.size() != 0) {
            VKAttachments.VKApiAttachment attach = attachments.get(0);

            if (attach.getType() == VKApiConst.PHOTO) {
                VKApiPhoto photo = (VKApiPhoto) attach;
                attachPhoto = photo.photo_130;
            }

            if (attach.getType() == "posted_photo")
            {
                VKApiPostedPhoto postedPhoto = (VKApiPostedPhoto) attach;
                attachPhoto = postedPhoto.photo_130;
            }

            if (attach.getType() == "video")
            {
                VKApiVideo postedVideo = (VKApiVideo) attach;
                attachPhoto = postedVideo.photo_130;
            }

            if (attach.getType() == "doc")
            {
                VKApiDocument postedDoc = (VKApiDocument) attach;
                attachPhoto = postedDoc.photo_130;
            }
            if (attach.getType() == "album")
            {
                VKApiPhotoAlbum postedAlbum = (VKApiPhotoAlbum) attach;
                if (postedAlbum.photo.size() != 0) {
                    attachPhoto = postedAlbum.photo.get(0).src;
                }
            }

        }
        Drawable myDrawable = _context.getResources().getDrawable(R.drawable.stub);
        holder.icon.setImageDrawable(myDrawable);
        if (holder.icon != null && attachPhoto != "") {
            imageLoader.displayImage(attachPhoto, holder.icon);
        }
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                String uri = post.post.photo_1280;
                if (uri == "") uri = post.post.photo_807;
                if (uri == "") uri = post.post.photo_604;
                if (uri == "") uri = post.post.photo_130;*/
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                //_context.startActivity(browserIntent);

            }
        });
        return view;
    }

    // содержимое корзины
    public ArrayList<VLApiModel> getBox() {
        ArrayList<VLApiModel> box = new ArrayList<VLApiModel>();
        for (VLApiModel p : _posts) {
            // если в корзине
            if (p.box) box.add(p);
        }
        return box;
    }

    // обработчик для чекбоксов
    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            getPost((Integer) buttonView.getTag()).box = isChecked;
        }
    };

    VLApiPost getPost(int position)
    {
        return (VLApiPost) getItem(position);
    }
}
