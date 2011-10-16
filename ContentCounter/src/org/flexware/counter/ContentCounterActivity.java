package org.flexware.counter;

import java.util.HashSet;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ContentCounterActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                new CounterTask(MediaStore.Images.Media._ID, R.id.textView1)
                        .execute(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }

        });
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                new CounterTask(MediaStore.Images.Media._ID, R.id.textView2)
                        .execute(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
            }

        });
        findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                new CounterTask(MediaStore.Images.Media._ID, R.id.textView3)
                        .execute(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
            }

        });
    }

    class CounterTask extends AsyncTask<Uri, Void, Integer> {
        private final String[] projection;
        private final TextView output;
        final String selection = "";
        final String[] selectionArgs = null;

        CounterTask(String idField, int outputId) {
            super();
            projection = new String[] { idField };
            output = (TextView) findViewById(outputId);
        }

        @Override
        protected Integer doInBackground(Uri... args) {
            int count = 0;
            if (args.length == 1) {
                Cursor cursor = getContentResolver().query(args[0], projection, selection, selectionArgs, null);
                count = cursor.getCount();
                cursor.close();
            } else {
                Cursor cursor = getContentResolver().query(args[1], projection, selection, selectionArgs, null);
                HashSet<Integer> firstSet = new HashSet<Integer>();
                int colIndex = cursor.getColumnIndex(projection[0]);
                while (cursor.moveToNext()) {
                    firstSet.add(cursor.getInt(colIndex));
                }
                cursor.close();
                cursor = getContentResolver().query(args[0], projection, selection, selectionArgs, null);
                HashSet<Integer> secondSet = new HashSet<Integer>();
                colIndex = cursor.getColumnIndex(projection[0]);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(colIndex);
                    if (firstSet.contains(id)) {
                        secondSet.add(id);
                    }
                }
                cursor.close();
                count = secondSet.size();
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer result) {
            output.setText(String.valueOf(result));
        }
    }

}