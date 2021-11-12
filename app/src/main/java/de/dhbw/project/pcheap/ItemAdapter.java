package de.dhbw.project.pcheap;

import android.content.Intent;
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

    public ItemAdapter(List<Item> itemList){ this.ItemList = itemList; }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hit, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        Item i = ItemList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent in = new Intent(view.getContext(), Details.class);
                in.putExtra("object", i);
                view.getContext().startActivity(in);
            }
        });

        Picasso.get().load(i.getImageUrl()).into(holder.picture);
        holder.name.setText(i.getName());
        holder.price.setText(Double.toString(i.getPrice()));
    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        ImageView picture;
        TextView name;
        TextView price;

        ItemViewHolder(View itemView){
            super(itemView);

            picture = itemView.findViewById(R.id.ihPic);
            name = itemView.findViewById(R.id.ihName);
            price = itemView.findViewById(R.id.ihPrice);
        }
    }
}
