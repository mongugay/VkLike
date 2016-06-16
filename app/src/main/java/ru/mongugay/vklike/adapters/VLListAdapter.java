package ru.mongugay.vklike.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.mongugay.vklike.R;
import ru.mongugay.vklike.ViewHolder;
import ru.mongugay.vklike.models.VLApiModel;
import ru.mongugay.vklike.models.VLApiPhoto;

/**
 * Created by user on 28.09.2015.
 */
public class VLListAdapter extends VLAdapter
{
    Context _context;
    ArrayList<VLApiModel> _photos;
    LayoutInflater _layout;
    ImageLoader imageLoader;

    public VLListAdapter(Context context, ArrayList<VLApiModel> photos)
    {
        _context = context;
        _photos  = photos;
        _layout  = (LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
    }
    @Override
    public int getCount() {
        return _photos.size();
    }

    @Override
    public Object getItem(int i) {
        return _photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;
        if (view == null)
        {
            view = _layout.inflate(R.layout.vl_photo_list_adapter, viewGroup, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.timestamp = (TextView) view.findViewById(R.id.secondLine);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        final VLApiPhoto photo = (VLApiPhoto) getPhoto(i);

        String name = photo.photo.id + " " + photo.photo.owner_id;
        ((TextView) view.findViewById(R.id.firstLine)).setText(name);

        String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(photo.photo.date*1000));
        holder.timestamp.setText(dateString);

        //ImageView ico = (ImageView) view.findViewById(R.id.icon);
        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.checkBox);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangList);
        // пишем позицию
        cbBuy.setTag(i);
        // заполняем данными из товаров: в корзине или нет
        cbBuy.setChecked(photo.box);

        if (holder.icon != null) {
            imageLoader.displayImage(photo.photo.photo_130, holder.icon);
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = photo.photo.photo_1280;
                if (uri == "") uri = photo.photo.photo_807;
                if (uri == "") uri = photo.photo.photo_604;
                if (uri == "") uri = photo.photo.photo_130;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                _context.startActivity(browserIntent);

            }
        });
        return view;
    }

    // содержимое корзины
    public ArrayList<VLApiModel> getBox() {
        ArrayList<VLApiModel> box = new ArrayList<VLApiModel>();
        for (VLApiModel p : _photos) {
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
            getPhoto((Integer) buttonView.getTag()).box = isChecked;
        }
    };

    VLApiPhoto getPhoto(int position)
    {
        return (VLApiPhoto) getItem(position);
    }
}
