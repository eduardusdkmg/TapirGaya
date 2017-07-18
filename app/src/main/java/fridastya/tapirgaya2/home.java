package fridastya.tapirgaya2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.provider.Settings.System.DATE_FORMAT;
import static fridastya.tapirgaya2.AppVar.GETDATA_URL;
import static fridastya.tapirgaya2.AppVar.LOGIN_DATA;

public class home extends AppCompatActivity {

    ImageButton btnMan, btnAuto;
    LinearLayout layoutSpinner;
    TextView currentDateTime, d;
    Button e;

    private Context context;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        setContentView(R.layout.activity_home);

        layoutSpinner = (LinearLayout) findViewById(R.id.layout_spinner);
        btnMan = (ImageButton) findViewById(R.id.man);
        btnAuto = (ImageButton) findViewById(R.id.auto);

        btnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSpinner.setVisibility(View.VISIBLE);
                btnAuto.setImageResource(R.drawable.auto_set);
                btnMan.setImageResource(R.drawable.manual_active);
            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSpinner.setVisibility(View.GONE);
                btnMan.setImageResource(R.drawable.manual_set);
                btnAuto.setImageResource(R.drawable.auto_active);
            }
        });

        currentDateTime = (TextView) findViewById(R.id.currentDateTime);
        d = (TextView) findViewById(R.id.cond);

        SharedPreferences prefs = getSharedPreferences("UserLogin", MODE_PRIVATE);
        String Username = prefs.getString("username", null);
        if (Username != null)
            getDataUser(Username);
        else
            d.setText(Username);
        e = (Button) findViewById(R.id.exitbtn);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitClick();
            }
        });

//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String currentDateTimeString = printStandardDate(Calendar.getInstance().getTime());

        // menampilkan data waktu dan hari
        currentDateTime.setText(currentDateTimeString);

        Spinner spinner = (Spinner) findViewById(R.id.mode_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void getDataUser(final String username) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Get Data Process...");
        showDialog();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppVar.GETDATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSONe","json response : "+response);
                            JSONObject json = new JSONObject(response);
                            //If we are getting success from server
                            if (response.contains(AppVar.LOGIN_SUCCESS) && response.contains(LOGIN_DATA)) {
                                hideDialog();
                                JSONObject data = json.getJSONObject(LOGIN_DATA);
                                d.setText(data.getString("kondisi_atap"));
                                if(Integer.parseInt(d.getText().toString()) == 1)
                                    d.setText("Kondisi atap : terbuka");
                                else
                                    d.setText("Kondisi atap : tertutup");
                            } else {
                                hideDialog();
                                //Displaying an error message on toast
                                Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        hideDialog();
                        error.printStackTrace();
                        Toast.makeText(context, "The server unreachable", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(AppVar.KEY_USERGET, username);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String printStandardDate(Date date) {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm ").format(date);
    }

    public void exitClick(){
        System.exit(0);
    }
}
