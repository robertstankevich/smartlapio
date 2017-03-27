package fi.minscie.duckt.smartshovel;

import static fi.minscie.duckt.smartshovel.Constants.FIFTH_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.FIRST_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.SECOND_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.THIRD_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.FOURTH_COLUMN;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DuckT on 24.3.2017.
 */

public class HistoryListAdapter extends BaseAdapter{
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFourth;
    TextView txtFifth;

    public HistoryListAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();


        if(convertView == null){

            convertView=inflater.inflate(R.layout.history_adapter_view, null);

            txtFirst=(TextView) convertView.findViewById(R.id.timestamp);
            txtSecond=(TextView) convertView.findViewById(R.id.time);
            txtThird=(TextView) convertView.findViewById(R.id.weight);
            txtFourth=(TextView) convertView.findViewById(R.id.temperature);
            txtFifth=(TextView) convertView.findViewById(R.id.count);

        }

        HashMap<String, String> map=list.get(position);
        txtFirst.setText(map.get(FIRST_COLUMN));
        txtSecond.setText(map.get(SECOND_COLUMN));
        txtThird.setText(map.get(THIRD_COLUMN));
        txtFourth.setText(map.get(FOURTH_COLUMN));
        txtFifth.setText(map.get(FIFTH_COLUMN));

        return convertView;
    }

}
