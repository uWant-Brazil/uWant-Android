package br.com.uwant.models.adapters;/*
 * Copyright (C) 2013 InfocusWeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Cleibson Gomes
 * @date {08/08/14}
 *
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.lucasr.twowayview.TwoWayView;

import java.util.Date;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.utils.DateUtil;
import br.com.uwant.utils.PictureUtil;

public class FeedsAdapter extends BaseAdapter {

    private static float DEFAULT_RADIUS;
    private static int DEFAULT_MARGIN_BOTTOM;
    private static Drawable UWANT_DRAWABLE;
    private static Drawable UWANT_DRAWABLE_ACTIVE;
    private static Drawable USHARE_DRAWABLE;
    private static Drawable USHARE_DRAWABLE_ACTIVE;

    private final int WDP;
    private final int HDP;
    private final Context mContext;
    private final List<Action> mActions;
    private final View.OnClickListener mClickListener;
    private final DisplayImageOptions mOptions;

    public FeedsAdapter(Context context, List<Action> actions, View.OnClickListener listener) {
        this.mContext = context;
        this.mActions = actions;
        this.mClickListener = listener;
        this.mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        Resources res = context.getResources();
        DEFAULT_RADIUS = res.getDimension(R.dimen.cardview_default_radius);
        DEFAULT_MARGIN_BOTTOM = (int) res.getDimension(R.dimen.cardview_default_margin_bottom);
        UWANT_DRAWABLE = res.getDrawable(R.drawable.ic_feed_wantar);
        UWANT_DRAWABLE_ACTIVE = res.getDrawable(R.drawable.ic_feed_wantar_on);
        USHARE_DRAWABLE = res.getDrawable(R.drawable.ic_feed_compartilhar);
        USHARE_DRAWABLE_ACTIVE = res.getDrawable(R.drawable.ic_feed_compartilhar); // TODO selected...

        float dpi = context.getResources().getDisplayMetrics().density;
        WDP = (int) (dpi * 76);
        HDP = (int) (dpi * 76);
    }

    @Override
    public int getCount() {
        return mActions != null ? mActions.size() : 0;
    }

    @Override
    public Action getItem(int position) {
        return mActions != null ? mActions.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            linearLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, DEFAULT_MARGIN_BOTTOM);

            CardView cardView = new CardView(this.mContext);
            cardView.setLayoutParams(params);
            cardView.setRadius(DEFAULT_RADIUS);
            linearLayout.addView(cardView);

            View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_feeds, cardView, false);
            cardView.addView(contentView);

            convertView = linearLayout;

            vh.imageViewPicture = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_picture);
            vh.imageViewPictureDetail = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_pictureDetail);
            vh.imageButtonMenu = (ImageButton) convertView.findViewById(R.id.adapter_feeds_imageButton);
            vh.textViewSystemMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_systemMessage);
            vh.textViewUserMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_userMessage);
            vh.textViewWhen = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_when);
            vh.buttonUWants = (Button) convertView.findViewById(R.id.adapter_feeds_button_uwants);
            vh.buttonComments = (Button) convertView.findViewById(R.id.adapter_feeds_button_comments);
            vh.buttonShares = (Button) convertView.findViewById(R.id.adapter_feeds_button_shares);
            vh.twoWayView = (TwoWayView) convertView.findViewById(R.id.adapter_feed_twoWayView);

            vh.twoWayView.setOrientation(TwoWayView.Orientation.HORIZONTAL);
            vh.twoWayView.setItemMargin(10);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }

        Action action = getItem(position);
        Person from = action.getFrom();
        WishList wishList = action.getWishList();
        if (wishList != null) {
            List<Product> products = wishList.getProducts();
            WishListProductAdapter adapter = new WishListProductAdapter(mContext, products);
            vh.twoWayView.setAdapter(adapter);
        }

        String systemMessage = action.getMessage();
        String userMessage = action.getExtra();
        Date when = action.getWhen();
        String timeAgo = DateUtil.getTimeAgo(this.mContext, when);
        int uWantCount = action.getUWantsCount();
        int commentsCount = action.getCommentsCount();
        int sharesCount = action.getSharesCount();
        boolean uWant = action.isuWant();
        boolean uShare = action.isuShare();

        populatePicture(vh.imageViewPicture, vh.imageViewPictureDetail, from.getPicture());

        vh.textViewSystemMessage.setText(systemMessage);
        vh.textViewUserMessage.setText(userMessage);
        vh.textViewWhen.setText(timeAgo);
        vh.buttonUWants.setText(String.valueOf(uWantCount));
        vh.buttonComments.setText(String.valueOf(commentsCount));
        vh.buttonShares.setText(String.valueOf(sharesCount));

        vh.buttonUWants.setTag(position);
        vh.buttonComments.setTag(position);
        vh.buttonShares.setTag(position);
        vh.imageButtonMenu.setTag(position);

        vh.buttonUWants.setOnClickListener(this.mClickListener);
        vh.buttonComments.setOnClickListener(this.mClickListener);
        vh.buttonShares.setOnClickListener(this.mClickListener);
        vh.imageButtonMenu.setOnClickListener(this.mClickListener);

        Drawable drawableLeftUWant;
        if (uWant) {
            drawableLeftUWant = UWANT_DRAWABLE_ACTIVE;
        } else {
            drawableLeftUWant = UWANT_DRAWABLE;
        }

        Drawable drawableLeftUShare;
        if (uShare) {
            drawableLeftUShare = USHARE_DRAWABLE_ACTIVE;
        } else {
            drawableLeftUShare = USHARE_DRAWABLE;
        }

        vh.buttonUWants.setCompoundDrawablesWithIntrinsicBounds(drawableLeftUWant, null, null, null);
        vh.buttonShares.setCompoundDrawablesWithIntrinsicBounds(drawableLeftUShare, null, null, null);

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

    static class ViewHolder {
        ImageView imageViewPicture;
        ImageView imageViewPictureDetail;
        ImageButton imageButtonMenu;
        TextView textViewSystemMessage;
        TextView textViewUserMessage;
        TextView textViewWhen;
        Button buttonUWants;
        Button buttonComments;
        Button buttonShares;
        TwoWayView twoWayView;
    }

}
