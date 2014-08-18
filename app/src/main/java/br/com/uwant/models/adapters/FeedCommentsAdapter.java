package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Comment;

/**
 * Created by felipebenezi on 18/08/14.
 */
public class FeedCommentsAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Comment> mComments;

    public FeedCommentsAdapter(Context context, List<Comment> comments) {
        this.mContext = context;
        this.mComments = comments;
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

            holder.hTextViewComment = (TextView) convertView.findViewById(R.id.adapter_feed_comment_textView_comment);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = getItem(position);
        String text = comment.getText();

        holder.hTextViewComment.setText(text);

        return convertView;
    }

    private static class ViewHolder {
        TextView hTextViewComment;
    }

}
