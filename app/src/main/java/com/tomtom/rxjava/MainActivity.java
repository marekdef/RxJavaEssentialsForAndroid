package com.tomtom.rxjava;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.FluentIterable;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.tomtom.rxjava.MyAdapter.CombinedResult;
import com.tomtom.rxjava.search.retrofit.RestAPIs;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.RxActivity;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends RxActivity {

    private static final String DECARTA_KEY = "fddbac04035537ba2758597c363e9690";
    private static final String MAPKIT_KEY = "jv4gfu9dd2vjnuv2w28z2wcz";
    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.editTextQuery)
    TextView editTextQuery;

    @Bind(R.id.listViewResults)
    ListView listViewResults;

    @Bind(R.id.rootLayout)
    RelativeLayout rootLayout;

    private CompositeSubscription compositeSubscription;
    private MyAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        listAdapter = new MyAdapter();
        listViewResults.setAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        compositeSubscription = new CompositeSubscription();

        Subscription subscription =
                getSearchStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                results -> {
                                    Log.d(TAG, "subscribe.onNext");
                                    editTextQuery.setBackgroundDrawable(null);
                                    listAdapter.setResultList(results);
                                },
                                throwable -> {
                                    Log.e(TAG, "subscribe.onError", throwable);
                                },
                                () -> Log.d(TAG, "subscribe.onCompleted")
                        );

        RxAdapterView.itemClicks(listViewResults)
                .observeOn(AndroidSchedulers.mainThread())
                .<Integer>compose(RxLifecycle.bindActivity(lifecycle()))
                .<Integer>subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                Toast.makeText(MainActivity.this, "Clicked " + listAdapter.getItem(position.intValue()), Toast.LENGTH_SHORT).show();
            }
        });

        compositeSubscription.add(subscription);
    }

    @NonNull
    @DebugLog
    private Observable<List<CombinedResult>> getSearchStream() {
        return RxTextView.textChanges(editTextQuery)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .filter((text) -> text.length() > 1)
                .debounce(150, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap(
                        (text) -> Observable.mergeDelayError(decartaSearch(text), mapkitSearch(text))
                                .lift(new OperatorMerge2InOne<>()))
                .doOnError(onError -> {
                    Log.d(TAG, "map.onError", onError);
                    editTextQuery.post(() -> editTextQuery.setBackgroundColor(getResources().getColor(R.color.colorError)));
                    Snackbar snackbar = Snackbar
                            .make(rootLayout, onError.getMessage(), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                })
                .onErrorResumeNext(Observable.create(
                        subscriber -> getSearchStream().subscribe(subscriber)
                ))
                .<List<CombinedResult>>compose(RxLifecycle.bindUntilActivityEvent(lifecycle(), ActivityEvent.PAUSE));
    }

    private Observable<List<CombinedResult>> mapkitSearch(String text) {
        Log.d(TAG, String.format("mapkitSearch(%s)", text));
        return RestAPIs.MapkitSearch.SERVICE
                .geocode(MAPKIT_KEY, text)
                .retryWhen(createExponentialBackOff())
                .doOnEach((onNotification) -> {
                    Log.d(TAG, "mapkitSearch.onNotification " + onNotification.getKind().toString());
                })
                .map((result) -> FluentIterable.from(result.geoResponse.geoResult)
                        .transform((from) -> {
                            return MyAdapter.CombinedResult.mapkit(from, text);
                        })
                        .toList());
    }

    private Observable<List<CombinedResult>> decartaSearch(String text) {
        Log.d(TAG, String.format("decartaSearch(%s)", text));
        return RestAPIs.DecartaSearch.SERVICE
                .search(DECARTA_KEY, text)
                .retryWhen(createExponentialBackOff())
                .map((result) -> FluentIterable.from(result.results).transform((from) -> {
                    return MyAdapter.CombinedResult.decarta(from, text);
                }).toList());
    }

    @NonNull
    private Func1<Observable<? extends Throwable>, Observable<Long>> createExponentialBackOff() {
        return throwables -> throwables.zipWith(Observable.range(0, 3), (throwable, counter) -> {
            return Pair.create(throwable, counter);
        }).flatMap(tuple -> {
            if (tuple.second == 2)
                return Observable.error(tuple.first);
            return Observable.timer((int) Math.pow(2, tuple.second), TimeUnit.SECONDS);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

//        compositeSubscription.clear();
    }
}
