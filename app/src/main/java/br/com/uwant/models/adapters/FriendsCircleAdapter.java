package br.com.uwant.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.PerfilActivity;
import br.com.uwant.flow.fragments.FriendsCircleFragment;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.AddFriendModel;
import br.com.uwant.models.cloud.models.BlockFriendModel;
import br.com.uwant.models.cloud.models.ExcludeFriendModel;

public class FriendsCircleAdapter extends BaseAdapter implements Filterable,
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private boolean itsMe = true;
    private int mMenuId;
    private Context mContext;
    private List<Person> mFriends;
    private FriendsCircleFragment mFragment;
    private List<Person> mFilteredFriends;
    private Filter mFilter;
    private Person mPersonSelected;

    private IRequest.OnRequestListener<Boolean> mListenerAddExcludeBlock = new IRequest.OnRequestListener<Boolean>() {

        private ProgressFragmentDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            if (FriendsCircleAdapter.this.mFragment != null) {
                mProgressDialog = ProgressFragmentDialog.show(FriendsCircleAdapter.this.mFragment.getFragmentManager());
            }
        }

        @Override
        public void onExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            if (FriendsCircleAdapter.this.mFragment != null) {
                FriendsCircleAdapter.this.mFragment.updateFriends();
            }

            Toast.makeText(FriendsCircleAdapter.this.mContext, "Operação realizada com sucesso!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(RequestError error) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(FriendsCircleAdapter.this.mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };

    public FriendsCircleAdapter(FriendsCircleFragment fragment, List<Person> friends, Person whoAmI) {
        this.mFragment = fragment;
        this.mContext = fragment.getActivity();
        this.mFriends = friends;
        this.itsMe = (whoAmI instanceof User);
        this.mMenuId = R.menu.friends_circle_actions;
    }

    public FriendsCircleAdapter(Context context, List<Person> friends) {
        this.mContext = context;
        this.mFriends = friends;
        this.mMenuId = 0;
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
            holder.hImageViewPopUp.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        Person person = null;
        if (position != null)
            person = getItem(position);

        switch (view.getId()) {
            case R.id.friendsCircle_adapter_imageView_popUp:
                openPopUp(view, person);
                break;

            default:
                break;
        }
    }

    private void openPopUp(View v, Person person) {
        this.mPersonSelected = person;
        int menuId = (this.mMenuId != 0 && this.itsMe) ?
                this.mMenuId
                :
                (person.isFriend() ?
                        R.menu.friends_circle_actions
                        :
                        R.menu.friends_search_actions);

        PopupMenu popup = new PopupMenu(this.mContext, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuId, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (this.mPersonSelected != null) {
            switch (item.getGroupId()) {
                case R.id.group_friend:
                    switch (item.getItemId()) {
                        case R.id.menu_activities:
                            cancelActivities();
                            break;

                        case R.id.menu_exclude:
                            excludeFriend();
                            break;

                        case R.id.menu_add_friend:
                            addFriend();
                            break;

                        case R.id.menu_perfil:
                            perfilFriend();
                            break;

                        default:
                            return false;
                    }
                    return true;

                default:
                    break;
            }
        }
        return false;
    }

    private void perfilFriend() {
        if (this.mPersonSelected != null) {
            Intent it = new Intent(this.mContext, PerfilActivity.class);
            it.putExtra(Person.EXTRA, this.mPersonSelected);
            this.mContext.startActivity(it);
        }
    }

    private void addFriend() {
        AddFriendModel model = new AddFriendModel();
        model.setPerson(this.mPersonSelected);
        Requester.executeAsync(model, this.mListenerAddExcludeBlock);
    }

    private void excludeFriend() {
        ExcludeFriendModel model = new ExcludeFriendModel();
        model.setPerson(this.mPersonSelected);
        Requester.executeAsync(model, this.mListenerAddExcludeBlock);
    }

    private void cancelActivities() {
        BlockFriendModel model = new BlockFriendModel();
        model.setPerson(this.mPersonSelected);
        Requester.executeAsync(model, this.mListenerAddExcludeBlock);
    }

    private static class ViewHolder {
        TextView hTextViewName;
        ImageView hImageViewPopUp;
    }

}
