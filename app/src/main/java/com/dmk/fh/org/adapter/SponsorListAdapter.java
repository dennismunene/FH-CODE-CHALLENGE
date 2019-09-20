package com.dmk.fh.org.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk.fh.org.R;
import com.dmk.fh.org.model.Child;
import com.dmk.fh.org.model.Sponsor;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class SponsorListAdapter extends RealmRecyclerViewAdapter<Sponsor, SponsorListAdapter.MyViewHolder> implements Filterable {


    public SponsorListAdapter(OrderedRealmCollection<Sponsor> data){
        super(data,true);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_childlist, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Sponsor sponsor = getItem(position);

        holder.txtName.setText(""+sponsor.getName());
        holder.txtCountry.setText("From: "+sponsor.getCountry());
        holder.txtAge.setText(""+sponsor.getAge()+" Years");
    }

    @Override
    public Filter getFilter() {
        return new SponsorListAdapter.MyNamesFilter(this);
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
        private final SponsorListAdapter adapter;

        private MyNamesFilter(SponsorListAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtCountry,txtAge;
        MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCountry = itemView.findViewById(R.id.txtCountry);
            txtAge = itemView.findViewById(R.id.txtAge);

        }
    }
}
