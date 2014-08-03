package br.com.uwant.models.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;

/**
 * Created by felipebenezi on 03/08/14.
 */
public class FriendsCircleAdapter extends BaseAdapter
{
    private final Context mContext;
    private final List<Person> mFriends;

    public FriendsCircleAdapter(Context context, List<Person> friends) {
        this.mContext = context;
        this.mFriends = friends;
    }

    @Override
    public int getCount() {
        return mFriends != null ? mFriends.size() : 0;
    }

    @Override
    public Person getItem(int i) {
        return mFriends != null ? mFriends.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_friends_circle, viewGroup, false);
            holder.hTextViewName = (TextView) view.findViewById(R.id.friendsCircle_adapter_textView_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Person person = getItem(i);
        String name = person.getName();

        holder.hTextViewName.setText(name);

        return view;
    }

    private static class ViewHolder {
        TextView hTextViewName;
    }

}
