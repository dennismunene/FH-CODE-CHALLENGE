package com.dmk.fh.org.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dmk.fh.org.model.Sponsor;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

public class SponsorAutoCompleteAdapter extends RealmBaseAdapter<Sponsor> implements Filterable {

    private Context context;
    OrderedRealmCollection<Sponsor> data;
    public SponsorAutoCompleteAdapter(@Nullable OrderedRealmCollection<Sponsor> data,Context context) {
        super(data);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(i).getName());
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new SponsorAutoCompleteAdapter.MyNamesFilter(this);
    }

    // filtering
    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        if(text == null || "".equals(text)) {
            updateData(Realm.getDefaultInstance().where(Sponsor.class).findAllAsync());
        } else {
            updateData(Realm.getDefaultInstance().where(Sponsor.class)
                    .contains("name", text, Case.INSENSITIVE)
                    .findAllAsync());
        }
    }



    private class MyNamesFilter
            extends Filter {
        private final SponsorAutoCompleteAdapter adapter;

        private MyNamesFilter(SponsorAutoCompleteAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if(constraint!=null)
            adapter.filterResults(""+constraint.toString());
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Sponsor sponsor = (Sponsor)resultValue;
            return sponsor.getName();
        }
    }
}
