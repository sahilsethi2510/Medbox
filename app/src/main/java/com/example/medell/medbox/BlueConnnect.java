package com.example.medell.medbox;

import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueConnnect extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    private Set<BluetoothDevice> pairedDevices;
    Button on, off, scandevices;
    Handler handler;
    Runnable runnable;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String address = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ArrayAdapter<String> adapter;
    ListView scand, pd;
    BluetoothAdapter BA;
    ArrayList<String> hourList = new ArrayList<>();
    ArrayList<String> minuteList = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_connnect);
        on = (Button) findViewById(R.id.connect_turn_on_btn);
        off = (Button) findViewById(R.id.connect_turn_off_btn);
        scandevices = (Button) findViewById(R.id.connect_scan_devices_btn);
        scand = (ListView) findViewById(R.id.scan_device_list);
        pd = (ListView) findViewById(R.id.pair_device_list);
        BA = BluetoothAdapter.getDefaultAdapter();
        spinner = (Spinner) findViewById(R.id.dosage_spinner);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("0");
        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void TURN_ON(View view) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        } else {
            msg("Already on");
        }
    }

    public void TURN_OFF(View view) {
        BA.disable();
        msg("Turned off");

    }

    public void SCAN_DEVICES(View view) {
        BA.startDiscovery();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        scand.setAdapter(adapter);

        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) list.add(bt.getName() + "\n" + bt.getAddress());
        final ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        pd.setAdapter(adapter1);

        pd.setOnItemClickListener(myListClickListener);
        scand.setOnItemClickListener(myListClickListener);
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Finding devices
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                adapter.add(device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            new ConnectBT().execute();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        showTimeDialog(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    BluetoothDevice dispositivo = BA.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
                settime();
            }
        }
    }

    public void settime() {
        try {
            // InputStream in = btSocket.getInputStream();
            handler = new Handler();
            handler.postAtTime(runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (btSocket.isConnected()) {
                            String string1 = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                            String string2 = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                            int in1 = Integer.valueOf(string1);
                            int in2 = Integer.valueOf(string2);
                            if (in1 < 10) {
                                string1 = "0" + string1;
                            }
                            if (in2 < 10) {
                                string2 = "0" + string2;
                            }
                            String s = string1 + string2;
                            btSocket.getOutputStream().write(s.toString().getBytes());
                        }
                    } catch (Exception e) {
                    }
                }
            }, 500);
        } catch (Exception e1) {
        }
    }

    public void set() {
        try {
            // InputStream in = btSocket.getInputStream();
            handler = new Handler();
            handler.postAtTime(runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (btSocket.isConnected()) {

                            for (int i = 0; i < hourList.size(); i++) {
                                String s1 = hourList.get(i);
                                String s2 = minuteList.get(i);
                                int in1 = Integer.valueOf(s1);
                                int in2 = Integer.valueOf(s2);
                                if (in1 < 10) {
                                    s1 = "0" + s1;
                                }
                                if (in2 < 10) {
                                    s2 = "0" + s2;
                                }
                                String string = s1 + s2;
                                btSocket.getOutputStream().write((string).toString().getBytes());
                            }
                            btSocket.getOutputStream().write("0000000000000000".toString().getBytes());
                            hourList.clear();
                            minuteList.clear();
                        }
                    } catch (Exception e) {
                    }
                }
            }, 500);
        } catch (Exception e1) {
        }
    }

    void showTimeDialog(final int position) {
        if (position > 0) {
            // TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(BlueConnnect.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    hourList.add(String.valueOf(selectedHour));
                    minuteList.add(String.valueOf(selectedMinute));
                    showTimeDialog(position - 1);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        } else if (position == 0) {
            set();
            for (int i = 0; i < hourList.size(); i++) {
                Log.e("HOUR " + i + " :", hourList.get(i));
                Log.e("MINUTE " + i + " :", minuteList.get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.sign_out_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:{
                mAuth.signOut();
                Intent intent=new Intent(BlueConnnect.this,Login.class);
                startActivity(intent);
                Toast.makeText(this, "USER SIGNED OUT", Toast.LENGTH_SHORT).show();
                break;

            }
            case R.id.deactivate:{
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential("sahil.sethi2648@gmail.com", "1234567");

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(BlueConnnect.this, "User account deleted", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            }
                        });

            }
        }

        return super.onOptionsItemSelected(item);
    }
}
