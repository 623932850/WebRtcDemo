package com.study.webrtc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.JavaAudioDeviceModule;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";

    private static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";

    static PeerConnectionFactory factory;

    private SurfaceViewRenderer mSurfaceViewRender;

    static{
        String fieldTrials = "";
        fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL;
        Log.d(TAG, "Enable FlexFEC field trial.");
        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
        fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL;
        Log.d(TAG, "Disable WebRTC AGC field trial.");
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(MyApplication.getInstance())
                        .setFieldTrials(fieldTrials)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);



        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = PeerConnectionFactory.builder()
                .setAudioDeviceModule(JavaAudioDeviceModule.builder(MyApplication.getInstance()).createAudioDeviceModule())
                .setVideoEncoderFactory(new SoftwareVideoEncoderFactory())
                .setVideoDecoderFactory(new SoftwareVideoDecoderFactory())
                .setOptions(options).createPeerConnectionFactory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceViewRender = (SurfaceViewRenderer)findViewById(R.id.surfaceview_render);
        EglBase rootEglBase = EglBase.create();
        mSurfaceViewRender.init(rootEglBase.getEglBaseContext(), null);
        VideoCapturer videoCapturer = null;
        CameraEnumerator cameraEnumerator = new Camera1Enumerator(true);
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        for(String deviceName : deviceNames){
            videoCapturer = cameraEnumerator.createCapturer(deviceName, null);
            break;
        }


        SurfaceTextureHelper surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());

        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());

        videoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        videoCapturer.startCapture(1280, 720, 48000);



        VideoTrack videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrack.addSink(mSurfaceViewRender);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
