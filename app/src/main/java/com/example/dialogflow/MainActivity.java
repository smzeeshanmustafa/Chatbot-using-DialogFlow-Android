package com.example.dialogflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import java.util.Locale;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements AIListener {
    private Button listenButton;
    int Checker=0;
    private TextView resultTextView,processtext,text;
    private AIService aiService;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private static String TAG = "PermissionDemo";
    FirebaseDatabase database;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listenButton = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.textView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
// Dialog Flow Api Token Below
        final AIConfiguration config = new AIConfiguration("YOUR Dialog Flow API Token here",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
        }
    }


    public void listenButtonOnClick(final View view) {
        aiService.startListening();
    }

    public void onResult(final AIResponse response) {

        Result result = response.getResult();
        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        processtext=(TextView)findViewById(R.id.textView2);
        processtext.setText("Answered");
        listenButton = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.textView);
        text = (TextView)findViewById(R.id.textView3) ;
        // Show results in TextView.
        String Answer ="input.welcome";
        resultTextView.setText("Query:" + result.getResolvedQuery());

        // +
        //                "\nAction: " + result.getAction() +
        //                "\nParameters: " + parameterString)



            final String h=result.getFulfillment().getSpeech();
            tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub
                    if(status == TextToSpeech.SUCCESS){
                        int result=tts.setLanguage(Locale.ENGLISH);
                        if(result==TextToSpeech.LANG_MISSING_DATA ||
                                result==TextToSpeech.LANG_NOT_SUPPORTED){
                            Log.e("error", "This Language is not supported");
                        }
                        else{
                            tts.speak(h,TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                    else
                        Log.e("error", "Initilization Failed!");
                }
            });

    }
    @Override
    public void onError(final AIError error) {
        processtext=(TextView)findViewById(R.id.textView2);
        processtext.setText("Answered");
        listenButton = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.textView);
        resultTextView.setText(error.toString());
    }
    @Override
    public void onListeningStarted() {
        processtext=(TextView)findViewById(R.id.textView2);
        processtext.setText("Listening");
    }

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {
        processtext=(TextView)findViewById(R.id.textView2);
        processtext.setText("Processing");
    }

    @Override
    public void onAudioLevel(final float level) {}




}
