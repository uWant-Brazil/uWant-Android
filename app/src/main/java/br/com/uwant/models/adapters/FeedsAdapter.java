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
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.MainActivity;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.utils.PictureUtil;

public class FeedsAdapter extends BaseAdapter {

    private static final int DEFAULT_TIME_AGO = R.string.text_feeds_just_now;
    private static final int ANOS = R.string.text_feeds_years;
    private static final int MESES = R.string.text_feeds_months;
    private static final int DIAS = R.string.text_feeds_days;
    private static final int HORAS = R.string.text_feeds_hours;
    private static final int MINUTOS = R.string.text_feeds_minutes;
    private static final int HÁ = R.string.text_feeds_ha;
    private static float DEFAULT_RADIUS;

    private final Context mContext;
    private final List<Action> mActions;

    public FeedsAdapter(Context context, List<Action> actions) {
        this.mContext = context;
        this.mActions = actions;

        DEFAULT_RADIUS = context.getResources().getDimension(R.dimen.cardview_default_radius);
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
        if (convertView == null) {
            CardView cardView = new CardView(this.mContext);
            cardView.setRadius(DEFAULT_RADIUS);

            View contentView = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_feeds, cardView, false);
            cardView.addView(contentView);

            convertView = cardView;
        }

        final ImageView hImageViewPicture = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_picture);
        final ImageView hImageViewPictureDetail = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_pictureDetail);
//        final ImageView hImageViewProduct = (ImageView) convertView.findViewById(R.id.adapter_feeds_imageView_product);
        final TextView hTextViewSystemMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_systemMessage);
        final TextView hTextViewUserMessage = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_userMessage);
        final TextView hTextViewWhen = (TextView) convertView.findViewById(R.id.adapter_feeds_textView_when);
        final Button hButtonUWants = (Button) convertView.findViewById(R.id.adapter_feeds_button_uwants);
        final Button hButtonComments = (Button) convertView.findViewById(R.id.adapter_feeds_button_comments);
        final Button hButtonShares = (Button) convertView.findViewById(R.id.adapter_feeds_button_shares);

        Action action = getItem(position);
        Person from = action.getFrom();
        Multimedia multimedia = from.getPicture();

        String systemMessage = action.getMessage();
        String userMessage = action.getExtra();
        Date when = action.getWhen();
        String timeAgo = getTimeAgo(when);
        int uWantCount = action.getUWantsCount();
        int commentsCount = action.getCommentsCount();
        int sharesCount = action.getSharesCount();

        // TODO Exibir a imagem do produto. Verificar como está o envio do servidor!
        hTextViewSystemMessage.setText(systemMessage);
        hTextViewUserMessage.setText(userMessage);
        hTextViewWhen.setText(timeAgo);
        hButtonUWants.setText(String.valueOf(uWantCount));
        hButtonComments.setText(String.valueOf(commentsCount));
        hButtonShares.setText(String.valueOf(sharesCount));
        populatePicture(hImageViewPicture, hImageViewPictureDetail, multimedia);

        return convertView;
    }

    private void populatePicture(final ImageView hImageViewPicture, final ImageView hImageViewPictureDetail, Multimedia multimedia) {
        if (multimedia != null) {
            Uri uri = multimedia.getUri();
            if (uri != null) {
                Picasso.with(this.mContext)
                        .load(uri)
                        .placeholder(R.drawable.ic_semfoto)
                        .into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bitmap = PictureUtil.cropToFit(bitmap);
                                bitmap = PictureUtil.scale(bitmap, hImageViewPicture);
                                bitmap = PictureUtil.circle(bitmap);
                                hImageViewPicture.setImageBitmap(bitmap);
                                hImageViewPictureDetail.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                hImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                                hImageViewPictureDetail.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                hImageViewPicture.setImageDrawable(placeHolderDrawable);
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
        StringBuilder builder = new StringBuilder();

        if (when != null) {
            builder.append(HÁ);

            Date now = new Date();
            long diff = now.getTime() - when.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            if (diffMinutes > 59) {
                long diffHours = diffMinutes / 60;
                if (diffHours > 23) {
                    long diffDays = diffHours / 24;
                    if (diffDays > 29) {
                        long diffMonths = diffDays / 30;
                        if (diffMonths > 11) {
                            long diffYears = diffMonths / 12;
                            builder.append(diffYears + resources.getString(ANOS)); // FINALMENTE...
                        } else {
                            builder.append(diffMonths + resources.getString(MESES));
                        }
                    } else {
                        builder.append(diffDays + resources.getString(DIAS));
                    }
                } else {
                    builder.append(diffHours + resources.getString(HORAS));
                }
            } else {
                builder.append(diffMinutes + resources.getString(MINUTOS));
            }
        } else {
            builder.append(resources.getString(DEFAULT_TIME_AGO));
        }

        return builder.toString();
    }

}
