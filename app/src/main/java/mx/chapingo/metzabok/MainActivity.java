package mx.chapingo.metzabok;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    TextView textE;
    Switch switchE;
    Button btnTempo;
    ArrayAdapter<CharSequence> adapter;

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    char MyCaracter=(char) msg.obj;
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();


        //Spinner
        spinner = (Spinner) findViewById(R.id.spinnerTempo);
        adapter = ArrayAdapter.createFromResource(this, R.array.temporizador, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //TextView Estado
        textE = (TextView) findViewById(R.id.textEstado);

        //Switch Estado
        switchE = (Switch) findViewById(R.id.switchEstado);

        //Botón Temporizador
        btnTempo = (Button) findViewById(R.id.btnTempo);



    }


    //Método para encendido y apagado
    public void onClickEstado(View view){
        if (view.getId()==R.id.switchEstado){
            if (switchE.isChecked()){
                textE.setTextColor(Color.GREEN);
                textE.setText("ON");
                MyConexionBT.write(1);
            }else{
                textE.setTextColor(Color.BLACK);
                textE.setText("OFF");
                MyConexionBT.write(0);
            }
        }
    }

    //Método para temporizador
    public void onClickTempo(View view){
        int position = spinner.getSelectedItemPosition();

        if(position == 0){
            MyConexionBT.write(2);
            switchE.setChecked(true);
            textE.setTextColor(Color.GREEN);
            textE.setText("ON");
        }
        if(position == 1){
            MyConexionBT.write(3);
            switchE.setChecked(true);
            textE.setTextColor(Color.GREEN);
            textE.setText("ON");
        }
        if(position == 2){
            MyConexionBT.write(4);
            switchE.setChecked(true);
            textE.setTextColor(Color.GREEN);
            textE.setText("ON");
        }
        if(position == 3){
            MyConexionBT.write(5);
            switchE.setChecked(true);
            textE.setTextColor(Color.GREEN);
            textE.setText("ON");
        }
    }

    //Mètodo para Volver y desconectar
    public void onClickBack(View view){
        if(btSocket!= null){
            try {
                btSocket.close();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(MainActivity.this, MenuConexiones.class);
            startActivity(intent);
            finish();
        }
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }

        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            btSocket.close();
        } catch (IOException e2) {}
    }

    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] byte_in = new byte[1];
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        public void write(int b)
        {
            try {
                mmOutStream.write(b);
            }
            catch (IOException e)
            {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }


}