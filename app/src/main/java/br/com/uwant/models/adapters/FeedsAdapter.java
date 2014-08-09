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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import br.com.uwant.flow.MainActivity;
import br.com.uwant.models.classes.Action;

public class FeedsAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Action> mActions;

    public FeedsAdapter(Context context, List<Action> actions) {
        this.mContext = context;
        this.mActions = actions;
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
        return convertView;
    }
}
