package br.com.uwant.models.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Date;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Comment;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.utils.DateUtil;
import br.com.uwant.utils.PictureUtil;

/**
 * Created by felipebenezi on 18/08/14.
 */
public class FeedCommentsAdapter extends BaseAdapter {

    private final int WDP;
    private final int HDP;
    private final Context mContext;
    private final View.OnClickListener mClickListener;
    private final List<Comment> mComments;
    private final DisplayImageOptions mOptions;

    public FeedCommentsAdapter(Context context, View.OnClickListener listener, List<Comment> comments) {
        this.mContext = context;
        this.mClickListener = listener;
        this.mComments = comments;
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
    }

    @Override
    public int getCount() {
        return this.mComments == null ? 0 : this.mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return this.mComments == null ? null : this.mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_feed_comments, viewGroup, false);

            holder.hTextViewName = (TextView) convertView.findViewById(R.id.adapter_feed_comment_textView_user);
            holder.hTextViewComment = (TextView) convertView.findViewById(R.id.adapter_feed_comment_textView_comment);
            holder.hTextViewSince = (TextView) convertView.findViewById(R.id.adapter_feed_comment_textView_since);
            holder.hTextViewUWantCount = (TextView) convertView.findViewById(R.id.adapter_feed_comment_textView_uwant);
            holder.hImageViewUWant = (ImageView) convertView.findViewById(R.id.adapter_feed_comment_imageView_uwant);
            holder.hImageViewPicture = (ImageView) convertView.findViewById(R.id.adapter_feed_comment_imageView_picture);
            holder.hImageViewPictureDetail = (ImageView) convertView.findViewById(R.id.adapter_feed_comment_imageView_pictureDetail);

            holder.hFrameLayoutPicture = (FrameLayout) convertView.findViewById(R.id.adapter_feed_comment_frameLayout_picture);
            holder.hFrameLayoutPicture.setOnClickListener(this.mClickListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = getItem(position);
        Person who = comment.getWho();
        Multimedia picture = who.getPicture();
        Date since = comment.getSince();

        String text = comment.getText();
        String name = who.getName();
        String when = DateUtil.getTimeAgo(this.mContext, since);
        int count = comment.getUWantsCount();

        holder.hTextViewName.setText(name);
        holder.hTextViewComment.setText(text);
        holder.hTextViewSince.setText(when);
        holder.hTextViewUWantCount.setText(count > 0 ? String.format("+%d", count) : ":-(");
        holder.hImageViewUWant.setImageResource(comment.isuWant() ? R.drawable.ic_feed_wantar_on : R.drawable.ic_comentario_wantar_cinza);

        populatePicture(holder.hImageViewPicture, holder.hImageViewPictureDetail, picture);

        holder.hFrameLayoutPicture.setTag(position);

        return convertView;
    }

    private void populatePicture(final ImageView hImageViewPicture, final ImageView hImageViewPictureDetail, final Multimedia multimedia) {
        if (multimedia != null) {
            Bitmap bitmap = multimedia.getBitmap();
            String url = multimedia.getUrl();
            if (bitmap != null) {
                hImageViewPicture.setImageBitmap(bitmap);
                hImageViewPictureDetail.setVisibility(View.VISIBLE);
            } else if (url != null && !url.isEmpty()) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadImage(url, this.mOptions, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        hImageViewPicture.setImageResource(R.drawable.ic_contatos_semfoto);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        hImageViewPicture.setImageResource(R.drawable.ic_contatos_semfoto);
                        hImageViewPictureDetail.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                        bitmap = PictureUtil.cropToFit(bitmap);
                        bitmap = PictureUtil.scale(bitmap, WDP, HDP);
                        bitmap = PictureUtil.circle(bitmap);
                        hImageViewPicture.setImageBitmap(bitmap);
                        hImageViewPictureDetail.setVisibility(View.VISIBLE);
                        multimedia.setBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        hImageViewPicture.setImageResource(R.drawable.ic_contatos_semfoto);
                        hImageViewPictureDetail.setVisibility(View.INVISIBLE);
                    }

                });
            } else {
                hImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                hImageViewPictureDetail.setVisibility(View.INVISIBLE);
            }
        } else {
            hImageViewPicture.setImageResource(R.drawable.ic_semfoto);
            hImageViewPictureDetail.setVisibility(View.INVISIBLE);
        }
    }

    private static class ViewHolder {
        FrameLayout hFrameLayoutPicture;
        TextView hTextViewName;
        TextView hTextViewComment;
        TextView hTextViewSince;
        TextView hTextViewUWantCount;
        ImageView hImageViewUWant;
        ImageView hImageViewPicture;
        ImageView hImageViewPictureDetail;
    }

}
