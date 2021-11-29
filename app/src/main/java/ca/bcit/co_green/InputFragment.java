package ca.bcit.co_green;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InputFragment extends Fragment {
    EditText edtDriveDistance;
    EditText edtElecUsed;
    Button button;
    DatabaseReference databaseInput;
    FirebaseUser user;
    private static final String DRIVING_EMISSION_ENDPOINT = "https://beta2.api.climatiq.io/estimate";
    private static final String ELEC_EMISSION_ENDPOINT = "https://beta2.api.climatiq.io/estimate";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        databaseInput = FirebaseDatabase.getInstance().getReference("ranking");
        user = FirebaseAuth.getInstance().getCurrentUser();
        edtDriveDistance = view.findViewById(R.id.edtDriveDistance);
        edtElecUsed = view.findViewById(R.id.edtElecUsed);
        button = view.findViewById(R.id.button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        Button button = (Button) view.findViewById(R.id.btnSaveInput);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildRequestDriving();
                buildRequestElec();
            }
        });

        return view;
    }

    private void insertToDB(CO2 co2Report){

        String id = databaseInput.push().getKey();
        Task setValueTask = databaseInput.child(id).setValue(co2Report);
        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getActivity(), "co2 input added.", Toast.LENGTH_LONG).show();
                edtDriveDistance.setText("");
                edtElecUsed.setText("");
            }
        });
    }

    private void writeToDB(Map<String, String> co2ResultPair) {
        if(co2ResultPair.size()==0) return;

        // Find Users report for today
        databaseInput
                .orderByChild("id")
                .equalTo(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<CO2> userReports = new ArrayList<>();
                    final String[] reportId = {""};
                    task.getResult().getChildren().forEach(child->{

                        CO2 tempReport = child.getValue(CO2.class);
                        if(DateUtils.isToday(tempReport.getTimestamp().getTime())) {
                            reportId[0] = child.getKey();
                            userReports.add(tempReport);
                        }
                    });

                    CO2 newCo2 = new CO2();

                    for (Map.Entry<String, String> entry : co2ResultPair.entrySet()) {
                        if (entry.getKey().equals("driveDistance")) {
                            newCo2.setDriveDistance(entry.getValue());
                            newCo2.setElecUsed("0");
                        }else if(entry.getKey().equals("elecUsed")){
                            newCo2.setDriveDistance("0");
                            newCo2.setElecUsed(entry.getValue());
                        }
                    }
                    newCo2.setTimestamp(new Date());
                    newCo2.setId(user.getUid());
                    insertToDB(newCo2);
                }
            }
        });
    }

    public void buildRequestElec(){
        String electUsed = edtElecUsed.getText().toString().trim();
        if (TextUtils.isEmpty(electUsed)) {
            Toast.makeText(getActivity(), "Must enter some value.", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert to body value
        double elec = Double.parseDouble(electUsed);

        // Build API request body
        JSONObject postData = new JSONObject();
        try {
            postData.put("emission_factor", "electricity-energy_source_coal_fired_plant");
            JSONObject postDataPAram = new JSONObject();
            postDataPAram.put("energy", elec);
            postDataPAram.put("energy_unit", "kWh");
            postData.put("parameters", postDataPAram);

            callAPI(postData, ELEC_EMISSION_ENDPOINT);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildRequestDriving() {
        // Get values
        String driveDistance = edtDriveDistance.getText().toString().trim();
        if (TextUtils.isEmpty(driveDistance)) {
            Toast.makeText(getActivity(), "Must enter some value.", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert to body value
        double distanceDriven = Double.parseDouble(driveDistance);

        // Build API request body
        JSONObject postData = new JSONObject();
        try {
            postData.put("emission_factor", "fuel_type_avtur-fuel_use_aviation");
            JSONObject postDataPAram = new JSONObject();
            postDataPAram.put("volume", distanceDriven);
            postDataPAram.put("volume_unit", "l");
            postData.put("parameters", postDataPAram);

            callAPI(postData, DRIVING_EMISSION_ENDPOINT);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callAPI(JSONObject postJsonObj, String apiEndpoint) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = apiEndpoint;
            JSONObject jsonBody = postJsonObj;

            final String token = "bearer " + getResources().getString(R.string.TOKEN);
            final String requestBody = jsonBody.toString();
            String endpointResponse = "";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    handleSuccessResult(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String parsed;
                    try {
                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    } catch (UnsupportedEncodingException e) {
                        parsed = new String(response.data);
                    }
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleSuccessResult(String response) {
        // Parse response
        Gson gson = new Gson();
        JsonObject convertedObject = gson.fromJson(response, JsonObject.class);

        String co2Result = "";
        String co2Type = "";
        if(convertedObject != null && convertedObject.get("co2e") != null){
            co2Result = convertedObject.get("co2e").getAsString();
            co2Type = convertedObject.get("id").getAsString();
        }

        Map<String, String> co2ResultPair = new HashMap<>();

        if (co2Type.contains("fuel")) {
            co2ResultPair.put("driveDistance", co2Result);
        }else if(co2Type.contains("elec")){
            co2ResultPair.put("elecUsed", co2Result);
        }

        writeToDB(co2ResultPair);
    }

}