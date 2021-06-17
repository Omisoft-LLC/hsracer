package com.omisoft.hsracer.features.settings;

import static android.widget.Toast.LENGTH_SHORT;
import static com.omisoft.hsracer.constants.Constants.OBD_DEVICE_KEY;
import static com.omisoft.hsracer.constants.Constants.OBD_ENABLE_KEY;
import static com.omisoft.hsracer.constants.Constants.RESOLUTION_KEY;
import static com.omisoft.hsracer.constants.Constants.TEMPERATURE_SCALE_KEY;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.services.RaceService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.greenrobot.eventbus.EventBus;


/**
 * Created by Omisoft LLC. on 3/23/17.
 */

public class SettingsFragment extends PreferenceFragment {

  private CheckBoxPreference enableMock;
  private CheckBoxPreference enableOBD;
  private ListPreference unitSystem;
  private ListPreference pairedDevices;
  private ListPreference temperatureScale;
  private ListPreference videoQuality;
  private BluetoothAdapter bluetoothAdapter;
  private boolean isPairedDevices;
  private HashSet<Integer> getSupportedSizes;
  

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    isPairedDevices = false;
    setupViews();
    registerReceiver();
//    setMockLocationAction();
    setEnableOBDAction();
    changePairedDevice();
    setUnitSystemTitle();
    setDynamicUnitSystemTitle();
    setDynamicPairedDeviceSummary();
    setDynamicVideoResolution();
    setVideoResolutionPreferences();
    setDynamicResolutionSummary();
    setTemperatureScaleAction();
    setDynamicTemperatureSummary();
  }

  @Override
  public void onResume() {
    if (videoQuality.isEnabled()) {
      setDynamicVideoResolution();
      populateResolutionList(getSupportedSizes);
    }
    super.onResume();
  }

  @Override
  public void onDestroy() {
    unregisterReceiver();
    super.onDestroy();
  }

  /**
   * Setup all the view sin the settings
   */
  private void setupViews() {
    enableMock = (CheckBoxPreference) findPreference("mock-location");
    if (BuildConfig.DEBUG && enableMock != null) {
      enableMock.setEnabled(true);
    }

    enableOBD = (CheckBoxPreference) findPreference(OBD_ENABLE_KEY);
    pairedDevices = (ListPreference) findPreference(OBD_DEVICE_KEY);
    videoQuality = (ListPreference) findPreference(RESOLUTION_KEY);
    temperatureScale = (ListPreference) findPreference(TEMPERATURE_SCALE_KEY);
    videoQuality.setEnabled(false);
    unitSystem = (ListPreference) findPreference(getString(R.string.pref_metric_key));
    if (!enableOBD.isChecked()) {
      pairedDevices.setEnabled(false);
      temperatureScale.setEnabled(false);
    } else {
      listPairedDevices();
    }
  }

  private void setMockLocationAction() {
    enableMock.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isChecked = (Boolean) newValue;
        setMockLocation(isChecked);
        return true;
      }
    });
  }

  private void setMockLocation(boolean isChecked) {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.putBoolean(Constants.MOCK_LOCATION, isChecked);
    editor.apply();
  }

  private void setEnableOBDAction() {
    enableOBD.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isChecked = (Boolean) newValue;
        listPairedDevices();
        checkOBDAvailability();
        setOBDEnabled(isChecked);
        pairedDevices.setEnabled(isChecked);
        temperatureScale.setEnabled(isChecked);
        return true;
      }
    });
  }

  private void setOBDEnabled(boolean isChecked) {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.putBoolean(OBD_ENABLE_KEY, isChecked);
    editor.apply();
  }

  private void enableBluetooth() {
    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
    startActivity(settingsIntent);
  }

  private void listPairedDevices() {
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      isPairedDevices = true;
      BluetoothDevice[] devices = pairedDevices
          .toArray(new BluetoothDevice[pairedDevices.size()]);
      String[] deviceName = new String[pairedDevices.size()];
      String[] deviceAddress = new String[pairedDevices.size()];
      for (int i = 0; i < pairedDevices.size(); i++) {
        deviceName[i] = devices[i].getName();
        deviceAddress[i] = devices[i].getAddress();
      }
      populateDeviceList(deviceName, deviceAddress);
    } else {
      isPairedDevices = false;
    }
  }

  private void populateDeviceList(String[] deviceNames, String[] deviceAddresses) {
    pairedDevices.setEntries(deviceNames);
    pairedDevices.setEntryValues(deviceAddresses);
  }

  private void changePairedDevice() {
    pairedDevices.setOnPreferenceClickListener(new OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        listPairedDevices();
        if (!bluetoothAdapter.isEnabled()) {
          ((ListPreference) preference).getDialog().dismiss();
          enableBluetooth();
          Toast.makeText(getActivity().getApplicationContext(),
              R.string.bluetooth_disabled_disclaimer, LENGTH_SHORT).show();
        } else {
          if (!isPairedDevices) {
            ((ListPreference) preference).getDialog().dismiss();
            deleteOBDDevice();
            enableBluetooth();
            Toast.makeText(getActivity().getApplicationContext(), R.string.no_paired_devices,
                Toast.LENGTH_LONG).show();
          }
        }
        setDynamicPairedDeviceSummary();
        return true;
      }
    });
    pairedDevices.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        setOBDDevice((String) newValue);
        setDynamicPairedDeviceSummary();
        return true;
      }
    });
  }

  private void deleteOBDDevice() {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.remove(Constants.OBD_DEVICE_KEY);
    editor.apply();
  }

  private void setOBDDevice(String address) {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.putString(OBD_DEVICE_KEY, address);
    editor.apply();
  }

  private void setResolution(String resolution) {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.putString(RESOLUTION_KEY, resolution);
    editor.apply();
  }

  private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
            BluetoothAdapter.ERROR);
        switch (state) {
          case BluetoothAdapter.STATE_OFF:
            listPairedDevices();
            break;
          case BluetoothAdapter.STATE_TURNING_OFF:
            break;
          case BluetoothAdapter.STATE_ON:
            listPairedDevices();
            checkOBDAvailability();
            break;
          case BluetoothAdapter.STATE_TURNING_ON:
            break;
        }
      }
    }
  };

  private void registerReceiver() {
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    getActivity().registerReceiver(receiver, filter);
  }

  private void unregisterReceiver() {
    getActivity().unregisterReceiver(receiver);
  }

  private void startOBD() {
    Message m = Message.obtain();
    m.what = RaceService.START_OBD_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(m));
  }

  private void checkOBDAvailability() {
//    boolean isDevicePaired = false;
//    if (!getPreferenceManager().getSharedPreferences().getString(OBD_DEVICE_KEY, "")
//        .equalsIgnoreCase("")) {
//      isDevicePaired = true;
//    }
//    if (bluetoothAdapter.isEnabled() && getPreferenceManager().getSharedPreferences()
//        .getBoolean(OBD_ENABLE_KEY, false) && isDevicePaired) {
//      startOBD();
//    }
  }


  private void setUnitSystemTitle() {
    unitSystem.setTitle(getPreferenceManager().getSharedPreferences().getString(getString(R.string.pref_metric_key), getString(R.string.metric_system)));
  }

  private void setDynamicUnitSystemTitle() {
    unitSystem.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setTitle((String) newValue);
        return true;
      }
    });
  }

  private void setTemperatureScaleAction() {
    temperatureScale.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        setTemperatureScale((String) newValue);
        setDynamicTemperatureSummary();
        return true;
      }
    });
  }

  private void setTemperatureScale(String temperature) {
    SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
    editor.putString(TEMPERATURE_SCALE_KEY, temperature);
    editor.apply();
  }

  private void setDynamicPairedDeviceSummary() {
    if (!getPreferenceManager().getSharedPreferences().getString(OBD_DEVICE_KEY, "")
        .equalsIgnoreCase("")) {
      pairedDevices.setSummary(getString(R.string.obd_device_paired));
    } else {
      pairedDevices.setSummary(getString(R.string.no_obd_device_paired));
    }
  }

  private void setDynamicResolutionSummary() {
    if (!getPreferenceManager().getSharedPreferences().getString(RESOLUTION_KEY, "")
        .equalsIgnoreCase("")) {
      videoQuality.setSummary(getPreferenceManager().getSharedPreferences().getString(RESOLUTION_KEY, ""));
    }
  }

  private void setDynamicVideoResolution() {
    try {
      Log.wtf("CAMERA", "setDynamicVideoResolution: ");
      Camera camera = Camera.open();
      List<Camera.Size> sizes = camera.getParameters().getSupportedVideoSizes();
      camera.release();
      videoQuality.setEnabled(true);
      if (getSupportedSizes == null) {
        getSupportedSizes = new HashSet<>();
        for (Camera.Size elem : sizes) {
          if (elem.height == 2160) {
            getSupportedSizes.add(2160);
          } else if (elem.height == 1080) {
            getSupportedSizes.add(1080);
          } else if (elem.height == 720) {
            getSupportedSizes.add(720);
          } else if (elem.height == 480) {
            getSupportedSizes.add(480);
          } else if (elem.height == 144) {
            getSupportedSizes.add(144);
          }
        }
      }
    } catch (Exception e) {
      videoQuality.setEnabled(false);
    }
  }

  private void populateResolutionList(HashSet<Integer> resolutions) {
    Integer[] notArrangedEntries = resolutions.toArray(new Integer[resolutions.size()]);
    String[] arrangedEntries = new String[notArrangedEntries.length];
    sortAndArrangeVideoQualityResolutions(notArrangedEntries, arrangedEntries);
    videoQuality.setEntries(arrangedEntries);
    videoQuality.setEntryValues(arrangedEntries);
  }

  private void sortAndArrangeVideoQualityResolutions(Integer[] from, String[] to) {
    Arrays.sort(from, Collections.reverseOrder());
    for (int i = 0; i < from.length; i++) {
      if (from[i] == 2160) {
        to[i] = "4k";
      } else if (from[i] == 1080) {
        to[i] = "1080p";
      } else if (from[i] == 720) {
        to[i] = "720p";
      } else if (from[i] == 480) {
        to[i] = "480p";
      } else if (from[i] == 144) {
        to[i] = "144p";
      }
    }
  }

  private void setVideoResolutionPreferences() {
    videoQuality.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        setResolution((String) newValue);
        setDynamicResolutionSummary();
        return true;
      }
    });
  }

  private void setDynamicTemperatureSummary() {
      temperatureScale.setSummary(getPreferenceManager().getSharedPreferences().getString(TEMPERATURE_SCALE_KEY, getString(R.string.celsius)));
  }
}