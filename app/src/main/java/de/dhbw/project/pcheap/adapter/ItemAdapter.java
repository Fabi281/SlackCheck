package de.dhbw.project.pcheap.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
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
    private final Context context;

    public ItemAdapter(ArrayList<Item> itemList, Context context){
        this.ItemList = itemList;
        this.context = context;
    }

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
            in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(),
                            Pair.create(view.findViewById(R.id.ihPic), "imageTransition"),
                            Pair.create(view.findViewById(R.id.ihName), "nameTransition"),
                            Pair.create(view.findViewById(R.id.ihPrice), "priceTransition"));

            view.getContext().startActivity(in, options.toBundle());
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        // I have no idea why this works but i´ll never touch it again
        try {
            executor.submit(new DownloadImageTask(holder.picture, i.getImageUrl())).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        holder.name.setText(i.getName());
        holder.price.setText(String.format(
                context.getResources().getString(R.string.formatted_price),
                i.getPrice()));
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
