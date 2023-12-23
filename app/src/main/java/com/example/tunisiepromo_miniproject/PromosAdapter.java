package com.example.tunisiepromo_miniproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class PromosAdapter extends RecyclerView.Adapter<PromosAdapter.ProductViewHolder> {

    private List<Promo> promoList;
    private Context context;

    public PromosAdapter(List<Promo> productList, Context context) {
        this.promoList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Promo product = promoList.get(position);

        // Bind data to views
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.priceAfterDiscount.setText(product.getPrice()*product.getDiscount()/100+"%");
        holder.ratingBar.setRating(product.getRating());
        holder.discount.setText(product.getDiscount());
        holder.merchant.setText(product.getMerchant());

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("/products").child(product.getName());
        DatabaseReference myRef = productRef.child("rating");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Toast.makeText(ProductActivity.class, "Rating: " + dataSnapshot, Toast.LENGTH_SHORT).show();
                holder.ratingBar.setRating(dataSnapshot.getValue(Float.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Do something with the new rating
                // For example, display a toast message
                myRef.setValue(rating);
//                Toast.makeText(MainActivity.this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });
        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView priceAfterDiscount;
        TextView discount;
        RatingBar ratingBar;
        TextView merchant;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productPrice = itemView.findViewById(R.id.textViewInitialPrice);
            productName = itemView.findViewById(R.id.textViewProductName);
            priceAfterDiscount = itemView.findViewById(R.id.textViewProductPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            discount = itemView.findViewById(R.id.discount);
            merchant = itemView.findViewById(R.id.textViewMerchant);
        }

    }
}
