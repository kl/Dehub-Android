package com.dewire.dehub.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dewire.dehub.model.entity.CreateGistEntity;
import com.dewire.dehub.model.entity.GistEntity;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;

public class GistApi {

  private final State state;
  private final RetrofitApi api;

  GistApi(@NonNull State state, @NonNull Retrofit retrofit) {
    this.state = state;
    api = retrofit.create(RetrofitApi.class);
  }

  public Observable<Object> login(String username, String password) {
    state.setBasicAuth(username, password);
    return api.getGists().compose(network());
  }

  public Observable<Object> loadGists() {
    return connect(api.getGists(), state.gists);
  }

  /**
   * Creates a new gist.
   *
   * @param gistEntity the gist to create
   * @return an Observable that indicates the success or failure of the post.
   */
  public Observable<Object> postGist(CreateGistEntity gistEntity) {
    return connectElement(api.postGist(gistEntity), state.gists, Orderings.GISTS);
  }

  /**
   * Downloads an URL. The server response content type must be text/plain.
   *
   * @param url gets the URL.
   * @return an Observable String of the URL's bodyText.
   */
  public Observable<String> get(String url) {
    return api.get(url).compose(network());
  }

  //===----------------------------------------------------------------------===//
  // Retrofit interface
  //===----------------------------------------------------------------------===//

  private interface RetrofitApi {

    @GET("gists")
    Observable<ImmutableList<GistEntity>> getGists();

    @POST("gists")
    Observable<GistEntity> postGist(@Body CreateGistEntity gistEntity);

    @GET
    Observable<String> get(@Url String url);
  }

  //===----------------------------------------------------------------------===//
  // Helper methods
  //===----------------------------------------------------------------------===//

  private <T> Observable<Object> connect(Observable<T> observable, BehaviorSubject<T> state) {
    return connectObservable(observable, state, data -> data);
  }

  private <T> Observable<Object> connectElement(Observable<T> observable,
                                              BehaviorSubject<ImmutableList<T>> state,
                                              @Nullable Ordering<T> ordering) {

    return connectObservable(observable, state, data -> {
      if (state.getValue() == null) {
        return ImmutableList.of(data);
      }

      ImmutableList<T> newList = new ImmutableList.Builder<T>()
          .addAll(state.getValue())
          .add(data)
          .build();

      if (ordering != null) {
        return ordering.immutableSortedCopy(newList);
      } else {
        return newList;
      }
    });
  }

  private <T, U> Observable<Object> connectObservable(Observable<T> observable,
                                                    BehaviorSubject<U> state,
                                                    Function<T, U> dataToState) {

    BehaviorSubject<Object> statusSubject = BehaviorSubject.create();

    observable.compose(network()).subscribe(
        data -> {
          state.onNext(dataToState.apply(data));
          statusSubject.onNext(Event.INSTANCE);
        },
        statusSubject::onError,
        statusSubject::onComplete);

    return statusSubject;
  }

  // A helper method to configure common options that should apply for all network
  // request observables.
  private <T> ObservableTransformer<T, T> network() {
    return observable -> observable.observeOn(AndroidSchedulers.mainThread())
        .doOnError(e -> Log.e("GistApi", Throwables.getStackTraceAsString(e)));
  }

  enum Event {
    INSTANCE;
  }
}


