package com.dewire.dehub.view.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dewire.dehub.R;
import com.dewire.dehub.model.entity.GistEntity;
import com.dewire.dehub.view.BaseSupportFragment;
import com.dewire.dehub.view.main.MainPresenter;
import com.dewire.dehub.view.util.ListRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;

/**
 * Created by kl on 28/10/16.
 */

@RequiresPresenter(MainPresenter.class)
public class MainView extends BaseSupportFragment<MainPresenter>
  implements MainContract.View {

  //===----------------------------------------------------------------------===//
  // View contract
  //===----------------------------------------------------------------------===//

  @Override
  public void displayGists(List<GistEntity> gists) {
    adapter.setData(gists);
  }

  //===----------------------------------------------------------------------===//
  // Implementation
  //===----------------------------------------------------------------------===//

  private final Adapter adapter = createAdapter();

  private Adapter createAdapter() {
    Adapter a = new Adapter();
    a.setOnItemClickListener((position, data) -> {
      getPresenter().onActionViewGist(data);
    });
    return a;
  }

  @BindView(R.id.gists_recycler_view) RecyclerView gistsView;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_main_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.new_gist) {
      getPresenter().onActionNewGist();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    gistsView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    gistsView.addItemDecoration(new DividerItemDecoration(gistsView.getContext(),
        LinearLayoutManager.VERTICAL));
    gistsView.setAdapter(adapter);
  }


  private static class ViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView language;

    public ViewHolder(View itemView) {
      super(itemView);
      name = ButterKnife.findById(itemView, R.id.name);
      language = ButterKnife.findById(itemView, R.id.language);
    }
  }

  private static class Adapter extends ListRecyclerAdapter<GistEntity, ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.gist_cell, parent, false);

      return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, GistEntity entity) {
      holder.name.setText(entity.file().getKey());
      holder.language.setText(entity.file().getValue().language());
    }

    @Override
    public boolean isClickable() {
      return true;
    }
  }

}