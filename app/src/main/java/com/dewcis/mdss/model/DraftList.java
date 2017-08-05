package com.dewcis.mdss.model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dewcis.mdss.MainActivity;
import com.dewcis.mdss.R;
import com.dewcis.mdss.utils.DraftActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Arwin Kish on 5/17/2017.
 */
public class DraftList extends ActionBarActivity {
    List<String> items;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_draft);

        items = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("DRAFT_PREFS", 0);
        String wordString = sharedPreferences.getString("draft_list", "");
        String[] itemsWords = wordString.split(",");

        Collections.addAll(items, itemsWords);
        mListView = (ListView) findViewById(R.id.draft_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = mListView.getAdapter().getItem(i).toString();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                int type = 0;
                if(value.contains("postpartum")){
                    type = 0;
                }else if(value.contains("pregnant")){
                    type = 1;
                }else if(value.contains("children")){
                    type = 2;
                }else if(value.contains("newborns")){
                    type = 3;
                }else if(value.contains("other_mothers")){
                    type = 4;
                }else if(value.contains("other_members")){
                    type = 5;
                }else {
                    value = "";
                }

                if(!value.equals("")){
                    intent.putExtra("type", type);
                    intent.putExtra("draft_key", value);
                    intent.putExtra("draft_index", i);
                    startActivity(intent);
                }

//                else if(value.contains("DSSpost-partum")){
//                    Intent e = new Intent(getApplicationContext(), DangerSignsActivity.class);
//                    e.putExtra("type", type);
//                    e.putExtra("draft_key", value);
//                    e.putExtra("draft_index", i);
//                    e.putExtra("pre", "514");
//                    startActivity(e);
//                }

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DraftActivity.removeDraft(DraftList.this, i);
                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });


    }
}
