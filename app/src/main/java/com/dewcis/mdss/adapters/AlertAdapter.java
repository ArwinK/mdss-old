package com.dewcis.mdss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.mdss.R;
import com.dewcis.mdss.model.Alerts;

import java.util.ArrayList;

/**
 * Created by Arwin Kish on 11/9/2016.
 */
public class AlertAdapter extends BaseAdapter {

    Context context;
    ArrayList<Alerts> messages;
    boolean _show_button = true;
    public  AlertAdapter(Context context, ArrayList<Alerts> surveys){
        this.context = context;
        this.messages = surveys;
    }

    @Override
    public int getCount() {
        return null == messages ? 0 : messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(this.getCount() == 0){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View emptyView = inflater.inflate(R.layout.alert_list_item, null);
            convertView = emptyView.findViewById(R.id.empty);
        }
        else{
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.alert_list_item, null);
            }

            TextView txtMessageId  = (TextView) convertView.findViewById(R.id.txtMessageId);
            TextView txtMessageDetails  = (TextView) convertView.findViewById(R.id.txtMessageDetails);

            Alerts s = messages.get(position);

            txtMessageId.setText("Message : " + s.getSurvey_id());
            txtMessageDetails.setText("\n Name of clinician  : " + s.getTxtRemarks()
                    + "\n Patient Status  : " + s.getTxtOficerName()
                    + "\n Patient Name:  " + s.getTxtHouseholdNum()
                    + "\n Village :  " + s.getvillageID()
            );

        }

        return convertView;
    }

}