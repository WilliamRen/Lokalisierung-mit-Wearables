package project.context.localization.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import project.context.localization.common.helper.PositionsHelper;
import project.context.localization.common.superclasses.MeasureClass;
import project.context.localization.wear.list.WearableAdapter;

/**
 * The MainWatch activity adds the menu items as well as the
 * ClickListeners to start associated activities.
 *
 */
public class MainWatchActivity extends Activity {
    private static final String TAG = MainWatchActivity.class.getSimpleName();

    private MeasureClass mMeasureClass;
    // Handle our Wearable List's click events
    private final WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    int clickedMenu = viewHolder.getLayoutPosition();
                    switch (clickedMenu) {
                        case WearableAdapter.ITEM_LOCALIZATION:
                            startActivity(new Intent(MainWatchActivity.this, WatchLocalizationActivity.class));
                            break;
                        case PositionsHelper.ITEM_SCAN11:
                        case PositionsHelper.ITEM_SCAN12:
                        case PositionsHelper.ITEM_SCAN21:
                        case PositionsHelper.ITEM_SCAN22:
                        case PositionsHelper.ITEM_SCAN31:
                        case PositionsHelper.ITEM_SCAN32:
                        case PositionsHelper.ITEM_SCAN41:
                        case PositionsHelper.ITEM_SCAN42:
                            Intent intent = new Intent(MainWatchActivity.this, ProcessingActivity.class);
                            intent.putExtra("mode", "measure");
                            intent.putExtra("placeId", PositionsHelper.getMenuLabelForPosition(clickedMenu));
                            startActivity(intent);
                            break;

                        case WearableAdapter.ITEM_CALCULATE:
                            intent = new Intent(MainWatchActivity.this, ProcessingActivity.class);
                            intent.putExtra("mode", "average");
                            startActivity(intent);
                            break;
                        case WearableAdapter.ITEM_DELETE:
                            mMeasureClass.deleteAllMeasurements();
                            break;
                        default:
                            Toast.makeText(MainWatchActivity.this,
                                    String.format("You selected item #%s",
                                            viewHolder.getLayoutPosition() + 1),
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

                @Override
                public void onTopEmptyRegionClick() {
                    Toast.makeText(MainWatchActivity.this,
                            "Top empty area tapped", Toast.LENGTH_SHORT).show();
                }
            };
    private TextView mHeader;
    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private final WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onScroll(int i) {}

                @Override
                public void onAbsoluteScrollChange(int i) {
                    if (i >= 0 && i <= 80) {
                        mHeader.setY(-i);
                    } else if (i > 80){
                        mHeader.setY(-80);
                    }
                }

                @Override
                public void onScrollStateChanged(int i) {}

                @Override
                public void onCentralPositionChanged(int i) {}
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ArrayList<Integer> mIcons = new ArrayList<>();
        mIcons.add(R.drawable.ic_red_door);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_station);
        mIcons.add(R.drawable.ic_action_select_all);
        mIcons.add(R.drawable.ic_action_delete);


        // This is our list header
        mHeader = (TextView) findViewById(R.id.header);

        mMeasureClass = new MeasureClass(this) {
            @Override
            public void updateMeasurementsCount() {}
        };

        WearableListView wearableListView =
                (WearableListView) findViewById(R.id.wearable_List);

        WearableAdapter wa = new WearableAdapter(this, mIcons);
        wearableListView.setAdapter(wa);
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
    }


}
