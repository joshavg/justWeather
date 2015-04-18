package de.joshavg.justweather;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchField = (EditText) findViewById(R.id.searchField);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                fetchWeatherData(v.getText().toString());
                return true;
            }
        });
    }

    private void fetchWeatherData(String qry) {
        Toast.makeText(getApplicationContext(), "searching for " + qry, Toast.LENGTH_LONG).show();
        new RequestTask() {
            @Override
            public void onPostExecute(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject main = obj.getJSONObject("main");
                    double tempMax = main.getDouble("temp_max") - 272.150;
                    double tempMin = main.getDouble("temp_min") - 272.150;

                    String desc = "";
                    JSONArray weather = obj.getJSONArray("weather");
                    if(weather.length() > 0) {
                        JSONObject weather1 = weather.getJSONObject(0);
                        desc = weather1.getString("description");
                    }

                    ((TextView)findViewById(R.id.tempMin)).setText(tempMin + "");
                    ((TextView)findViewById(R.id.tempMax)).setText(tempMax + "");
                    ((TextView)findViewById(R.id.desc)).setText(desc);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "error while parsing json " + e, Toast.LENGTH_LONG).show();
                }
            }
        }.execute("http://api.openweathermap.org/data/2.5/weather?q=" + qry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
