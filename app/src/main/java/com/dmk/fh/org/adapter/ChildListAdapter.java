package com.dmk.fh.org.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk.fh.org.AddChildActivity;
import com.dmk.fh.org.R;
import com.dmk.fh.org.model.Child;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ChildListAdapter extends RealmRecyclerViewAdapter<Child, ChildListAdapter.MyViewHolder> implements Filterable {



    Context context;
    public ChildListAdapter(OrderedRealmCollection<Child> data,Context context){
        super(data,true);
        this.context =context;

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

        Child child = getItem(position);

        holder.txtName.setText(""+child.getName());
        holder.txtCountry.setText("From: "+child.getCountry());
        holder.txtAge.setText(""+child.getAge()+" Years");

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, AddChildActivity.class)
                        .putExtra("child",child));
            }
        });
    }



    @Override
    public Filter getFilter() {
        return new MyNamesFilter(this);
    }


    // filtering
    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        if(text == null || "".equals(text)) {
            updateData(Realm.getDefaultInstance().where(Child.class).findAllAsync());
        } else {
            updateData(Realm.getDefaultInstance().where(Child.class)
                    .contains("name", text, Case.INSENSITIVE)
                    .findAllAsync());
        }
    }



    private class MyNamesFilter
            extends Filter {
        private final ChildListAdapter adapter;

        private MyNamesFilter(ChildListAdapter adapter) {
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
