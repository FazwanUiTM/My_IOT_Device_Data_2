package com.example.my_iot_device_data_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
//    private Context context;
//    private List<DataModel> dataModelList;
//
//    public MyAdapter(Context context) {
//        this.context = context;
//        dataModelList = new ArrayList<>();
//    }
//
//    public void addModel(DataModel dataModel) {
//        dataModelList.add(dataModel);
//        notifyDataSetChanged();
//    }
//
//    public void clear() {
//        dataModelList.clear();
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_row, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.bindViews(dataModelList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataModelList.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        private TextView nameTv, rollTv, stdClassTv;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTv = itemView.findViewById(R.id.TVcolumn1);
//            rollTv = itemView.findViewById(R.id.column2);
//            stdClassTv = itemView.findViewById(R.id.TVhumidity);
//        }
//
//        public void bindViews(DataModel dataModel) {
//            nameTv.setText(dataModel.getName());
//            rollTv.setText(dataModel.getRoll());
//            stdClassTv.setText(dataModel.getStdClass());
//        }
//    }
//}


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    // variable for our array list and context.
    private ArrayList<DataModel> dataModelArrayList;
    private Context context;

    // creating a constructor.
    public MyAdapter(ArrayList<DataModel> dataModelArrayList, Context context) {
        this.dataModelArrayList = dataModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout file on below line.
        View view = LayoutInflater.from(context).inflate(R.layout.data_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // getting data from our array list in our modal class.
        DataModel dataModel = dataModelArrayList.get(position);

        // on the below line we are setting data to our text view.
        holder.column1TV.setText(dataModel.getColumn1());
        holder.column2TV.setText(dataModel.getColumn2());

    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view
        private TextView column1TV, column2TV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initializing our variables.
            column1TV = itemView.findViewById(R.id.TVcolumn1);
            column2TV = itemView.findViewById(R.id.column2);
        }
    }
}