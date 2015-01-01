package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.ContactsAdapter;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.UserSearchModel;

public class UWantFragment extends ContactsFragment implements TextView.OnEditorActionListener,
        IRequest.OnRequestListener<List<Person>> {

    private EditText mEditTextSearch;
    private ContactsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uwant, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter = new ContactsAdapter(getActivity(), this.mGridView, this.mPersons);
        this.mEditTextSearch = (EditText) view.findViewById(R.id.uwant_editText_search);
        this.mEditTextSearch.setOnEditorActionListener(this);
    }

    @Override
    protected void loadPersons() {
        // Do nothing...
    }

    @Override
    protected BaseAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (event == null || !event.isShiftPressed()) {
                String s = mEditTextSearch.getText().toString();
                if (s.length() > 2) {
                    UserSearchModel model = new UserSearchModel();
                    model.setQuery(mEditTextSearch.getText().toString());
                    Requester.executeAsync(model, this);
                } else {
                    Toast.makeText(getActivity(), R.string.text_fill_three_characteres, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void onPreExecute() {
        mPersons.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExecute(List<Person> result) {
        mPersons.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

}
