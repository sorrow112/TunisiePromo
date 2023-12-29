package com.example.tunisiepromo_miniproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        View itemView = holder.itemView;
        // Bind data to views
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.priceAfterDiscount.setText(String.valueOf(product.getPrice()-(product.getPrice()*product.getDiscount()/100)));
        holder.ratingBar.setRating(product.getRatingAvg());
        holder.discount.setText(product.getDiscount()+"%");
        holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(product.getEndDate());
        holder.endDateTextView.setText("fini a:"+formattedDate);
        holder.count.setText("("+product.getRatingsCount()+")");
        holder.merchant.setText(product.getMerchant());

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("/promos").child(product.getProductId());
        DatabaseReference myRef = productRef.child("ratingAvg");
        holder.ratingBar.setIsIndicator(false);
        holder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("RatingBar", "Touch event: " + event.getAction());
                return false;
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // holder.ratingBar.setRating(dataSnapshot.getValue(Float.class));
                Float ratingValue = dataSnapshot.getValue(Float.class);
                if (ratingValue != null) {
                    holder.ratingBar.setRating(ratingValue);
                } else {
                    // Handle the case where the rating value is null
                    // For example, set a default rating or show a message to the user
                    holder.ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("rating change", "Failed to read value.", error.toException());
            }
        });
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                List<Rating> newList;
                if(product.getRatings()!=null){
                    newList= product.getRatings();
                }else{
                    newList = new ArrayList<>();
                }
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                int ind = hasUserReviewed(newList,product.getRatingsCount(), uid);
                if(ind==-1){
                    newList.add(new Rating(uid,"",rating,new Date()));
                    product.setRatingsCount(product.getRatingsCount()+1);
                }
                else{
                    newList.set(ind,new Rating(uid,"",rating,new Date()));
                }
                product.setRatings(newList);

                DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference().child("promos").child(product.getProductId());
                promoRef.setValue(product);
            }
        });
        holder.merchant.setClickable(true);
        Context context = itemView.getContext();
        holder.merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the second activity
                Intent intent = new Intent(context, ConsultMerchantActivity.class);
                intent.putExtra("uid",product.getMerchantId());
                // Start the second activity
                context.startActivity(intent);
            }
        });
        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImage);
    }
    private int hasUserReviewed(List<Rating> ratings,int size, String userId) {
        for (int i = 0;i<size;i++) {
            if (ratings.get(i).getUID().equals(userId)) {
                return i;
            }
        }
        return -1; // User hasn't reviewed
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
        TextView count;
        TextView endDateTextView;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productPrice = itemView.findViewById(R.id.textViewInitialPrice);
            productName = itemView.findViewById(R.id.textViewProductName);
            priceAfterDiscount = itemView.findViewById(R.id.textViewProductPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            discount = itemView.findViewById(R.id.discount);
            merchant = itemView.findViewById(R.id.textViewMerchant);
            count = itemView.findViewById(R.id.count);
            endDateTextView = itemView.findViewById(R.id.endDate);
        }

    }
}
