package com.dewcis.mdss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.mdss.R;
import com.dewcis.mdss.model.Message;

import java.util.ArrayList;

/**
 * Created by henriquedn on 7/7/15.
 */
public class MessageAdapter extends BaseAdapter{

    Context context;
    ArrayList<Message> messages;
    boolean _show_button = true;
    public  MessageAdapter(Context context, ArrayList<Message> surveys){
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

            View emptyView = inflater.inflate(R.layout.message_list_item, null);
            convertView = emptyView.findViewById(R.id.empty);
        }
        else{
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.message_list_item, null);
            }

            TextView txtMessageId  = (TextView) convertView.findViewById(R.id.txtMessageId);
            TextView txtMessageDetails  = (TextView) convertView.findViewById(R.id.txtMessageDetails);

            Message s = messages.get(position);

            txtMessageId.setText("Message : " + s.getSurvey_id());
            txtMessageDetails.setText("\n Name of clinician  : " + s.getTxtOficerName()
                    + "\n Health facility  : " + s.getTxtHouseholdNum()
                    + "\n Patient Name:  " + s.getTxtHouseholdMember()
                    + "\n Village :  " + s.getvillageID()
                    );

        }

        return convertView;
    }

}
