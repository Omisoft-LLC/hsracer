package com.omisoft.hsracer.common.obd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;
import com.omisoft.hsracer.features.race.events.OBDConnectionErrorEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * Wrapper class for obd connection
 */
public class OBDConnManager {

  public static final String SERIAL_PORT_UUID = "00001101-0000-1000-8000-00805F9B34FB";
  private BluetoothSocketWrapper bluetoothSocket;
  private BluetoothDevice device;
  private boolean secure;
  private BluetoothAdapter adapter;
  private List<UUID> uuidCandidates;
  private volatile int candidate;

  /**
   * @param device the device
   * @param secure if connection should be done via a secure socket
   * @param adapter the Android BT adapter
   * @param uuidCandidates a list of UUIDs. if null or empty, the Serial PP id is used
   */
  public OBDConnManager(BluetoothDevice device, boolean secure, BluetoothAdapter adapter,
      List<UUID> uuidCandidates) {
    this.device = device;
    this.secure = secure;
    this.adapter = adapter;
    this.uuidCandidates = uuidCandidates;

    if (this.uuidCandidates == null || this.uuidCandidates.isEmpty()) {
      this.uuidCandidates = new ArrayList<>();
      this.uuidCandidates.add(UUID.fromString(SERIAL_PORT_UUID));
    }
  }

  public BluetoothSocketWrapper connect() throws IOException {
    boolean success = false;
    while (selectSocket()) {
      adapter.cancelDiscovery();
      Message OBDError = Message.obtain();
      try {
        bluetoothSocket.connect();
        success = true;
        break;
      } catch (IOException e) {
        try {
          bluetoothSocket = new FallbackBluetoothSocket(bluetoothSocket.getUnderlyingSocket());
          Thread.sleep(500);
          bluetoothSocket.connect();
          success = true;
          break;
        } catch (FallbackException e1) {
          Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", e);
          /* Eventbus posts to RaceService class, OBDErrorEvent method */
          EventBus.getDefault().post(new OBDConnectionErrorEvent(OBDError));
        } catch (InterruptedException e1) {
          Log.w("BT", e1.getMessage(), e1);
          /* Eventbus posts to RaceService class, OBDErrorEvent method */
          EventBus.getDefault().post(new OBDConnectionErrorEvent(OBDError));
        } catch (IOException e1) {
          Log.w("BT", "Fallback failed. Cancelling.", e1);
          e1.printStackTrace();
          /* Eventbus posts to RaceService class, OBDErrorEvent method */
          EventBus.getDefault().post(new OBDConnectionErrorEvent(OBDError));
        }
      }
    }

    if (!success) {
      throw new IOException("Could not connect to device: " + device.getAddress());
    }

    return bluetoothSocket;
  }

  private boolean selectSocket() throws IOException {
    if (candidate >= uuidCandidates.size()) {
      return false;
    }

    BluetoothSocket tmp;
    UUID uuid = uuidCandidates.get(candidate++);
    if (secure) {
      tmp = device.createRfcommSocketToServiceRecord(uuid);
    } else {
      tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
    }
    bluetoothSocket = new NativeBluetoothSocket(tmp);

    return true;
  }

  public interface BluetoothSocketWrapper {

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    String getRemoteDeviceName();

    void connect() throws IOException;

    String getRemoteDeviceAddress();

    void close() throws IOException;

    BluetoothSocket getUnderlyingSocket();
  }


  public static class NativeBluetoothSocket implements BluetoothSocketWrapper {

    private BluetoothSocket socket;

    public NativeBluetoothSocket(BluetoothSocket tmp) {
      this.socket = tmp;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      return socket.getOutputStream();
    }

    @Override
    public String getRemoteDeviceName() {
      return socket.getRemoteDevice().getName();
    }

    @Override
    public void connect() throws IOException {
      socket.connect();
    }

    @Override
    public String getRemoteDeviceAddress() {
      return socket.getRemoteDevice().getAddress();
    }

    @Override
    public void close() throws IOException {
      socket.close();
    }

    @Override
    public BluetoothSocket getUnderlyingSocket() {
      return socket;
    }
  }

  public class FallbackBluetoothSocket extends NativeBluetoothSocket {

    private BluetoothSocket fallbackSocket;

    public FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
      super(tmp);
      try {
        Class<?> clazz = tmp.getRemoteDevice().getClass();
        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
        Object[] params = new Object[]{Integer.valueOf(1)};
        fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
      } catch (Exception e) {
        throw new FallbackException(e);
      }
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return fallbackSocket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      return fallbackSocket.getOutputStream();
    }


    @Override
    public void connect() throws IOException {
      fallbackSocket.connect();
    }


    @Override
    public void close() throws IOException {
      fallbackSocket.close();
    }

  }

  public static class FallbackException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FallbackException(Exception e) {
      super(e);
    }
  }
}