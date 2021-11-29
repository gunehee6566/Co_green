package ca.bcit.co_green.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ca.bcit.co_green.CO2;
import ca.bcit.co_green.R;
import ca.bcit.co_green.User;
import ca.bcit.co_green.ranking.RankingRecyclerAdapter;

public class HomeFragment extends Fragment {
    private PieChart pieChart;
    private FirebaseAuth fAuth;
    private TextView nameText;
    private RecyclerView recyclerView;
    private HomeRecyclerAdapter recyclerAdapter;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pieChart = view.findViewById(R.id.home_pieChart);
        fAuth = FirebaseAuth.getInstance();
        nameText = view.findViewById(R.id.home_username);

        recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerView);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerAdapter = new HomeRecyclerAdapter(getContext(), new ArrayList<CO2>());
        recyclerView.setAdapter(recyclerAdapter);

        getMyInfo((userName)->{
            nameText.setText(userName);
        });
        getMyReports((reports)->{
            Map<String, Integer> typeAmountMap = new HashMap<>();

            //if some reports are from the same day, we want them to be shown in the same row.
            ArrayList<CO2> reportSameDayCollected = new ArrayList<>();
            boolean added = false;
            for(CO2 report : reports) {
                for(CO2 collected : reportSameDayCollected) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                    if(fmt.format(report.getTimestamp()).equals(fmt.format(collected.getTimestamp()))) {
                        collected.setCo2(report.getCo2() + collected.getCo2());
                        collected.setDriveDistance("" + (Float.parseFloat(report.getDriveDistance()) + Float.parseFloat(collected.getDriveDistance())));
                        collected.setElecUsed("" + (Float.parseFloat(report.getElecUsed()) + Float.parseFloat(collected.getElecUsed())));
                        added = true;
                    }
                }
                if(!added) {
                    reportSameDayCollected.add(report);
                    added = false;
                }
            }
            for(CO2 report : reportSameDayCollected) {
                Log.d("Electricity", "" + (int)Float.parseFloat(report.getElecUsed()));
                Log.d("Drive", "" + (int)Float.parseFloat(report.getDriveDistance()));
                typeAmountMap.put("Electricity", typeAmountMap.get("Electricity") == null?(int)Float.parseFloat(report.getElecUsed()):typeAmountMap.get("Electricity") + (int)Float.parseFloat(report.getElecUsed()));
                typeAmountMap.put("Drive", typeAmountMap.get("Drive") == null?(int)Float.parseFloat(report.getDriveDistance()):typeAmountMap.get("Drive") + (int)Float.parseFloat(report.getDriveDistance()));
            }
            Log.d("!!!!!!!!!!!!!!!!", "!!!!!!!!!!!!!");
            Log.d("Electricity", "" + typeAmountMap.get("Electricity"));
            Log.d("Drive", "" + typeAmountMap.get("Drive"));
            recyclerAdapter.updateData(reportSameDayCollected);
            if (reports.isEmpty()) {
                view.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.empty_view).setVisibility(View.GONE);
            }
            initPieChart();
            showPieChart(typeAmountMap);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void showPieChart(Map<String, Integer> data){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#9F43CC"));
        colors.add(Color.parseColor("#EBA10F"));
        colors.add(Color.parseColor("#2B87E3"));
        colors.add(Color.parseColor("#0CA85D"));

        for(String type: data.keySet()){
            pieEntries.add(new PieEntry(data.get(type).floatValue(), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        pieChart.setData(pieDataSet.getEntryCount() == 0?null:pieData);
        pieChart.invalidate();
    }

    private void initPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setRotationAngle(0);
        pieChart.setHighlightPerTapEnabled(true);
    }

    private void getMyReports(Consumer<ArrayList<CO2>> onFinish) {
        FirebaseUser user = fAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ranking");
        myRef.orderByChild("id").equalTo(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<CO2> reports = new ArrayList<>();
                    task.getResult().getChildren().forEach(child->{
                        reports.add(child.getValue(CO2.class));
                    });
                    onFinish.accept(reports);
                } else {
                    onFinish.accept(null);
                }
            }
        });
    }

    private void getMyInfo(Consumer<String> onFinish) {
        FirebaseUser user = fAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        myRef.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    onFinish.accept(String.valueOf(task.getResult().child("name").getValue()));
                } else {
                    onFinish.accept(null);
                }
            }
        });
    }
}