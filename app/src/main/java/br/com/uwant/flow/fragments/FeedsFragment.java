package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.FeedsModel;

public class FeedsFragment extends Fragment implements IRequest.OnRequestListener<List<Action>> {

    public static final String TAG = "feedsFragment";
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;
    private static final FeedsModel MODEL = new FeedsModel();

    private List<Action> mActions;
    private FeedsAdapter mFeedsAdapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActions = new ArrayList<Action>(25);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeedsAdapter = new FeedsAdapter(getActivity(), mActions);

        mGridView = (GridView) view.findViewById(R.id.main_gridView);
        mGridView.setNumColumns(1);
        mGridView.setAdapter(mFeedsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActions.clear();
        mFeedsAdapter.notifyDataSetChanged();

        MODEL.setStartIndex(DEFAULT_START_INDEX);
        MODEL.setEndIndex(DEFAULT_END_INDEX);

        Requester.executeAsync(MODEL, this);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Action> result) {
        if (result != null) {
            mActions.addAll(result);
            mFeedsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

}
