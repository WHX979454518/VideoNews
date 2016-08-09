package peixun.videonews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;

import butterknife.OnClick;
import io.vov.vitamio.Vitamio;
import peixun.videoplayer.full.VideoViewMainActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLocal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Vitamio.isInitialized(this);

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        //ButterKnife.bind(this);
        btnLocal = (Button) findViewById(R.id.btnLocal);
        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"dianjile", Toast.LENGTH_SHORT).show();
                VideoViewMainActivity.open(MainActivity.this, getTestVideo1());
            }
        });
    }
//    @OnClick(R.id.btnLocal)
//    public void demoPlay(){
//        Toast.makeText(this,"dianjile", Toast.LENGTH_SHORT).show();
//        VideoViewMainActivity.open(this, getTestVideo1());
//    }

    public String getTestVideo1() {
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }
}
