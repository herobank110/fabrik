package uk.co.davidkanekanian.fabrik;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import uk.co.davidkanekanian.fabrik.persistence.Chain;

class BrowseChainsAdapter extends RecyclerView.Adapter<BrowseChainsAdapter.ChainViewHolder> {
    public BrowseChains managerActivity;
    private Chain[] chainList = {};

    protected static class ChainViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ConstraintLayout containerView;
        public BrowseChains managerActivity;

        public ChainViewHolder(@NonNull View itemView) {
            super(itemView);
            containerView = itemView.findViewById(R.id.chain_list_row);
            textView = itemView.findViewById(R.id.chain_row_text);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Chain myChain = (Chain)containerView.getTag();
                    managerActivity.openChain(myChain.id);
                }
            });
        }
    }

    public void reload() {
        chainList = MainActivity.database.chainDao().getAllChains();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chain_list_row, parent, false);
        final ChainViewHolder chainViewHolder = new ChainViewHolder(view);
        chainViewHolder.managerActivity = managerActivity;
        return chainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChainViewHolder holder, int position) {
        Chain current = chainList[position];
        holder.textView.setText(current.name);
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return chainList.length;
    }
}
