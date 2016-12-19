package edu.np.ece.beaconmonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.altbeacon.beacon.BeaconTransmitter;

public class NewBeaconActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private BeaconTransmitter mBeaconTransmitter;

    EditText etRegionName, etUuid, etMajor, etMinor;
    Button btAdd;

    final int JOB_ID = 1;
    String name, uuid, major, minor;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (TextUtils.isEmpty(etRegionName.getText()))
                name = "";
            else
                name = etRegionName.getText().toString();
            if (TextUtils.isEmpty(etUuid.getText()))
                uuid = "";
            else
                uuid = etUuid.getText().toString();
            if (TextUtils.isEmpty(etMajor.getText()))
                major = "";
            else
                major = etMajor.getText().toString();
            if (TextUtils.isEmpty(etMinor.getText()))
                minor = "";
            else
                minor = etMinor.getText().toString();

            BeaconInfo bi = new BeaconInfo(name, uuid, major, minor);
            Intent intent = new Intent();
            intent.putExtra("info", bi);
            setResult(RESULT_OK,intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();

        etRegionName = (EditText) this.findViewById(R.id.etRegionName);
        etUuid = (EditText) this.findViewById(R.id.etUuid);
        etMajor = (EditText) this.findViewById(R.id.etMajor);
        etMinor = (EditText) this.findViewById(R.id.etMinor);
        btAdd = (Button) this.findViewById(R.id.btAdd);
        btAdd.setOnClickListener(listener);
    }
}
