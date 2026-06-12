package com.cloud.runball.basecomm.utils;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * create by libo
 * create on 2020/5/21
 * description RxBus管理类
 * @author ns467
 */
public class RxBus {
    private static volatile RxBus instance;
    private final Subject<Object> BUS;

    private RxBus() {
        BUS = PublishSubject.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void post(Object event) {
        BUS.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return BUS.ofType(eventType);
    }


}
