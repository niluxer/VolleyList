package mx.niluxer.volleyexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.niluxer.volleyexample.adapter.AnswersAdapter;
import mx.niluxer.volleyexample.model.Item;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAnswers;
    private RequestQueue requestQueue;
    private String url = "https://api.stackexchange.com/2.2/answers?order=desc&sort=activity&site=stackoverflow";
    private AnswersAdapter mAdapter;
    private List<Item> itemList = new ArrayList<Item>();
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvAnswers = findViewById(R.id.rvAnswers);
        requestQueue = Volley.newRequestQueue(this);

        mAdapter = new AnswersAdapter(this, new ArrayList<Item>(0), new AnswersAdapter.PostItemListener() {

            @Override
            public void onPostClick(long id) {
                Toast.makeText(MainActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvAnswers.setLayoutManager(layoutManager);
        rvAnswers.setAdapter(mAdapter);
        rvAnswers.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvAnswers.addItemDecoration(itemDecoration);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        loadAnswers();
    }

    private void loadAnswers()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        generateList(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }

    private void generateList(JSONObject jsonObject)
    {
        try{
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray.length() > 0) {
                itemList = Arrays.asList(gson.fromJson(jsonArray.toString(), Item[].class));
            }

            mAdapter.updateAnswers(itemList);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
