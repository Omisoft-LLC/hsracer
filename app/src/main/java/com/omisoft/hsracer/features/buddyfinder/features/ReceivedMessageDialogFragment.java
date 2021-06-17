package com.omisoft.hsracer.features.buddyfinder.features;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.buddyfinder.dto.ResponseMessageDTO;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderWebSocketCommandEvent;
import com.omisoft.hsracer.features.buddyfinder.threads.BuddyFinderWebSocketThread;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Omisoft LLC. on 6/13/17.
 * Dialog frame for buddy finder which is shown when other player is sending a message to you
 */

public class ReceivedMessageDialogFragment extends DialogFragment {

  private ResponseMessageDTO response;
  private Long restId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    response = bundle.getParcelable("message");

  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    restId = sharedPreferences.getLong(Constants.REST_ID, 0);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Message from: " + response.getFromNickName());
    builder.setMessage(response.getMessage());

    final EditText input = new EditText(getActivity());
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    input.setSingleLine(false);
    input.setHint("message");
    builder.setView(input);

    builder.setPositiveButton("Send message", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Bundle bundle = new Bundle();
        bundle.putLong("otherRacerId", response.getRestIdFrom());
        bundle.putLong("myId", restId);
        bundle.putString("message", input.getText().toString());

        sendCommandMessageToWebSocketThread(bundle);
      }

    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    return builder.create();
  }

  private void sendCommandMessageToWebSocketThread(Bundle bundle) {
    Message message = Message.obtain();
    message.setData(bundle);
    message.what = BuddyFinderWebSocketThread.ASK_TO_RACE;
    EventBus.getDefault().post(new BuddyFinderWebSocketCommandEvent(message));
  }

}
