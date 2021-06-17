package com.omisoft.hsracer.features.race.threads;

import static com.omisoft.hsracer.constants.ObdConstants.GET_SPEED_RPM_COOLANT_TEMP;
import static com.omisoft.hsracer.constants.ObdConstants.QUIT_OBD;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.github.pires.obd.commands.ObdMultiCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.obd.OBDConnManager;
import com.omisoft.hsracer.common.obd.OBDConnManager.BluetoothSocketWrapper;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.race.events.RaceOBDEvent;
import com.omisoft.hsracer.features.race.structures.OBDDataDTO;
import com.omisoft.hsracer.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

 /**
 * OBD Handler thread
 * Created by dido on 03.08.17.
 */

 public class RaceOBDHandlerThread extends LongMonitoredThread {


  private static final String TAG = RaceOBDHandlerThread.class.getName();
   @Getter
  private final BaseApp context;
  private final SharedPreferences sharedPreferences;
   @Getter
  private final OBDConnManager connManager;
  @Getter
  private boolean connected;
  private static final Pattern EXTRACT_DIGITS_PATTERN = Pattern.compile("-?\\d+");

  @Getter
  private BlockingQueue<Message> commandQueue;
  @Getter
  @Setter
  private String name;
  private BluetoothSocketWrapper wrapper;


   public RaceOBDHandlerThread() {
    setName("Race OBD Thread");
     context = Utils.getBaseApp();
     sharedPreferences = context.getSharedPreferences();
    // Init command map

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device = adapter
        .getRemoteDevice(sharedPreferences.getString(Constants.OBD_DEVICE_KEY, ""));
    this.connManager = new OBDConnManager(device, true, adapter, null);
    commandQueue = new ArrayBlockingQueue<>(100);
    connected = false;
     threadState = ThreadState.WAITING;

   }

   public RaceOBDHandlerThread(RaceOBDHandlerThread original) {
     name = original.getName();
     context = original.getContext();
     sharedPreferences = original.context.getSharedPreferences();

     this.connManager = original.getConnManager();
     commandQueue = original.getCommandQueue();
     connected = original.isConnected();
     threadState = ThreadState.RUNNING;

   }

  @Override
  public void run() {
    threadState = ThreadState.RUNNING;

    try {
      wrapper = connManager.connect();
      initConnection();
      connected = true;
      while (threadState ==ThreadState.RUNNING) {
        Message getCommandFromQueue = commandQueue.take();
        int commandKey = getCommandFromQueue.what;
        switch (commandKey) {
          case GET_SPEED_RPM_COOLANT_TEMP: {
            ObdMultiCommand multiCommand = new ObdMultiCommand();
            multiCommand.add(new SpeedCommand());
            multiCommand.add(new RPMCommand());
            multiCommand.add(new EngineCoolantTemperatureCommand());
            multiCommand.sendCommands(wrapper.getInputStream(), wrapper.getOutputStream());
            String result = multiCommand.getFormattedResult();
            Matcher matcher = EXTRACT_DIGITS_PATTERN.matcher(result);
            List<Integer> results = new ArrayList<>(3);
            while (matcher.find()) {
              results.add(Integer.parseInt(matcher.group()));
            }
            Message m = Message.obtain();
            OBDDataDTO obdDataDTO = new OBDDataDTO();
            obdDataDTO.setObdSpeed(results.get(0));
            obdDataDTO.setObdRpm(results.get(1));
            obdDataDTO.setEngineCoolantTemp(results.get(2));
            Bundle bundle = new Bundle();
            bundle.putParcelable(OBDDataDTO.class.getName(), obdDataDTO);
            m.setData(bundle);
            context.getRaceDataManager().setObdData(new RaceOBDEvent(m));
            break;
          }
          case QUIT_OBD: {
            threadState =ThreadState.FINISHED;
          }
        }
      }
    } catch (Exception e) {
      Log.e(TAG,"OBD IS OFF");
      threadState =ThreadState.CRASHED;

    } finally {
      try {
        if (wrapper != null) {
          wrapper.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void initConnection() throws InterruptedException, IOException {
    new ObdResetCommand().run(wrapper.getInputStream(), wrapper.getOutputStream());
    Thread.sleep(500);
    new EchoOffCommand().run(wrapper.getInputStream(), wrapper.getOutputStream());
    new LineFeedOffCommand().run(wrapper.getInputStream(), wrapper.getOutputStream());
//        new HeadersOffCommand().run(wrapper.getInputStream(),wrapper.getOutputStream());
    new TimeoutCommand(125).run(wrapper.getInputStream(), wrapper.getOutputStream());
//      new HeadersOffCommand().run(wrapper.getInputStream(),wrapper.getOutputStream());
    new SelectProtocolCommand(ObdProtocols.AUTO)
        .run(wrapper.getInputStream(), wrapper.getOutputStream());
  }


 }