package com.dewcis.mdss.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.mdss.R;
import com.dewcis.mdss.model.Survey;

import java.util.ArrayList;

/**
 * Created by henriquedn on 7/7/15.
 */
public class SurveyAdapter extends BaseAdapter{

    Context context;
    ArrayList<Survey> surveys;
    boolean _show_button = true;
    public  SurveyAdapter(Context context, ArrayList<Survey> surveys){
        this.context = context;
        this.surveys = surveys;
    }

    @Override
    public int getCount() {
        return null == surveys ? 0 : surveys.size();
    }

    @Override
    public Object getItem(int position) {
        return surveys.get(position);
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

            View emptyView = inflater.inflate(R.layout.survey_list_item, null);
            convertView = emptyView.findViewById(R.id.empty);
        }
        else{
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.survey_list_item, null);
            }

            TextView txtSurveyId  = (TextView) convertView.findViewById(R.id.txtSurveyId);
            TextView txtDetails  = (TextView) convertView.findViewById(R.id.txtDetails);

            Survey s = surveys.get(position);

            txtSurveyId.setText("Report : " + s.getSurvey_id());
            txtDetails.setText("\nVillage : " + s.getTxtVillageName()
                               + "\nHousehold No.: " + s.getTxtHouseholdNum()
                               + "\nHousehold Head : " + s.getTxtHouseholdMember());

        }

        return convertView;
    }

}
