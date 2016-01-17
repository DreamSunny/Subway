package com.dsunny.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.dsunny.base.AppBaseActivity;
import com.dsunny.db.StationDao;
import com.dsunny.engine.SubwayMap;
import com.dsunny.subway.R;
import com.infrastructure.net.RequestCallback;


/**
 * 搜索页面
 */
public class SearchActivity extends AppBaseActivity {

    private TextView tvHelloWorld;
    private RequestCallback mRequestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        Bundle bundle = getIntent().getExtras();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        tvHelloWorld = findAppViewById(R.id.tv_helloworld);
    }

    @Override
    protected void loadData() {
        setActionBarTitle("北京地铁");
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SubwayMap map = new SubwayMap();
                StationDao dao = new StationDao();
//                List<Station> lstStations = dao.getAllStationNamesAndAbbrs();
//                for (Station s1 : lstStations) {
//                    for (Station s2 : lstStations) {
//                        if (!s1.Name.equals(s2.Name)) {
//                            map.search(s1.Name, s2.Name);
//                        }
//                        publishProgress(s1.Name, s2.Name);
//                    }
//                }
                map.search("丰台科技园", "北京站");
                return "ok";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tvHelloWorld.setText(values[0] + "-" + values[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                tvHelloWorld.setText(s);
            }
        }.execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
