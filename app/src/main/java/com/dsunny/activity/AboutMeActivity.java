package com.dsunny.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.dsunny.base.AppBaseActivity;
import com.dsunny.db.BaseDao;
import com.dsunny.engine.SubwayMap;
import com.dsunny.subway.R;
import com.infrastructure.utils.UtilsLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * 关于我页面
 */
public class AboutMeActivity extends AppBaseActivity {

    private TextView tvTest;

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_me);

        tvTest = findAppViewById(R.id.tv_test);
    }

    @Override
    protected void loadData() {
        final TestDao dao = new TestDao();
        final SubwayMap mSubwayMap = new SubwayMap();

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String fromStationId = "";
                String toStationId = "";
                String fromStationName = "";
                String toStationName = "";

                try {
                    //0101,0201,0401,0501,0607,0701,0801,0901,1001,1301,1401,1501,9401,9501,9601,9701,9801,9902
                    for (String from : dao.getAllStationIds()) {
                        for (String to : dao.getAllStationIds(from)) {
                            fromStationId = from;
                            toStationId = to;
                            fromStationName = dao.getStationName(from);
                            toStationName = dao.getStationName(to);
                            publishProgress(fromStationId + "-" + toStationId + "(" + fromStationName + "-" + toStationName + ")");
                            mSubwayMap.search(fromStationName, toStationName);
                        }
                    }
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String msg = fromStationId + "-" + toStationId + "(" + fromStationName + "-" + toStationName + ")" + "\r\n" + sw.toString();
                    UtilsLog.d(msg);
                    return msg;
                }

                return "ok!";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                tvTest.setText(values[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                tvTest.setText(s);
            }
        }.execute();
    }


    private static class TestDao extends BaseDao {
        public List<String> getAllStationIds() {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT ID ");
            sql.append(" FROM STATION ");
            sql.append(" WHERE State == '1' ");
            sql.append(" ORDER BY ID ASC ");

            return queryListString(sql.toString());
        }

        public List<String> getAllStationIds(String sid) {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT ID ");
            sql.append(" FROM STATION ");
            sql.append(" WHERE State == '1' ");
            sql.append(" AND ID > '").append(sid).append("' ");
            sql.append(" ORDER BY ID ASC ");

            return queryListString(sql.toString());
        }

        public String getStationName(String sid) {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT Name ");
            sql.append(" FROM STATION ");
            sql.append(" WHERE ID = '").append(sid).append("' ");

            return queryString(sql.toString());
        }

    }
}
