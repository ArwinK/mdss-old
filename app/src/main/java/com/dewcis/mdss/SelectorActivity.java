package com.dewcis.mdss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class SelectorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        eventSelectorMotherChild();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void eventSelectorMotherChild() {

        final CharSequence[] items = {"Mother Danger Signs", "Child Danger Signs"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectorActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Select A DSS option ");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    Intent intent = new Intent(getApplicationContext(), DssActivityMother.class);

                    startActivity(intent);
                }else if(which == 1){
                    Intent intent = new Intent(getApplicationContext(), DssActivity.class);
                    startActivity(intent);
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
