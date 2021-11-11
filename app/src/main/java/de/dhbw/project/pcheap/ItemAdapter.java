package de.dhbw.project.pcheap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    List<Item> ItemList;
    private OnItemListener mOnItemListener;

    public ItemAdapter(List<Item> itemList, OnItemListener onItemListener){
        this.ItemList = itemList;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hit, parent, false);
        return new ItemViewHolder(v, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item i = ItemList.get(position);
        Picasso.get().load(i.getImageUrl()).into(holder.picture);
        holder.name.setText(i.getName());
        holder.price.setText(Double.toString(i.getPrice()));
    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView picture;
        TextView name;
        TextView price;

        OnItemListener onItemListener;

        ItemViewHolder(View itemView, OnItemListener onitemListener){
            super(itemView);

            picture = itemView.findViewById(R.id.ihPic);
            name = itemView.findViewById(R.id.ihName);
            price = itemView.findViewById(R.id.ihPrice);
            this.onItemListener = onitemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
