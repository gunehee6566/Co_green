package ca.bcit.co_green.home;

import static java.text.DateFormat.getDateTimeInstance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.bcit.co_green.CO2;
import ca.bcit.co_green.R;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<CO2> reports;

    public HomeRecyclerAdapter(Context context, ArrayList<CO2> reports) {
        this.context = context;
        this.reports = reports;
    }

    public void updateData(ArrayList<CO2> newCO2) {
        this.reports.clear();
        this.reports.addAll(newCO2);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.home_list_item, parent, false);
        return new HomeRecyclerAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        CO2 report = reports.get(position);

        TextView date = cardView.findViewById(R.id.home_item_date);
        TextView drive = cardView.findViewById(R.id.home_item_drive);
        TextView elec = cardView.findViewById(R.id.home_item_elec);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM", Locale.ENGLISH);
        String formatted = dateFormat.format(report.getTimestamp());
        date.setText(formatted);
        drive.setText(String.valueOf(report.getDriveDistance()));
        elec.setText(String.valueOf(report.getElecUsed()));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
