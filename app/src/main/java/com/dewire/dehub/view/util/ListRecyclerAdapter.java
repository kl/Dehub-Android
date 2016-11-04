package com.dewire.dehub.view.util;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.functions.Action2;

/**
 * Created by kl on 31/10/16.
 */

public abstract class ListRecyclerAdapter<D, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  protected List<D> data;
  private Action2<Integer, D> clickListener;

  public void setOnItemClickListener(Action2<Integer, D> listener) {
    clickListener = listener;
  }

  public void setData(List<D> data) {
    this.data = Collections.unmodifiableList(data);
    notifyDataSetChanged();
  }

  @SuppressWarnings("unused")
  public ListRecyclerAdapter(List<D> data) {
    this.data = Collections.unmodifiableList(data);
  }

  public ListRecyclerAdapter() {
    this.data = Collections.unmodifiableList(new ArrayList<D>());
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  @Override
  @CallSuper
  public void onBindViewHolder(VH holder, int position) {
    D entity = data.get(position);
    setViewClickListener(holder.itemView, position, entity);
    onBindViewHolder(holder, entity);
  }

  private void setViewClickListener(View itemView, int position, D entity) {
    if (isClickable()) {
      itemView.setOnClickListener(l -> {
        if (clickListener != null) {
          clickListener.call(position, entity);
        }
      });
    }
  }

  public void onBindViewHolder(VH holder, D data) {
  }

  public boolean isClickable() {
    return false;
  }
}