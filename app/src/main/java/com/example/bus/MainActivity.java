package com.example.bus;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    public String dataKey = "95m7mGemWlThUNjzGE%2BDlk%2B3Oergfi0G0veBVR0kIqwjnmAkAct6%2FLzyt2nmysp%2BoPevwerShoS4CGBOMQs4uA%3D%3D";
    private String requestUrl;
    ArrayList<Item> list = null;
    Item bus = null;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

//        AsyncTask
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }
    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            requestUrl = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=" +dataKey+ "&stationId=233001213";
            try {
                boolean b_locationNo1 = false;
                boolean b_plateNo1 =false;
                boolean b_routeId = false;
                boolean b_stationId = false;

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<Item>();
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                        case XmlPullParser.END_TAG:
                            if(parser.getName().equals("busArrivalList") && bus != null) {
                                list.add(bus);
                            }
                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("busArrivalList")){
                                bus = new Item();
                            }
                            if (parser.getName().equals("locationNo1")) b_locationNo1 = true;
                            if (parser.getName().equals("plateNo1")) b_plateNo1 = true;
                            if (parser.getName().equals("routeId")) b_routeId = true;
                            if (parser.getName().equals("stationId")) b_stationId = true;
                            break;
                        case XmlPullParser.TEXT:
                            if(b_locationNo1){
                                bus.setLocationNo1(parser.getText());
                                b_locationNo1 = false;
                            } else if(b_plateNo1) {
                                bus.setPlateNo1(parser.getText());
                                b_plateNo1 = false;
                            } else if (b_routeId) {
                                bus.setRouteId(parser.getText());
                                b_routeId = false;
                            } else if(b_stationId) {
                                bus.setStationId(parser.getText());
                                b_stationId = false;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //어답터 연결
            MyAdapter adapter = new MyAdapter(getApplicationContext(), list);
            recyclerView.setAdapter(adapter);
        }
    }
}
