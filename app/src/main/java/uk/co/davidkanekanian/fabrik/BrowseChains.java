package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BrowseChains extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private BrowseChainsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_chains);

        recyclerView = findViewById(R.id.chain_list_recycler);
        layoutManager = new LinearLayoutManager(this);
        adapter = new BrowseChainsAdapter();
        adapter.managerActivity = this;

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get data from database and make recycler list.
        adapter.reload();
    }

    /** Called when a row is clicked to open that chain. */
    public void openChain(int chainId) {
        // Should I just 'getIntent' or make a new Intent?
        Intent intent = getIntent();
        intent.putExtra("chainId", chainId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}