package fridastya.tapirgaya2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.text.DecimalFormat;
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
    Button e, btnChangeStatusAtap;
    private Boolean modeAtap;
    private Boolean tempProcessInitMode = false;
    private String resultSpinnerAtap;
    String Username;

    private Context context;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        modeAtap = true;

        currentDateTime = (TextView) findViewById(R.id.currentDateTime);
        d = (TextView) findViewById(R.id.cond);

        layoutSpinner = (LinearLayout) findViewById(R.id.layout_spinner);
        btnMan = (ImageButton) findViewById(R.id.man);
        btnAuto = (ImageButton) findViewById(R.id.auto);

        SharedPreferences prefs = getSharedPreferences("UserLogin", MODE_PRIVATE);
        Username = prefs.getString("username", null);

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


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        setModeAtap(modeAtap);
                    }
                },
                5000);
//        if (tempProcessInitMode)

        final Spinner spinner = (Spinner) findViewById(R.id.mode_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resultSpinnerAtap = spinner.getSelectedItem().toString();
                if (resultSpinnerAtap.equals("Buka Atap")){
                    resultSpinnerAtap = "1";
                }else {
                    resultSpinnerAtap = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // On nothing selected
            }
        });

        btnChangeStatusAtap = (Button) findViewById(R.id.btn_change_status);
        btnChangeStatusAtap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataStatusAtap(Username, resultSpinnerAtap);
//                Toast.makeText(getApplicationContext(), resultSpinnerAtap, Toast.LENGTH_LONG).show();
            }
        });
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
                                // cek kondisi atap
                                d.setText(data.getString("kondisi_atap"));
                                if(Integer.parseInt(d.getText().toString()) == 1) {
                                    d.setText("Kondisi atap : terbuka");
//                                    modeAtap = false;
                                }else {
                                    d.setText("Kondisi atap : tertutup");
                                }
                                // cek mode atap
                                String tempModeAtap = data.getString("mode");
                                if(Integer.parseInt(tempModeAtap) == 0)
                                    modeAtap = false;
                                else
                                    modeAtap = true;

//                                tempProcessInitMode = true;
                                hideDialog();
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
                        Toast.makeText(getApplicationContext(), "The server unreachable", Toast.LENGTH_LONG).show();

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

    private void setModeAtap(final Boolean status){
        if (status){
            btnAuto.setImageResource(R.drawable.auto_active);
            layoutSpinner.setVisibility(View.GONE);
        }else{
            btnMan.setImageResource(R.drawable.manual_active);
            layoutSpinner.setVisibility(View.VISIBLE);
        }

//        Toast.makeText(getApplicationContext(), modeAtap.toString(), Toast.LENGTH_LONG).show();

        btnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSpinner.setVisibility(View.VISIBLE);
                btnAuto.setImageResource(R.drawable.auto_set);
                btnMan.setImageResource(R.drawable.manual_active);
                modeAtap = false;
                updateDataModeAtap(Username, modeAtap) ;
            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSpinner.setVisibility(View.GONE);
                btnMan.setImageResource(R.drawable.manual_set);
                btnAuto.setImageResource(R.drawable.auto_active);
                modeAtap = true;
                updateDataModeAtap(Username, modeAtap);
            }
        });
    }

    private void updateDataModeAtap(String usernm, Boolean status){
        final String username = usernm;
        String modeatap = null;

        if (status)
            modeatap = "1";
        else
            modeatap = "0";

        pDialog.setMessage("Update Data ...");
        showDialog();
        //Creating a string request
        final String finalModeatap = modeatap;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppVar.UPDATEDATAMODE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON","json response : "+response);
                            JSONObject json = new JSONObject(response);
                            //If we are getting success from server
                            if (response.contains(AppVar.LOGIN_SUCCESS) && response.contains(LOGIN_DATA)) {
                                hideDialog();
//                                gotoHomeActivity();


                            } else {
                                hideDialog();
                                //Displaying an error message on toast
                                Toast.makeText(context, "Invalid process", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "The server unreachable", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(AppVar.KEY_USERNAME, username);
                params.put(AppVar.KEY_MODEATAP, finalModeatap);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void updateDataStatusAtap(String usernm,  String status){
        final String username = usernm;
        String statusatap = status;

       pDialog.setMessage("Update Data ...");
        showDialog();
        //Creating a string request
        final String finalStatusatap = statusatap;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppVar.UPDATEDATASTATUS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON","json response : "+response);
                            JSONObject json = new JSONObject(response);
                            //If we are getting success from server
                            if (response.contains(AppVar.LOGIN_SUCCESS) && response.contains(LOGIN_DATA)) {
                                hideDialog();

                                String data = json.getString(LOGIN_DATA);
                                // cek kondisi atap
                                d.setText(data);
                                if(Integer.parseInt(d.getText().toString()) == 1) {
                                    d.setText("Kondisi atap : terbuka");
//                                    Toast.makeText(getApplicationContext(), "Kondisi atap : terbuka", Toast.LENGTH_LONG).show();
                                }else {
                                    d.setText("Kondisi atap : tertutup");
//                                    Toast.makeText(getApplicationContext(), "Kondisi atap : tertutup", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                hideDialog();
                                //Displaying an error message on toast
                                Toast.makeText(context, "Invalid process", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "The server unreachable", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(AppVar.KEY_USERNAME, username);
                params.put(AppVar.KEY_KONDISIATAP, finalStatusatap);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);
        finish();
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
