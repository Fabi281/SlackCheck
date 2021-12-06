package de.dhbw.project.pcheap.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.activities.Details;
import de.dhbw.project.pcheap.databinding.CardItemBinding;
import de.dhbw.project.pcheap.pojo.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    ArrayList<Item> ItemList;

    public ItemAdapter(ArrayList<Item> itemList, Context context){
        this.ItemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_item, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.itemView.setOnClickListener(view -> {
            Intent in = new Intent(view.getContext(), Details.class);
            in.putParcelableArrayListExtra("itemList", ItemList);
            in.putExtra("position", position);
            in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(),
                            Pair.create(view.findViewById(R.id.ihPic), "imageTransition"),
                            Pair.create(view.findViewById(R.id.ihPic), "nameTransition"),
                            Pair.create(view.findViewById(R.id.ihPrice), "priceTransition"));

            view.getContext().startActivity(in, options.toBundle());
        });

        Item item = ItemList.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        private final CardItemBinding binding;

        public ItemViewHolder(CardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Item item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }
}
