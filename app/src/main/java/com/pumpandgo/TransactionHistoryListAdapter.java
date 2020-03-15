package com.pumpandgo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pumpandgo.entities.TransactionHistory;

import java.util.List;

/**
 * Created by David McElhinney on 14/03/2020.
 */

public class TransactionHistoryListAdapter extends RecyclerView.Adapter<TransactionHistoryListAdapter.transactionHistoryViewHolder> {

    // Declaration variables
    private Context context;
    private List<TransactionHistory> transactionHistoryList;

    // Constructor
    public TransactionHistoryListAdapter(Context context, List<TransactionHistory> transactionHistoryList) {
        this.context = context;
        this.transactionHistoryList = transactionHistoryList;
    }

    // Creates a new recycler view.
    @Override
    public transactionHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_transactionhistory, null);
        return new transactionHistoryViewHolder(view);
    }

    // Binds the data to the view.
    @Override
    public void onBindViewHolder(transactionHistoryViewHolder holder, int position) {

        // Gets the Transaction of the specified position.
        TransactionHistory transactionHistory = transactionHistoryList.get(position);

        // Binds the data to the viewholder views.
        holder.textViewFuelStationName.setText(transactionHistory.getFuelStationName());
        holder.textViewTotalCost.setText("€" + transactionHistory.getTotalPrice());
        holder.textViewTransactionDate.setText(transactionHistory.getTransactionDate());
        holder.textViewNumOfLitres.setText(transactionHistory.getNumberOfLitres() + " litres");

        // Pass the transactionId to the Receipt activity and start it.
        holder.cardViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReceiptActivity.class);
                intent.putExtra("TRANSACTION_ID", transactionHistory.getTransactionId());
                context.startActivity(intent);
            }
        });
    }

    // Returns the size of the list.
    @Override
    public int getItemCount() {
        return transactionHistoryList.size();
    }

    class transactionHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView textViewFuelStationName, textViewTotalCost, textViewTransactionDate, textViewNumOfLitres;
        CardView cardViewTransaction;

        public transactionHistoryViewHolder(View transactionHistoryView) {
            super(transactionHistoryView);
            textViewFuelStationName = transactionHistoryView.findViewById(R.id.textViewFuelStationName);
            textViewTotalCost = transactionHistoryView.findViewById(R.id.textViewTotalCost);
            textViewTransactionDate = transactionHistoryView.findViewById(R.id.textViewTransactionDate);
            textViewNumOfLitres = transactionHistoryView.findViewById(R.id.textViewNumOfLitres);
            cardViewTransaction = transactionHistoryView.findViewById(R.id.cardViewTransaction);
        }
    }
}