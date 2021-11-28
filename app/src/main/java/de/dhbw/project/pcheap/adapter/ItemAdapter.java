package de.dhbw.project.pcheap.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.dhbw.project.pcheap.pojo.DownloadImageTask;
import de.dhbw.project.pcheap.pojo.Item;
import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.activities.Details;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    ArrayList<Item> ItemList;

    public ItemAdapter(ArrayList<Item> itemList){ this.ItemList = itemList; }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hit, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        Item i = ItemList.get(position);

        holder.itemView.setOnClickListener(view -> {
            Intent in = new Intent(view.getContext(), Details.class);
            in.putParcelableArrayListExtra("itemList", ItemList);
            in.putExtra("position", position);
            view.getContext().startActivity(in);
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        // I have no idea why this works but i´ll never touch it again
        try {
            executor.submit(new DownloadImageTask(holder.picture, i.getImageUrl())).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


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
