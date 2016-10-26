package com.barayuda.swiperefreshandloadmore;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private LoadMoreListView lvItem;
    private SwipeRefreshLayout swipeMain;
    private LinkedList<String> list;
    private ArrayAdapter<String> adapter;
    private int MaxPage = 5;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItem = (LoadMoreListView)findViewById(R.id.lv_item);
        swipeMain = (SwipeRefreshLayout)findViewById(R.id.swipe_main);
        list = new LinkedList<>();

        populateDefaultData();

        //listener untuk loadmore
        lvItem.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (currentPage < MaxPage) {
                    new FakeLoadmoreAsync().execute();
                } else {
                    lvItem.onLoadMoreComplete();
                }
            }
        });

        swipeMain.setOnRefreshListener(this);
    }

    private void populateDefaultData(){
        String[] androidVersion = new String[] {
                "Android Alpha", "Android Beta", "Cupcake", "Donut",
                "Enclair", "Froyo", "Gingerbread", "Honeycomb",
                "Ice Cream Sandwich", "Jelly Bean", "KitKat",
                "Lollipop", "Marshmallow", "Nougat"
        };

        for (int i = 0; i < androidVersion.length; i++){
            list.add(androidVersion[i]);
        }

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
                android.R.id.text1, list);
        lvItem.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        new FakePullRefreshAsync().execute();
    }

    //Background proses palsu untuk melakukan proses delay dan append data di bagian bawah list
    private class FakeLoadmoreAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Thread.sleep(4000);
            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateLoadmoreData();
            currentPage +=1;
        }
    }

    private void populateLoadmoreData() {
        String loadmoreText = "Added on loadmore";
        for (int i = 0; i < 10; i++){
            list.addLast(loadmoreText);
        }
        adapter.notifyDataSetChanged();
        lvItem.onLoadMoreComplete();
        lvItem.setSelection(list.size() - 11);
    }

    //Background proses palsu untuk melakukan proses delay dan append data di bagian atas list
    private class FakePullRefreshAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Thread.sleep(4000);
            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populatePullRefreshData();
        }
    }

    private void populatePullRefreshData() {
        String swipePullRefreshText = "Added after swipe layout";
        for (int i = 0; i < 5; i++){
            list.addFirst(swipePullRefreshText);
        }
        swipeMain.setRefreshing(false);
        adapter.notifyDataSetChanged();
        lvItem.setSelection(0);

    }

    /**
     * NOTE:
     * Cara kerja dari code diatas sebagai berikut:
     * 1. Data default akan ditampilkan terlebih dahulu
     * 2. Jika user scroll ke bawah dan ListView sudah mencapai bagian paling bawah dari batas
     * layar maka fungsi loadMore() akan dijalankan. Dan setelah data ditambahkan dibagian paling
     * bawah dari list maka adapter akan direfresh dengan menggunakan perintah: notifyDataSetChanged()
     * 3. Jika user menarik listview ketika ada di posisi paling atas maka fungsi Pull To Refresh
     * akan dijalankan. Sama seperti dengan load more namun bedanya Pull To Refresh akan menambahkan
     * data dibagian atas dari list dan kemudian adapter akan direfresh dengan menggunakan
     * perintah: notifyDataSetChanged()
     */
}
