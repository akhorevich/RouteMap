package nortti.ru.routemap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DataActivity extends AppCompatActivity{

    private FragmentTabHost mTabHost;
    private Button btnFind;
    public Double lat1;
    public Double lat2;
    public Double lng1;
    public Double lng2;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.from)),
                FromFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.to)),
                ToFragment.class, null);

        btnFind= (Button)findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getBaseContext(),MapsActivity.class);
                i.putExtra("lat1",lat1);
                i.putExtra("lat2",lat2);
                i.putExtra("lng1",lng1);
                i.putExtra("lng2",lng2);
                startActivity(i);
            }
        });

    }



}
