package smartwatch.context.project.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;

import java.util.ArrayList;
import java.util.List;

import smartwatch.context.common.helper.BluetoothData;
import smartwatch.context.common.superclasses.Localization;
import smartwatch.context.project.card.CardAdapter;


public class GlassLocalizationActivity extends Activity {
    private static final String TAG = GlassLocalizationActivity.class.getSimpleName();

    // Index of api demo cards.
    // Visible for testing.
    static final int CARD_STATUS = 0;

    private CardScrollAdapter mAdapter;
    private CardScrollView mCardScroller;
    private CardBuilder mScanCard;
    private Slider mSlider;
    private Slider.Indeterminate mIndeterminate;
    private Localization mLocalization;

    private ServiceConnection mConnection;
    boolean mBound = false;
    private BluetoothData bldata;

    // Visible for testing.
    CardScrollView getScroller() {
        return mCardScroller;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
        mSlider = Slider.from(mCardScroller);
        mIndeterminate = mSlider.startIndeterminate();
        mLocalization = new Localization(this) {
            @Override
            protected void updateLocalizationProgressUI(String foundPlaceId, String waypointDescription) {
                Log.i(TAG, "foundPlaceId: " + foundPlaceId);
                mScanCard.setText(waypointDescription);
                mScanCard.setFootnote("Ort: " + foundPlaceId);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void notifyLocationChange(String priorPlaceId, String foundPlaceId) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.SUCCESS);
            }

            @Override
            protected void showLocalizationProgressOutput() {
            }
        };
        mLocalization.startLocalization();
    }

    /**
     * Create list of API demo cards.
     */
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<>();
        mScanCard = new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Scanne die Umgebung")
                .setFootnote("einen Moment noch...");
        cards.add(CARD_STATUS, mScanCard);
        return cards;
    }

    @Override
    protected void onResume() {
        super.onResume();


        mCardScroller.activate();
    }

    @Override
    protected void onPause() {

        mCardScroller.deactivate();
        mLocalization.stopScanningAndCloseProgressDialog();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
        this.bindService(new Intent(this, BluetoothData.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.DISALLOWED;
                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

    public GlassLocalizationActivity(){
        Log.w(TAG, "Constructor");
        mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                BluetoothData.LocalBinder binder = (BluetoothData.LocalBinder) service;
                bldata = binder.getService();
                mBound = true;
                Toast.makeText(GlassLocalizationActivity.this, "Connected", Toast.LENGTH_SHORT)
                        .show();
            }

            public void onServiceDisconnected(ComponentName className) {
                mBound = false;
            }
        };
    }

}