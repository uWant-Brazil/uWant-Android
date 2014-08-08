package br.com.uwant.flow;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import br.com.uwant.R;
import br.com.uwant.utils.DebugUtil;

public class MainActivity extends ActionBarActivity {

    SlidingPaneLayout pane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new PaneListener());

        if (!pane.isSlideable()) {
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(false);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class PaneListener implements SlidingPaneLayout.PanelSlideListener{

        @Override
        public void onPanelSlide(View view, float v) {
            DebugUtil.debug("Panel sliding");
        }

        @Override
        public void onPanelOpened(View view) {
            DebugUtil.debug("Panel opened");
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(true);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(false);
        }

        @Override
        public void onPanelClosed(View view) {
            DebugUtil.debug("Panel closed");
            getFragmentManager().findFragmentById(R.id.fragmet_left).setHasOptionsMenu(false);
            getFragmentManager().findFragmentById(R.id.fragmet_right).setHasOptionsMenu(true);
        }
    }
}
