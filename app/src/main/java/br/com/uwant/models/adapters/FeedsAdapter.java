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

import java.util.Date;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.utils.PictureUtil;

public class FeedsAdapter extends BaseAdapter {

    private static final int DEFAULT_TIME_AGO = R.string.text_feeds_just_now;
    private static final int YEARS = R.plurals.text_feeds_years;
    private static final int MONTHS = R.plurals.text_feeds_months;
    private static final int DAYS = R.plurals.text_feeds_days;
    private static final int HOURS = R.plurals.text_feeds_hours;
    private static final int MINUTES = R.plurals.text_feeds_minutes;

    private static float DEFAULT_RADIUS;
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
        TextView hTextViewSystemMessage;
        ImageView hImageViewPicture;
        ImageView hImageViewPictureDetail;
        ImageButton hImageButtonMenu;
        TextView hTextViewUserMessage;
        TextView hTextViewWhen;
        Button hButtonUWants;
        Button hButtonComments;
        Button hButtonShares;
        if (convertView == null) {
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            linearLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 15);

            CardView cardView = new CardView(this.mContext);
            cardView.setLayoutParams(params);
            cardView.setRadius(DEFAULT_RADIUS);
            linearLayout.addView(cardView);

            View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_feeds, cardView, false);
            cardView.addView(contentView);

            convertView = linearLayout;
        }

        hImageViewPicture = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_picture);
        hImageViewPictureDetail = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_pictureDetail);
        hImageButtonMenu = (ImageButton) convertView.findViewById(R.id.adapter_feeds_imageButton);
        hTextViewSystemMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_systemMessage);
        hTextViewUserMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_userMessage);
        hTextViewWhen = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_when);
        hButtonUWants = (Button) convertView.findViewById(R.id.adapter_feeds_button_uwants);
        hButtonComments = (Button) convertView.findViewById(R.id.adapter_feeds_button_comments);
        hButtonShares = (Button) convertView.findViewById(R.id.adapter_feeds_button_shares);
        
        Action action = getItem(position);
        Person from = action.getFrom();

        String systemMessage = action.getMessage();
        String userMessage = action.getExtra();
        Date when = action.getWhen();
        String timeAgo = getTimeAgo(when);
        int uWantCount = action.getUWantsCount();
        int commentsCount = action.getCommentsCount();
        int sharesCount = action.getSharesCount();
        boolean uWant = action.isuWant();
        boolean uShare = action.isuShare();

        populatePicture(hImageViewPicture, hImageViewPictureDetail, from.getPicture());

        hTextViewSystemMessage.setText(systemMessage);
        hTextViewUserMessage.setText(userMessage);
        hTextViewWhen.setText(timeAgo);
        hButtonUWants.setText(String.valueOf(uWantCount));
        hButtonComments.setText(String.valueOf(commentsCount));
        hButtonShares.setText(String.valueOf(sharesCount));

        hButtonUWants.setTag(position);
        hButtonComments.setTag(position);
        hButtonShares.setTag(position);
        hImageButtonMenu.setTag(position);

        hButtonUWants.setOnClickListener(this.mClickListener);
        hButtonComments.setOnClickListener(this.mClickListener);
        hButtonShares.setOnClickListener(this.mClickListener);
        hImageButtonMenu.setOnClickListener(this.mClickListener);

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

        hButtonUWants.setCompoundDrawablesWithIntrinsicBounds(drawableLeftUWant, null, null, null);
        hButtonShares.setCompoundDrawablesWithIntrinsicBounds(drawableLeftUShare, null, null, null);

        return convertView;
    }

    private void populatePicture(final ImageView hImageViewPicture, final ImageView hImageViewPictureDetail, Multimedia multimedia) {
        if (multimedia != null) {
            String url = multimedia.getUrl();
            if (url != null && !url.isEmpty()) {
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

    private String getTimeAgo(Date when) {
        Resources resources = this.mContext.getResources();

        String timeAgo;
        if (when != null) {
            Date now = new Date();
            long diff = now.getTime() - when.getTime();
            int diffMinutes = (int) diff / (60 * 1000);
            if (diffMinutes > 59) {
                int diffHours = diffMinutes / 60;
                if (diffHours > 23) {
                    int diffDays = diffHours / 24;
                    if (diffDays > 29) {
                        int diffMonths = diffDays / 30;
                        if (diffMonths > 11) {
                            int diffYears = diffMonths / 12;
                            timeAgo = resources.getQuantityString(YEARS, diffYears, diffYears); // FINALMENTE...
                        } else {
                            timeAgo = resources.getQuantityString(MONTHS, diffMonths, diffMonths);
                        }
                    } else {
                        timeAgo = resources.getQuantityString(DAYS, diffDays, diffDays);
                    }
                } else {
                    timeAgo = resources.getQuantityString(HOURS, diffHours, diffHours);
                }
            } else if (diffMinutes > 0) {
                timeAgo = resources.getQuantityString(MINUTES, diffMinutes, diffMinutes);
            } else {
                timeAgo = resources.getString(DEFAULT_TIME_AGO);
            }
        } else {
            timeAgo = resources.getString(DEFAULT_TIME_AGO);
        }

        return timeAgo;
    }

}
