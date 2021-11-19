package ca.bcit.co_green.ranking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.bcit.co_green.R;
import ca.bcit.co_green.User;

public class RankingRecyclerAdapter extends RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<User> users;

    public RankingRecyclerAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    public void updateData(ArrayList<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.rank_list_item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingRecyclerAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        User user = users.get(position);

        TextView num = cardView.findViewById(R.id.rank_item_num);
        TextView name = cardView.findViewById(R.id.rank_item_user);
        TextView co2 = cardView.findViewById(R.id.rank_item_co2);

        num.setText(String.valueOf(position + 1));
        name.setText(user.getName());
        co2.setText(String.valueOf(user.getCo2()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
