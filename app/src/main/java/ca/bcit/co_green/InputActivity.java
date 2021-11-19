package ca.bcit.co_green;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private static final String EMISSION_URL = "https://beta2.api.climatiq.io/estimate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }

    public void calculateEmission(View view) {
        // Get values
        TextView distanceDrivenTV = findViewById(R.id.edtDriveDistance);
        String distanceDrivenStr = distanceDrivenTV.getText().toString();

        // Convert to body value
        double distanceDriven = Double.parseDouble(distanceDrivenStr);

        // Build API request body
        JSONObject postData = new JSONObject();
        try {
            postData.put("emission_factor", "fuel_type_avtur-fuel_use_aviation");
            JSONObject postDataPAram = new JSONObject();
            postDataPAram.put("volume", distanceDriven);
            postDataPAram.put("volume_unit", "l");
            postData.put("parameters",postDataPAram);

            callAPI(postData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callAPI(JSONObject postJsonObj){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = EMISSION_URL;
            JSONObject jsonBody = postJsonObj;

            final String token = "bearer " +getResources().getString(R.string.TOKEN);
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


    public void handleSuccessResult(String response){
        // Parse response
        Gson gson = new Gson();
        JsonObject convertedObject = gson.fromJson(response, JsonObject.class);
        String result = convertedObject.get("co2e").getAsString();

        // Display CO2 result as text
        TextView resultText = findViewById(R.id.txtResult);
        resultText.setText(result);
    }

}