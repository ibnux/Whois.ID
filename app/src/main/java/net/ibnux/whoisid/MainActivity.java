package net.ibnux.whoisid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    EditText txtDomain;
    TextView result;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtDomain = findViewById(R.id.txtDomain);
        this.result = findViewById(R.id.result);
        this.progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        txtDomain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(txtDomain.getText().toString().isEmpty()){
                    txtDomain.setError("Mohon diisi domain");
                    return false;
                }
                if(!txtDomain.getText().toString().endsWith(".id")){
                    txtDomain.setError("domain tidak valid");
                    return false;
                }
                getWhois();
                return true;
            }
        });
    }

    public void setLoading(boolean yes){
        if(yes){
            progressBar.setVisibility(View.VISIBLE);
            txtDomain.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            txtDomain.setVisibility(View.VISIBLE);
        }
    }


    public void getWhois(){
        setLoading(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pandi.id/whois/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setLoading(false);
                        String[] temp = response.split("<pre>");
                        if(temp.length>1){
                            temp = temp[1].split("</pre>");
                            result.setText(temp[0]);
                        }else {
                            result.setText("Error: \n\n"+response);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoading(false);
                result.setText("Error: \n\n"+error.getMessage());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return ("domain="+txtDomain.getText().toString()).getBytes("utf-8");
                }catch (Exception e){
                    return ("domain="+txtDomain.getText().toString()).getBytes();
                }
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
