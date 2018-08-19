package com.lambton.madt.rockpaperscissors.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.models.Result;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chitrang on 28/01/18.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<Result> resultArrayList;


	public ResultAdapter(Context context, ArrayList<Result> bluetoothDeviceArrayList) {
		mContext = context;
		resultArrayList = bluetoothDeviceArrayList;
	}

	@Override
	public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(mContext).inflate(R.layout.cell_result, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ResultAdapter.ViewHolder holder, final int position) {
		holder.txtUserId.setText(resultArrayList.get(position).userId);
		holder.txtOption.setText(resultArrayList.get(position).strOption);
		holder.txtStatus.setText(resultArrayList.get(position).getStatus());
	}

	@Override
	public int getItemCount() {
		return resultArrayList != null ? resultArrayList.size() : 0;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txtUserId)
		public TextView txtUserId;

		@BindView(R.id.txtOption)
		public TextView txtOption;

		@BindView(R.id.txtStatus)
		public TextView txtStatus;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}