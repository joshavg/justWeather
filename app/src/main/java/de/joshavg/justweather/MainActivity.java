package de.joshavg.justweather;

import android.content.res.Resources;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchField = (EditText) findViewById(R.id.searchField);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    fetchWeatherData(v.getText().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void fetchWeatherData(String qry) throws UnsupportedEncodingException {
        Toast.makeText(getApplicationContext(), "searching for " + qry, Toast.LENGTH_SHORT).show();
        String encodedQry = URLEncoder.encode(qry, "utf-8");

        new RequestTask() {
            @Override
            public void onPostExecute(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    String cityName = obj.getJSONObject("city").getString("name");
                    ((TextView)findViewById(R.id.cityName)).setText(cityName);

                    JSONArray lst = obj.getJSONArray("list");
                    int cnt = obj.getInt("cnt");
                    for(int i = 0; i < cnt; ++i) {
                        JSONObject row = lst.getJSONObject(i);
                        JSONObject temp = row.getJSONObject("temp");

                        double tempMax = Math.round((temp.getDouble("max") - 272.150) * 10) / 10.0;
                        double tempMin = Math.round((temp.getDouble("min") - 272.150) * 10) / 10.0;

                        String desc = "";
                        JSONArray weather = row.getJSONArray("weather");
                        if(weather.length() > 0) {
                            JSONObject weather1 = weather.getJSONObject(0);
                            desc = weather1.getString("description");
                        }

                        ((TextView)findViewById(getViewId("tempMin" + i))).setText(tempMin + "");
                        ((TextView)findViewById(getViewId("tempMax" + i))).setText(tempMax + "");
                        ((TextView)findViewById(getViewId("desc" + i))).setText(desc);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "error while parsing json " + e, Toast.LENGTH_LONG).show();
                } catch(NullPointerException e) {
                    // noop
                }
            }
        }.execute("http://api.openweathermap.org/data/2.5/forecast/daily?cnt=2&q=" + encodedQry);
    }

    private int getViewId(String name) {
        Resources res = getResources();
        return res.getIdentifier(name, "id", getApplicationContext().getPackageName());
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
