package com.reynaldiwijaya.foodapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reynaldiwijaya.foodapp.BuildConfig;
import com.reynaldiwijaya.foodapp.R;
import com.reynaldiwijaya.foodapp.model.DataMakananItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private onItemClick click;


    Context ctx;
    List<DataMakananItem> listDataMakanan;

    public FoodAdapter(Context ctx, List<DataMakananItem> listDataMakanan) {
        this.ctx = ctx;
        this.listDataMakanan = listDataMakanan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_food, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemDesk.setText(listDataMakanan.get(position).getMakanan());
        holder.itemTime.setText(listDataMakanan.get(position).getInsertTime());
        Picasso.with(ctx).load(BuildConfig.IMAGE_URL + listDataMakanan.get(position).getFotoMakanan())
                .error(R.drawable.noimage)
                .placeholder(R.drawable.noimage)
                .into(holder.itemImages);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (listDataMakanan == null) return 0;
        else return listDataMakanan.size();
    }

    public interface onItemClick {
        void onItemClick(int position);
    }

    public void setOnClick(onItemClick onClick) {
        click = onClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_images)
        ImageView itemImages;
        @BindView(R.id.item_desk)
        TextView itemDesk;
        @BindView(R.id.item_time)
        TextView itemTime;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
