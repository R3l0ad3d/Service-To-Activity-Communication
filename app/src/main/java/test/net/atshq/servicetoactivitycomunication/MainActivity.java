package test.net.atshq.servicetoactivitycomunication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView tvData;


    private static final int SEND_CODE = 1;
    private static final int RESPONS_CODE = 2;
    private boolean isConnect = false;
    private Messenger messengerService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnect = true;
            messengerService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnect=false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.etData);
        tvData = findViewById(R.id.tvReceive);
    }

    public void sentDataToService(View view) {
        String data = editText.getText().toString();

        //create a message to send commend to service
        Message message = Message.obtain(null,SEND_CODE);
        //define handler where to reply
        message.replyTo=new Messenger(new ResponsHandler());

        //send data to service via bundle
        Bundle bundle = new Bundle();
        bundle.putString("dataFromActivity",data);

        //set data
        message.setData(bundle);

        try {
            //send message
            messengerService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startService(View view) {
        Intent intent = new Intent(this,MessengerService.class);
        if(!isConnect){
            bindService(intent,connection,BIND_AUTO_CREATE);
        }
    }

    public void stopService(View view) {
        if(isConnect){
            unbindService(connection);
        }
    }

    protected class ResponsHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==RESPONS_CODE){
                Bundle receive = msg.getData();
                String data = receive.getString("sendFromService");
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                tvData.setText(data);
            }
        }
    }
}
