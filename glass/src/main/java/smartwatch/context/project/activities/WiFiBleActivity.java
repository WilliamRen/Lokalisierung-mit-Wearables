/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smartwatch.context.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

import smartwatch.context.common.superclasses.AverageMeasures;
import smartwatch.context.common.superclasses.Measure;
import smartwatch.context.project.R;
import smartwatch.context.project.card.CardAdapter;

/**
 * Creates a card scroll view with examples of different image layout cards.
 */
public final class WiFiBleActivity extends Activity {

    private static final String TAG = WiFiBleActivity.class.getSimpleName();

    private static final int CARD_LOCALIZATION = 0;
    private static final int CARD_SCAN1 = 1;
    private static final int CARD_SCAN2 = 2;
    private static final int CARD_SCAN3 = 3;
    private static final int CARD_SCAN4 = 4;
    private static final int CARD_SCAN5 = 5;
    private static final int CARD_SCAN6 = 6;
    private static final int CARD_SCAN7 = 7;
    private static final int CARD_SCAN8 = 8;
    private static final int CARD_CALCULATE = 9;
    private static final int CARD_DELETE = 10;

    private CardScrollView mCardScroller;
    private Measure mMeasure;
    private AverageMeasures mAverageMeasures;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardAdapter(createCards(this)));
        setContentView(mCardScroller);
        setCardScrollerListener();

        mMeasure = new Measure(this){
            @Override
            public void updateMeasurementsCount() {}
        };

        mAverageMeasures = new AverageMeasures(this);
    }

    /**
     * Creates list of cards that showcase different type of {@link CardBuilder} API.
     */
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<>();

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_localization_start))
                .setIcon(R.drawable.ic_localization_start)
                .setFootnote(R.string.wifi_ble_localization_footnote));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place1))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place2))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place3))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place4))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place5))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place6))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place7))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_scan_place7))
                .setIcon(R.drawable.ic_station));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_calculate))
                .setIcon(R.drawable.ic_calculate));

        cards.add(new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.wifi_ble_delete_all))
                .setIcon(R.drawable.ic_trash)
                .setFootnote(R.string.wifi_ble_scan_delete_all_footnote));

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
        super.onPause();
    }

    /**
     * Different type of activities can be shown, when tapped on a card.
     */
    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                switch (position) {
                    case CARD_LOCALIZATION:
                        startActivity(new Intent(WiFiBleActivity.this, GlassLocalizationActivity.class));
                        break;

                    case CARD_SCAN1:
                    case CARD_SCAN2:
                    case CARD_SCAN3:
                    case CARD_SCAN4:
                    case CARD_SCAN5:
                    case CARD_SCAN6:
                    case CARD_SCAN7:
                    case CARD_SCAN8:
                        mMeasure.setScanCountMax(5);
                        mMeasure.setPlaceIdString(String.valueOf(position));
                        mMeasure.measureWlan();
                        break;

                    case CARD_CALCULATE:
                        mAverageMeasures.calculateAverageMeasures();
                        break;
                    case CARD_DELETE:
                        soundEffect = Sounds.SUCCESS;
                        mMeasure.deleteAllMeasurements();
                        break;
                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't show anything");
                }

                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

}
