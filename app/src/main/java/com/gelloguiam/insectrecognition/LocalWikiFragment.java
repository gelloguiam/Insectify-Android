package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class LocalWikiFragment extends Fragment {
    private ImageButton readMore;
    private TextView wiki;
    private TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_wiki, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceType) {
        super.onActivityCreated(savedInstanceType);

        AssetManager assetManager = getActivity().getAssets();
        String wikiFile = "wiki/" + InsectWiki.subject + ".txt";
        String wikiTitle = InsectWiki.subject;
        String contents = "";

        try {
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(assetManager.open(wikiFile)));
            String line;
            while ((line = br.readLine()) != null) {
                contents += line + "\n";
            }
            br.close();
        } catch(Exception e){}

        initializeTTS(contents.toString());

        Typeface bebas = Typeface.createFromAsset(getActivity().getAssets(), "font/bebas.otf");
        Typeface liberation = Typeface.createFromAsset(getActivity().getAssets(), "font/liberation.ttf");

        TextView title = (TextView) getFragmentManager().
                findFragmentById(R.id.wiki_wrapper).
                getView().
                findViewById(R.id.local_wiki_title);

        title.setText(InsectWiki.subject.toUpperCase());
        title.setTypeface(bebas);

        wiki = (TextView) getFragmentManager().
                findFragmentById(R.id.wiki_wrapper).
                getView().
                findViewById(R.id.local_wiki_description);

        wiki.setText(contents.toString());
        wiki.setMovementMethod(new ScrollingMovementMethod());
        wiki.setTypeface(liberation);

        readMore = (ImageButton) getFragmentManager().
                findFragmentById(R.id.wiki_wrapper).
                getView().
                findViewById(R.id.read_more);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsectWiki.openWebWiki();
            }
        });
    }

    protected void initializeTTS(final String text) {
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "Language not supported.");
                    } else {
                        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                    }
                }
                else {
                    Log.e("error", "TTS Initilization failed.");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
