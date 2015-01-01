package br.com.uwant.models.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import br.com.uwant.R;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.views.CheckableLinearLayout;
import br.com.uwant.utils.PictureUtil;

public class ContactsAdapter extends BaseAdapter implements SectionIndexer {

    private final int WDP;
    private final int HDP;
    private Context mContext;
    private Map<String, Integer> alphabeticIndexer;
    private List<String> sectionsArray;
    private String[] sections;
    private List<Person> mPersons;
    private GridView mGridView;
    private final DisplayImageOptions mOptions;
    private final ImageSize mTargetSize;

    public ContactsAdapter(Context context, GridView listView, List<Person> persons) {
        this.mContext = context;
        this.mPersons = persons;
        this.mGridView = listView;

        this.mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        float dpi = context.getResources().getDisplayMetrics().density;
        WDP = (int) (dpi * 76);
        HDP = (int) (dpi * 76);
        this.mTargetSize = new ImageSize(WDP, HDP);

        getAlphabeticIndex(persons);
    }

    private void getAlphabeticIndex(List<Person> persons) {
        if (persons == null || persons.size() == 0)
            return;

        this.alphabeticIndexer = new LinkedHashMap<String, Integer>(30);
        for (int i = 0;i < persons.size();i++) {
            Person person = persons.get(i);
            String letter = String.valueOf(person.getName().toUpperCase(Locale.getDefault()).charAt(0));
            if (!alphabeticIndexer.containsKey(letter)) {
                alphabeticIndexer.put(letter, i);
            }
        }

        Set<String> keys = alphabeticIndexer.keySet();
        this.sectionsArray = new ArrayList<String>(keys);
        Collections.sort(this.sectionsArray);

        this.sections = this.sectionsArray.toArray(new String[this.sectionsArray.size()]);
    }

    @Override
    public int getCount() {
        return this.mPersons == null || this.mPersons.size() == 0 ? 0 : this.mPersons.size() + 1;
    }

    @Override
    public Person getItem(int i) {
        return this.mPersons.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_contacts, viewGroup, false);
            holder.hImageViewPicture = (ImageView) view.findViewById(R.id.contacts_adapter_imageView_picture);
            holder.hImageViewPictureCircle = (ImageView) view.findViewById(R.id.contacts_adapter_imageView_pictureCircle);
            holder.hTextViewName = (TextView) view.findViewById(R.id.contacts_adapter_textView_name);
            holder.hTextViewMail = (TextView) view.findViewById(R.id.contacts_adapter_textView_mail);
            holder.hCheckBox = (CheckBox) view.findViewById(R.id.checkablelinearlayou_checkbox);
            holder.hPosition = position;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.hImageViewPictureCircle.setVisibility(View.INVISIBLE);

        if (position == 0) {
            holder.hTextViewMail.setVisibility(View.GONE);
            holder.hImageViewPicture.setVisibility(View.GONE);
            holder.hImageViewPictureCircle.setVisibility(View.GONE);
            holder.hCheckBox.setVisibility(View.GONE);
            holder.hTextViewName.setText(R.string.text_invite_all_friends);
        } else {
            holder.hTextViewMail.setVisibility(View.VISIBLE);
            holder.hImageViewPicture.setVisibility(View.VISIBLE);
            holder.hCheckBox.setVisibility(View.VISIBLE);

            boolean isChecked = mGridView.isItemChecked(position);
            CheckableLinearLayout cll = (CheckableLinearLayout) view;
            cll.setChecked(isChecked);
            holder.hCheckBox.setChecked(isChecked);

            Person person = getItem(position);
            String name = person.getName();
            String mail = person.getMail();
            final Multimedia multimedia = person.getPicture();
            if (multimedia != null) {
                Bitmap bitmap = multimedia.getBitmap();
                Uri uri = multimedia.getUri();
                String url = multimedia.getUrl();
                if (bitmap != null) {
                    holder.hImageViewPicture.setImageBitmap(bitmap);
                    holder.hImageViewPictureCircle.setVisibility(View.VISIBLE);
                } else if (uri != null) {
                    load(position, holder, uri);
                } else if (url != null) {
                    load(position, holder, url);
                } else {
                    holder.hImageViewPicture.setImageResource(R.drawable.ic_contatos_semfoto);
                    holder.hImageViewPictureCircle.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.hImageViewPicture.setImageResource(R.drawable.ic_contatos_semfoto);
                holder.hImageViewPictureCircle.setVisibility(View.INVISIBLE);
            }

            holder.hTextViewName.setText(name);
            holder.hTextViewMail.setText(mail == null ? "" : mail);
        }

        return view;
    }

    private void load(final int position, ViewHolder vh, Uri uri) {
        new AsyncTask<Object, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(Object... objects) {
                viewHolder = (ViewHolder) objects[0];
                Uri uri = (Uri) objects[1];

                try {
                    Bitmap bitmap = Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.ic_contatos_semfoto).get();

                    bitmap = PictureUtil.cropToFit(bitmap);
                    bitmap = PictureUtil.scale(bitmap, viewHolder.hImageViewPicture);
                    bitmap = PictureUtil.circle(bitmap);

                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    if (viewHolder.hPosition == position) {
                        viewHolder.hImageViewPicture.setImageBitmap(bitmap);
                        viewHolder.hImageViewPictureCircle.setVisibility(View.VISIBLE);
                    }
                }
            }

        }.execute(vh, uri);
    }

    private void load(final int position, ViewHolder vh, String url) {
        new AsyncTask<Object, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(Object... objects) {
                viewHolder = (ViewHolder) objects[0];
                String url = (String) objects[1];

                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(url, mOptions);

                bitmap = PictureUtil.cropToFit(bitmap);
                bitmap = PictureUtil.scale(bitmap, viewHolder.hImageViewPicture);
                bitmap = PictureUtil.circle(bitmap);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    if (viewHolder.hPosition == position) {
                        viewHolder.hImageViewPicture.setImageBitmap(bitmap);
                        viewHolder.hImageViewPictureCircle.setVisibility(View.VISIBLE);
                    }
                }
            }

        }.execute(vh, url);
    }

    @Override
    public void notifyDataSetChanged() {
        this.getAlphabeticIndex(this.mPersons);
        super.notifyDataSetChanged();
    }

    @Override
    public String[] getSections() {
        return this.sections;
    }

    @Override
    public int getPositionForSection(int i) {
        return this.alphabeticIndexer != null && this.alphabeticIndexer.size() > 0 ? this.alphabeticIndexer.get(this.sections[i]) : 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        Iterator<Map.Entry<String, Integer>> iterator = this.alphabeticIndexer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() == i) {
                return this.sectionsArray.indexOf(entry.getKey());
            }
        }
        return 0;
    }

    static class ViewHolder {
        int hPosition;
        ImageView hImageViewPicture;
        ImageView hImageViewPictureCircle;
        TextView hTextViewName;
        TextView hTextViewMail;
        CheckBox hCheckBox;
    }

}
