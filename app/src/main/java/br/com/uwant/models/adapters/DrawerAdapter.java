package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.uwant.R;

public class DrawerAdapter extends BaseAdapter {

    private final Context mContext;
    private final String[] mOptions;

    public DrawerAdapter(Context context) {
        this.mContext = context;
        this.mOptions = context.getResources().getStringArray(R.array.options_drawer);
    }

    @Override
    public int getCount() {
        return mOptions.length;
    }

    @Override
    public String getItem(int position) {
        return mOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_drawer, parent, false);
            holder.hTextViewOption = (TextView) convertView.findViewById(R.id.adapter_drawer_textView_option);
            holder.hImageViewIcon = (ImageView) convertView.findViewById(R.id.adapter_drawer_imageView_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String option = getItem(position);
        int iconRes = getIcon(position);

        holder.hTextViewOption.setText(option);
        holder.hImageViewIcon.setImageResource(iconRes);

        return convertView;
    }

    private int getIcon(int position) {
        int icon;
        switch (position) {
            case 0:
                // Minhas listas
                icon = R.drawable.ic_panel_listas;
                break;
            case 1:
                // Amigos
                icon = R.drawable.ic_panel_amigos;
                break;
            case 2:
                // Configuracoes
                icon = R.drawable.ic_panel_conf;
                break;
//            case 3:
//                // Sobre
//                icon = R.drawable.ic_panel_sobre;
//                break;
//            case 4:
//                // Sair
//                icon = R.drawable.ic_panel_sair;
//                break;
            default:
                icon = R.drawable.ic_launcher;
                break;
        }
        return icon;
    }

    private static class ViewHolder {
        ImageView hImageViewIcon;
        TextView hTextViewOption;
    }

}
