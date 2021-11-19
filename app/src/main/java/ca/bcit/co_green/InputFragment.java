package ca.bcit.co_green;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.bcit.co_green.ranking.RankingRecyclerAdapter;

public class InputFragment extends Fragment {
    EditText edtDriveDistance;
    EditText edtElecUsed;
    Button button;
    DatabaseReference databaseInput;
    FirebaseUser user;
    private static final String EMISSION_URL = "https://beta2.api.climatiq.io/estimate";

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
                calculateEmission();
                addInput();
            }
        });

        return view;
    }

    private void addInput() {
        String driveDistance = edtDriveDistance.getText().toString().trim();
        String electUsed = edtElecUsed.getText().toString().trim();

        if (TextUtils.isEmpty(driveDistance)) {
            Toast.makeText(getActivity(), "Must enter some value.", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(electUsed)) {
            Toast.makeText(getActivity(), "Must enter some value.", Toast.LENGTH_LONG).show();
        }

        String id = databaseInput.push().getKey();
        String userId = user.getUid();
        CO2 co2 = new CO2(userId, driveDistance, electUsed);

        Task setValueTask = databaseInput.child(id).setValue(co2);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getActivity(), "co2 input added.", Toast.LENGTH_LONG).show();
                edtDriveDistance.setText("");
                edtElecUsed.setText("");
            }
        });
    }

    public void calculateEmission() {
        // Get values
        TextView distanceDrivenTV = getView().findViewById(R.id.edtDriveDistance);
        String distanceDrivenStr = distanceDrivenTV.getText().toString();
        if (distanceDrivenStr.isEmpty()) distanceDrivenStr = "0";

        // Convert to body value
        double distanceDriven = Double.parseDouble(distanceDrivenStr);

        // Build API request body
        JSONObject postData = new JSONObject();
        try {
            postData.put("emission_factor", "fuel_type_avtur-fuel_use_aviation");
            JSONObject postDataPAram = new JSONObject();
            postDataPAram.put("volume", distanceDriven);
            postDataPAram.put("volume_unit", "l");
            postData.put("parameters", postDataPAram);

            callAPI(postData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callAPI(JSONObject postJsonObj) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = EMISSION_URL;
            JSONObject jsonBody = postJsonObj;

            final String token = "bearer " + getResources().getString(R.string.TOKEN);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println(response);
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
        String result = convertedObject.get("co2e").getAsString();

        // Display CO2 result as text
        TextView resultText = getActivity().findViewById(R.id.txtResult);
        resultText.setText(result);
    }

}