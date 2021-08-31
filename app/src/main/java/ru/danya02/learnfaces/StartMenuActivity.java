package ru.danya02.learnfaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class StartMenuActivity extends AppCompatActivity {
    ProgressDialog dialog;
    testAddressRunner runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        final Button start = findViewById(R.id.b_start);
        start.setText(R.string.start_button_text);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(v);
            }
        });
        final Button rtfs = findViewById(R.id.b_rtfs);
        rtfs.setText(R.string.rtfs_button_text);
        rtfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRtfs(v);
            }
        });
        final Button dataview = findViewById(R.id.b_dataview);
        dataview.setText(R.string.b_dataview_text);
        dataview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDataview(v);
            }
        });
        final Button update = findViewById(R.id.b_update);
        update.setText(R.string.b_update_text);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUpdate(v);
            }
        });
        final Button save = findViewById(R.id.b_save);
        save.setText(R.string.b_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddress(v);
            }
        });
        loadAddress();
    }

    public void start(View view) {
        EditText t = findViewById(R.id.question_num_startmenu);
        Intent toGame = new Intent(this, GameActivity.class);
        String qs = String.valueOf(t.getText());
        Integer qn = 10;
        try {
            qn = Integer.parseInt(qs);
        } catch (NumberFormatException e) {
            Log.wtf("startMenu", "Invalid number from number-only EditText?!", e);
        }
        toGame.putExtra("questions", qn);
        toGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(toGame);
    }

    public void toRtfs(View view) {
        Intent toFS = new Intent(Intent.ACTION_VIEW);
        toFS.setData(Uri.parse(getString(R.string.source_link)));
        startActivity(toFS);
    }

    public void toDataview(View view) {
        Intent to_data = new Intent(this, DatabaseViewActivity.class);
        startActivity(to_data);
    }

    public void toUpdate(View view) {
        Intent to_update = new Intent(this, UpdaterActivity.class);
        startActivity(to_update);
    }

    public void loadAddress() {
        CheapoConfigManager configManager = new CheapoConfigManager(getApplicationContext());
        EditText addr = findViewById(R.id.source_address);
        String s = configManager.getDataField("databaseUri");
        if (s == null) {
            try {
                configManager.setDataField("databaseUri", getString(R.string.data_base_directory_link));
            } catch (IOException e) {
                Log.wtf("mainMenu", "Error while writing to my directory?!", e);
                Toast.makeText(this, R.string.error_saving_address, Toast.LENGTH_LONG).show();
            }
        }
        addr.setText(configManager.getDataField("databaseUri"));
    }

    public void setAddress(View view) {

        showDialog();
        runner = new testAddressRunner();
        runner.execute(this);


    }

    public void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(getApplicationContext());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(getString(R.string.testing_wait_popup));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });
    }

    public void hideDialog() {
        dialog.dismiss();
    }

    static class testAddressRunner extends AsyncTask<StartMenuActivity, Integer, StartMenuActivity> {

        protected StartMenuActivity doInBackground(StartMenuActivity... activities) {
            final StartMenuActivity activity = activities[0];
            final EditText addr = activity.findViewById(R.id.source_address);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addr.setText(addr.getText().toString().trim());
                    if (addr.getText().charAt(addr.getText().toString().length() - 1) != '/') {
                        addr.getText().append('/');
                    }

                }
            });

            DatabaseTools.databaseStatus status = DatabaseTools.databaseAddressCheck(addr.getText().toString());
            if (status == DatabaseTools.databaseStatus.BAD_URI) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, R.string.invalid_address_toast, Toast.LENGTH_SHORT).show();
                    }
                });
                return activity;
            }
            if (status == DatabaseTools.databaseStatus.NOT_CONNECTING) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, R.string.cannot_download_to_test_toast, Toast.LENGTH_SHORT).show();
                    }
                });
                return activity;
            }
            if (status == DatabaseTools.databaseStatus.BAD_JSON) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, R.string.invalid_json_to_test_toast, Toast.LENGTH_SHORT).show();
                    }
                });
                return activity;
            }
            CheapoConfigManager configManager = new CheapoConfigManager(activity);
            try {
                configManager.setDataField("databaseUri", String.valueOf(addr.getText()));
            } catch (IOException e) {
                Log.wtf("mainMenu", "Error while writing to my directory?!", e);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, R.string.error_saving_address, Toast.LENGTH_LONG).show();
                    }
                });
                return activity;
            }
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, R.string.address_set_ok_toast, Toast.LENGTH_SHORT).show();
                }
            });
            return activity;
        }

        @Override
        protected void onPostExecute(final StartMenuActivity startMenuActivity) {
            startMenuActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startMenuActivity.hideDialog();

                }
            });
        }
    }


}
