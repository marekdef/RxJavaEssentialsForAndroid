package com.tomtom.rxjava;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.MutableBoolean;

import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import org.junit.Assert;
import org.mockito.Mock;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by defecins on 28/11/15.
 */
public class OperatorMerge2InOneTest {

    private OperatorMerge2InOne<String> stringOperatorMerge2InOne;

    @org.junit.Test
    public void testMergesListProperly() throws Exception {
        stringOperatorMerge2InOne = new OperatorMerge2InOne<>();

        List<String> list1 = Arrays.asList("List1");
        List<String> list2 = Arrays.asList("List2");
        List<String> list3 = Arrays.asList("List3");

        Observable<List<String>> just3 = Observable.just(list1, list2, list3);
        NullPointerException exception = new NullPointerException();
        Observable<List<String>> error = Observable.error(exception);
        Observable<List<String>> just = Observable.merge(just3, error);

//
//        Subscriber<? super List<String>> subscriber = mock(Subscriber.class);


        Iterable<List<String>> lists = just.subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate()).lift(stringOperatorMerge2InOne).toBlocking().toIterable();

        List<String> actual1 = Iterables.get(lists, 0);
        List<String> actual2 = Iterables.get(lists, 1);
        List<String> actual3 = Iterables.get(lists, 2);

        Assert.assertEquals(actual1, list1);
        Assert.assertEquals(actual2, FluentIterable.from(list1).append(list2).toList());
        Assert.assertEquals(actual3, list3);
    }

    @org.junit.Test
    public void testMergesError() {
        List<String> list1 = Arrays.asList("List1");
        List<String> list2 = Arrays.asList("List2");
        List<String> list3 = Arrays.asList("List3");

        Observable<List<String>> just3 = Observable.just(list1, list2, list3);
        NullPointerException exception = new NullPointerException();
        Observable<List<String>> error = Observable.error(exception);
        Observable<List<String>> just = Observable.merge(just3, error);

        Subscriber mock = spy(Subscriber.class);

        just.subscribeOn(Schedulers.immediate()).lift(new OperatorMerge2InOne<>()).subscribe(mock);

        verify(mock).onNext(list1);
        verify(mock).onNext(FluentIterable.from(list1).append(list2).toList());
        verify(mock).onNext(list3);
        verify(mock).onError(exception);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @org.junit.Test
    public void testError() {
        List<String> list1 = Arrays.asList("List1");
        List<String> list2 = Arrays.asList("List2");

        Observable<List<String>> just3 = Observable.just(list1, list2);
        NullPointerException exception = new NullPointerException();
        Observable<List<String>> error = Observable.error(exception);
        Observable<List<String>> just = Observable.merge(just3, error);

        Subscriber mock = spy(Subscriber.class);

        MutableBoolean mb = new MutableBoolean(false);

        just.subscribeOn(Schedulers.immediate()).lift(new OperatorMerge2InOne<>()).doOnError(throwable -> {mb.value = true;}).subscribe(mock);

        verify(mock).onNext(list1);
        verify(mock).onNext(FluentIterable.from(list1).append(list2).toList());
        assertTrue(mb.value);

    }
}