package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.FriendsCircleFragment;
import br.com.uwant.models.classes.Person;

public class FriendsCircleAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private final List<Person> mFriends;
    private final View.OnClickListener mListener;
    private List<Person> mFilteredFriends;
    private Filter mFilter;

    public FriendsCircleAdapter(Context context, List<Person> friends, View.OnClickListener listener) {
        this.mContext = context;
        this.mFriends = friends;
        this.mListener = listener;
    }

    public FriendsCircleAdapter(Context context, List<Person> friends) {
        this.mContext = context;
        this.mFriends = friends;
        this.mListener = new FriendsCircleFragment();
    }

    @Override
    public int getCount() {
        return mFilteredFriends == null ?
                (mFriends != null ? mFriends.size() : 0)
                :
                (mFilteredFriends.size());
    }

    @Override
    public Person getItem(int i) {
        return mFilteredFriends == null ?
                (mFriends != null ? mFriends.get(i) : null)
                :
                (mFilteredFriends.get(i));
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
            holder.hImageViewPopUp = (ImageView) view.findViewById(R.id.friendsCircle_adapter_imageView_popUp);
            holder.hImageViewPopUp.setOnClickListener(this.mListener);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.hImageViewPopUp.setTag(i);

        Person person = getItem(i);
        String name = person.getName();

        holder.hTextViewName.setText(name);

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            createFilter();
        }
        return mFilter;
    }

    private void createFilter() {
        mFilter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Person> values = (List<Person>) results.values;
                if (values != null) {
                    mFilteredFriends = values; // has the filtered values
                } else {
                    mFilteredFriends = null;
                }

                notifyDataSetChanged();  // notifies the data with new filtered values. Only filtered values will be shown on the list
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Person> filteredWishList = new ArrayList<Person>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = 0;
                    results.values = null;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mFriends.size(); i++) {
                        Person data = mFriends.get(i);
                        if (data.getName().toLowerCase().contains(constraint.toString())) {
                            filteredWishList.add(data);
                        }
                    }

                    results.count = filteredWishList.size();
                    results.values = filteredWishList;
                }
                return results;
            }
        };
    }

    private static class ViewHolder {
        TextView hTextViewName;
        ImageView hImageViewPopUp;
    }

}
