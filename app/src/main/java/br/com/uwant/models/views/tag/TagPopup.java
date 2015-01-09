package br.com.uwant.models.views.tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Person;

public class TagPopup extends PopupWindow {

    private final int WIDTH;
    private final int HEIGHT;
    private final Context mContext;
    private final View mAnchorView;
    private final List<Person> mPersons;
    private ListView mListView;
    private AdapterView.OnItemClickListener mListener;

    public TagPopup(Context context, TagEditText editText, List<Person> persons) {
        super();
        this.mContext = context;
        this.mAnchorView = editText;
        this.mPersons = persons;
        this.mListener = editText;

        int margin = (int) (10 * context.getResources().getDisplayMetrics().density);
        this.WIDTH = editText.getWidth();
        this.HEIGHT = (editText.getHeight() + margin) * persons.size();
        configure();
    }

    private void configure() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        this.mListView = (ListView) inflater.inflate(R.layout.popup_tag, null);
        this.mListView.setOnItemClickListener(this.mListener);
        this.mListView.setAdapter(new ArrayAdapter<Person>(this.mContext, android.R.layout.simple_list_item_1, this.mPersons));

        setContentView(this.mListView);
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setFocusable(true);
        setBackgroundDrawable(this.mContext.getResources().getDrawable(R.color.GRAY_BACKGROUND));
    }

    public void show() {
        showAsDropDown(this.mAnchorView, 0, 10);
    }

}
