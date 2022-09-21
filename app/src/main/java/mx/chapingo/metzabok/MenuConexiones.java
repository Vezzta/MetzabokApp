package mx.chapingo.metzabok;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuConexiones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_conexiones);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClickBluetooth(View view){
        Intent intent = new Intent(MenuConexiones.this, DispositivosVinculados.class);
        startActivity(intent);
    }

    public void onClickExit(View view){
        finish();
        System.exit(0);
    }
}