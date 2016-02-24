package com.gnr.esgi.taggle.activitie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.util.Config;

public class SettingsActivity extends AppCompatActivity {

    CheckBox nighModeCheckbox;
    Button purgeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Config.getTheme());
        setContentView(R.layout.activity_settings);

        nighModeCheckbox = (CheckBox) findViewById(R.id.settings_night_mode_checkbox);
        nighModeCheckbox.setChecked(Taggle.getSession().getNightMode());
        nighModeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Taggle.getSession().setNightMode(nighModeCheckbox.isChecked());

                // Restart application after changing skin
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        purgeButton = (Button) findViewById(R.id.settings_purge);
        purgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Taggle.getDao().purge();

                Toast.makeText(getApplicationContext(), "Done !", Toast.LENGTH_LONG).show();
            }
        });
    }
}
