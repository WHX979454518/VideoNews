package peixun.videoplayer.full;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import peixun.videoplayer.R;

/**
 * 使用VideoViewjinxing视频播放的Activity
 * 请使用open方法，传入视频ulr，启动Activity
 */
public class VideoViewMainActivity extends AppCompatActivity {

    private static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";
    public static void open(Context context,String videoPath) {
        Intent intent = new Intent(context,VideoViewMainActivity.class);
        intent.putExtra(KEY_VIDEO_PATH,videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置窗口的背景色
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        //设置当前视图内容
        setContentView(R.layout.activity_video_view);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        //.初始化视图,
        initBufferViews();
        // 初始化videoview
        initVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if(pm.isScreenOn()){//如果屏幕是亮的
            videoView.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));//已经把Path传过来了，直接把路径get过里用
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();//停止
    }

    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private ImageView ivLoading;//缓冲信息图片
    private TextView tvBufferInfo;//缓冲信息（文本信息：显示100kb/s，69%）
    private int downloadSped;//当前缓冲速率
    private int bufferPercent;//当前缓冲百分比

    private void initBufferViews(){
        tvBufferInfo = (TextView) findViewById(R.id.tvBufferInfo);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }
    private void initVideoView() {
        videoView = (VideoView) findViewById(R.id.videoView);
        //控制（就是暂停、快进、播放键）
        videoView.setMediaController(new MediaController(this));
        //保持屏幕长亮
        videoView.setKeepScreenOn(true);
        //让其获取焦点
        videoView.requestFocus();
        //资源准备监听处理
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                //设置缓冲取得大小（缓冲区填充完成才会播放）。默认值就是1M
                mediaPlayer.setBufferSize(1024 * 1024);
            }
        });
        //缓冲更新的监听（为了知道缓冲时的百分比）
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //percent:当前缓冲的百分比
                bufferPercent = percent;
                updataBufferViewInfo();
            }
        });
        //“状态”信息的监听
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    //开始缓冲
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (videoView.isPlaying()) {//是否正在播放
                            videoView.pause();
                        }
                        showBufferViews();
                        break;
                    //结束缓冲
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        videoView.start();//开始播放视频
                        hideBufferViews();
                        break;
                    //缓冲时的下载速率
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        downloadSped = extra;
                        updataBufferViewInfo();
                        break;
                }
                return true;
            }
        });
    }
    //在开始缓冲时调用的
    private void updataBufferViewInfo() {
        tvBufferInfo.setVisibility(View.VISIBLE);
        ivLoading.setVisibility(View.VISIBLE);
        downloadSped =0;
        bufferPercent =0;
    }
    //在结束缓冲的时调用
    private void hideBufferViews() {
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }
    //缓冲时，速率发生变化
    private void showBufferViews() {
        String info =String.format(Locale.CHINA,"%d%%,%dkb/s",bufferPercent,downloadSped);
        tvBufferInfo.setText(info);
    }
}
