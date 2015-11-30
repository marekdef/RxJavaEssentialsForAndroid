package com.tomtom.rxjava;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import rx.Observable.Operator;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OperatorMerge2InOne<T> implements Operator<List<T>, List<T>> {

    private static final String TAG = OperatorMerge2InOne.class.getSimpleName();

    public OperatorMerge2InOne() {
    }

    @Override
    public Subscriber<? super List<T>> call(Subscriber<? super List<T>> subscriber) {
        return new Subscriber<List<T>>(subscriber) {
            final List<T> list = new ArrayList<T>();
            boolean needsClean = true;
            @Override
            public void onNext(List<T> t) {
                Log.d(TAG, "OperatorMerge2InOne.onNext " + t);
                if (subscriber.isUnsubscribed())
                    return;
                if (needsClean)
                    list.clear();
                list.addAll(t);
                Log.d(TAG, "OperatorMerge2InOne.emitting " + list);
                subscriber.onNext(new ArrayList<T>(list));
                needsClean = !needsClean;
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "OperatorMerge2InOne.e " + e);
                if (subscriber.isUnsubscribed())
                    return;
                needsClean = true;
                Log.d(TAG, "OperatorMerge2InOne.emitting " + e);
                subscriber.onError(e);
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "OperatorMerge2InOne.onCompleted ");
                if (subscriber.isUnsubscribed())
                    return;
                Log.d(TAG, "OperatorMerge2InOne.onCompleted emitting");
                subscriber.onCompleted();
            }
        };
    }
}