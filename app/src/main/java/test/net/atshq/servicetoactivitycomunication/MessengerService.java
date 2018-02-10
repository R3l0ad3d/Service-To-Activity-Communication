package test.net.atshq.servicetoactivitycomunication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MessengerService extends Service {

    private static final int RESPONS_CODE=2;
    private static final int RECEIVE_CODE=1;

    //data receive from activity
    private Handler incommingHandeler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==RECEIVE_CODE){
                //data receive from activity via Bundle
                Bundle bundle = msg.getData();
                String data = bundle.getString("dataFromActivity");
                Log.d("Messenger Service : ",data);
                //Create a Message for reply to Activity
                Message responsToActivity = Message.obtain(null,RESPONS_CODE,0,0);
                Bundle sendBundle = new Bundle();
                sendBundle.putString("sendFromService",data);
                responsToActivity.setData(sendBundle);

                try {
                    //send data to Activity
                    msg.replyTo.send(responsToActivity);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //create messenger for return Binder
    private Messenger messenger = new Messenger(incommingHandeler);

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return messenger.getBinder();
    }
}
