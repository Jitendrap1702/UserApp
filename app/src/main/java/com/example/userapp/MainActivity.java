package com.example.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.userapp.adapter.ProductsAdapter;
import com.example.userapp.constants.Constants;
import com.example.userapp.databinding.ActivityMainBinding;
import com.example.userapp.fcmsender.FCMSender;
import com.example.userapp.fcmsender.MessageFormatter;
import com.example.userapp.model.Cart;
import com.example.userapp.model.Inventory;
import com.example.userapp.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Cart cart = new Cart();
    private ProductsAdapter adapter;
    private List<Product> list;
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setup();
        fetchProductsListFromCloudFirestore();

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("data", cart);
                startActivityForResult(intent, 1);
                sendNotification();
            }
        });

    }

    private void sendNotification() {
        String message = MessageFormatter.getSampleMessage("users", "Test2", "Tes2");

        new FCMSender()
                .send(message
                        , new Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Failure")
                                                .setMessage(e.toString())
                                                .show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Success")
                                                .setMessage(response.toString())
                                                .show();
                                    }
                                });

                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Cart newCart = (Cart) data.getSerializableExtra("new");

                cart.changeCart(newCart);

                adapter.notifyDataSetChanged();

                updateCheckOutSummary();
            }
        }

    }

    private void setup() {
        app = (MyApp) getApplicationContext();
    }

    private void fetchProductsListFromCloudFirestore() {

        if (app.isOffline()) {
            app.showToast(MainActivity.this, "No Internet!");
            return;
        }

        app.showLoadingDialog(this);

        app.db.collection(Constants.INVENTORY).document(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(MainActivity.this, "Loading ......\n Fetch data from cloud", Toast.LENGTH_SHORT).show();
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            list = inventory.products;
                        } else {
                            list = new ArrayList<>();
                        }
                        setupList();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Can't load", Toast.LENGTH_SHORT).show();
                        app.hideLoadingDialog();
                    }
                });
    }

    private void setupList() {

        adapter = new ProductsAdapter(this, list, cart);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(itemDecor);


    }

    public void updateCheckOutSummary() {
        if (cart.noOfItems == 0) {
            binding.checkout.setVisibility(View.GONE);
        } else {
            binding.checkout.setVisibility(View.VISIBLE);
            binding.cartSummary.setText("Total: Rs. " + cart.totalPrice + "\n" + cart.noOfItems + " items");
        }
    }

}