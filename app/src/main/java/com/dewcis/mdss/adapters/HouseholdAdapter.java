package com.dewcis.mdss.adapters;

/**
 * Created by Arwin Kish on 12/22/2016.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dewcis.mdss.MainActivity;
import com.dewcis.mdss.R;
import com.dewcis.mdss.model.Household;

import java.util.List;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.MyViewHolder> {
    private Context mContext;
    private List<Household> householdList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, content, content_main;
        public CardView cardView;
        public RadioButton radioButton;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            content = (TextView) view.findViewById(R.id.content);
            content_main = (TextView) view.findViewById(R.id.content_description);
            cardView = (CardView) view.findViewById(R.id.card_view);
//            radioButton = (RadioButton) view.findViewById(R.id.checkbox);

        }
    }

    public HouseholdAdapter(Context mContext, List<Household> householdList) {
        this.mContext = mContext;
        this.householdList = householdList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Household household = householdList.get(position);
        holder.content.setText(household.getName());
        holder.content_main.setText(household.getNumOfMember() + "");
        holder.title.setText(household.getContent());

        final int survey = household.getSurvey();
        final int type = household.getType();
        final int numberOfMembers = household.getNumOfMember();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberOfMembers > 0){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("survey_id", survey);
                    switch (type){
                        case 0:
                            Toast.makeText(mContext, "Fill survey for Postpartum Mother", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(mContext, "Fill survey for Pregnant Mother", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(mContext, "Fill survey for Children under 5 years", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(mContext, "Fill survey for New born", Toast.LENGTH_LONG).show();
                            break;
                        case 4:
                            Toast.makeText(mContext, "Fill survey for Other Women", Toast.LENGTH_LONG).show();
                            break;
                        case 5:
                            Toast.makeText(mContext, "Fill survey for Other Members", Toast.LENGTH_LONG).show();
                            break;
                    }
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, "Survey Completed for this category", Toast.LENGTH_LONG).show();
                }

            }
        });

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberOfMembers > 0){
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("survey_id", survey);
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, "Survey Completed for this category", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return householdList.size();
    }
}
