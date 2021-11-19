package ca.bcit.co_green.ranking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ca.bcit.co_green.CO2;
import ca.bcit.co_green.R;
import ca.bcit.co_green.User;

public class RankingFragment extends Fragment {
    private RecyclerView recyclerView;
    private RankingRecyclerAdapter recyclerAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rank_recycler);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);

        recyclerAdapter = new RankingRecyclerAdapter(getContext(), new ArrayList<User>());
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getReportByUser((Map<String, ArrayList<CO2>> reportsByUser)->{
            getUserInfo((ArrayList<User> userInfo)->{
                userInfo.forEach(user->{
                    ArrayList<CO2> reportForThisUser = reportsByUser.get(user.getId());
                    user.setCo2((reportForThisUser == null)?0f:reportForThisUser.stream().map(CO2::getCo2).reduce(0f, Float::sum));
                });
                //at this point, userInfo has a list of users with ranked order.
                recyclerAdapter.updateData(userInfo);
            });
        });

        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    private void getReportByUser(Consumer<Map<String, ArrayList<CO2>>> onFinish) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ranking");
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    Map<String, ArrayList<CO2>> reports = new HashMap<>();
                    task.getResult().getChildren().forEach(child->{
                        CO2 co2 = child.getValue(CO2.class);
                        ArrayList<CO2> prevState = reports.get(co2.getId());
                        if(prevState == null) prevState = new ArrayList<>();
                        prevState.add(co2);
                        reports.put(co2.getId(), prevState);
                    });
                    onFinish.accept(reports);
                } else {
                    onFinish.accept(null);
                }
            }
        });
    }

    private void getUserInfo(Consumer<ArrayList<User>> onFinish) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<User> users = new ArrayList<>();
                    task.getResult().getChildren().forEach(child->{
                        users.add(child.getValue(User.class));
                    });
                    onFinish.accept(users);
                } else {
                    onFinish.accept(null);
                }
            }
        });
    }

}