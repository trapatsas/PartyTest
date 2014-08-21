package com.example.ptrapatsas.partytest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class HomePage extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.ptrapatsas.partytest.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
            return rootView;
        }
    }

    /** Called when the user clicks the Info button */
    public void UpdateInfo(View view) {
        // Do something in response to button
        Button btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setEnabled(false);
        TextView viewInfo = (TextView) findViewById(R.id.txtInfo);
        viewInfo.append("\n 1. XXX---XXX");
    }

    public void LeavePage(View view) {

        try {
            Intent intent = new Intent(this, TetherPage.class);
            TextView editText = (TextView) findViewById(R.id.txtInfo);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
        catch (Exception e){
            Log.d("Error opening page: ", e.toString());
        }
    }

    public void ShowIp(View view){
        TextView viewInfo = (TextView) findViewById(R.id.txtInfo);
        viewInfo.append("\n\n IP: "+ NetUtils.getIPAddress(true));


        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID",wifiInfo.getSSID());

        viewInfo.append("\n SSID: "+ wifiInfo.getSSID());
    }

    public void WiFiToggle(View view) {
        boolean isChecked = ((ToggleButton) view).isChecked();
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        Boolean result = false;
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "Tab3OpenWifi";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        String setWifiApConfigurationMethodName = "setWifiApConfiguration";
        Method setWifiApConfigurationMethod = null;
        try {
            setWifiApConfigurationMethod = wifiManager.getClass().getMethod(setWifiApConfigurationMethodName, WifiConfiguration.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            assert setWifiApConfigurationMethod != null;
            result = (Boolean) setWifiApConfigurationMethod.invoke(wifiManager, config);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (result) {
            String setWifiApEnableMethodName = "setWifiApEnabled";
            Method setWifiApEnableMethod = null;
            try {
                setWifiApEnableMethod = wifiManager.getClass().getMethod(setWifiApEnableMethodName, WifiConfiguration.class, boolean.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            String message;
            if (isChecked) {
                try {
                    assert setWifiApEnableMethod != null;
                    result = (Boolean) setWifiApEnableMethod.invoke(wifiManager, null, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (result) {
                    message = "Enabling tethering successfully";
                } else {
                    message = "Enabling tethering failed";
                }
            } else {
                try {
                    assert setWifiApEnableMethod != null;
                    result = (Boolean) setWifiApEnableMethod.invoke(wifiManager, null, false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (result) {
                    message = "Disabling tethering successfully";
                } else {
                    message = "Disabling tethering failed";
                }
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update Wifi Tethering config.", Toast.LENGTH_SHORT).show();
        }
    }
}
