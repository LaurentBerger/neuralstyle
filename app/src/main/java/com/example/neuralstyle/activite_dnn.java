package com.example.neuralstyle;



import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.opencv.imgproc.Imgproc;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;

public class activite_dnn extends CameraActivity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;
    private boolean neural_style = false;
    boolean enregistrer = false;
    Button 				boutonCpt;
    String  modelName;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public activite_dnn() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activite_dnn);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activite_dnn);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        boutonCpt = (Button) findViewById(R.id.take_dnn_picture);
        boutonCpt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enregistrer = true;
            }
        });
        Bundle b = getIntent().getExtras();
        String value="";
        if(b != null)
            value = b.getString("model");
        modelName = value;
    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        String weights = getPath(modelName, this);
        String proto = "";
        if (!weights.isEmpty()){
            net = Dnn.readNetFromTorch( weights);
            neural_style = true;
            Log.i(TAG, "Network loaded successfully");
        }
        else
        {
            neural_style = false;
        }
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Log.i(TAG, "inputFrame");
        Mat frametmp = inputFrame.rgba();
        Mat frame = new Mat();
        Log.i(TAG, "cvtColor");
        Imgproc.cvtColor(frametmp, frame, Imgproc.COLOR_RGBA2RGB);
        if (neural_style) {
            Imgproc.resize(frame,frame,new Size(),0.25,0.25);
            Mat blob = Dnn.blobFromImage(frame, 1.0,
                    new Size(frame.cols(), frame.rows()),
                    new Scalar(103.939, 116.779, 123.68), false,false);
            net.setInput(blob);
            Mat out = net.forward();
            List<Mat> outMat= new ArrayList<Mat>(0);
            Dnn.imagesFromBlob(out,outMat);
            double x = (outMat.get(0)).get(0,0)[0];
            Log.i(TAG, Double.toString(x));
            Core.add(outMat.get(0), new Scalar(103.939, 116.779, 123.68), outMat.get(0));
            x = (outMat.get(0)).get(0,0)[0];
            Log.i(TAG, Double.toString(x));
            Mat framePaint = new Mat();
            Log.i(TAG, "convertTo");
            outMat.get(0).convertTo(framePaint, CvType.CV_8U);
            Mat frame2 = new Mat();
            Log.i(TAG, "cvtColor");
            Imgproc.cvtColor(framePaint, frame2, Imgproc.COLOR_RGB2RGBA);
            Log.i(TAG, "END*****************************************************************************");
            Mat frame3 = new Mat();

            Imgproc.resize(frame2,frame3,new Size(frametmp.cols(),frametmp.rows()));
            if (enregistrer)
            {
                File sdRoot = Environment.getExternalStorageDirectory();
                String dir = "/DCIM/Data Collection/";
                String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".png";
                File mkDir = new File(sdRoot, dir);
                mkDir.mkdirs();
                File pictureFile = new File(sdRoot, dir + fileName);

                Imgcodecs.imwrite("/sdcard/" + dir + fileName,frame3);
                enregistrer = false;

            }
            return frame3;
        }
        else
        {
            return frametmp;
        }

    }
    // Upload file to storage and return a path.
    private static String getPath(String file, Context context) {
//        AssetManager assetManager = context.getAssets();
        File chemin=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        BufferedInputStream inputStream = null;
        FileInputStream fis = null;
        try {
            // Read data from assets.
            File fichier = new File(chemin, file);
            fis = new FileInputStream(fichier);
            inputStream = new BufferedInputStream(fis);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }
    private static final String[] classNames = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};
    private Net net;
}
