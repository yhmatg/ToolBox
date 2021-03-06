package com.android.toolbox.utils;

import com.android.toolbox.core.bean.BaseResponse;
import com.android.toolbox.core.http.exception.ExpiredExpection;
import com.android.toolbox.core.http.exception.NoAssetInCreateInvException;
import com.android.toolbox.core.http.exception.NoUserException;
import com.android.toolbox.core.http.exception.OtherException;
import com.android.toolbox.core.http.exception.ParameterException;
import com.android.toolbox.core.http.exception.ResultIsNullException;
import com.android.toolbox.core.http.exception.TokenException;
import com.android.toolbox.core.http.exception.WrongAccountOrPassException;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chao.qu at 2017/10/20
 *
 * @author yhm
 */

public class RxUtils {

    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return FlowableTransformer
     */
    public static <T> FlowableTransformer<T, T> rxFlSchedulerHelper() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> flowable) {
                return flowable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一线程处理
     *
     * @param <T> 指定的泛型类型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一返回结果处理
     *
     * @param <T> 指定的泛型类型
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleResult() {
        return new ObservableTransformer<BaseResponse<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseResponse<T>> httpResponseObservable) {
                return httpResponseObservable.flatMap(new Function<BaseResponse<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(BaseResponse<T> baseResponse) throws Exception {
                        if (baseResponse.isSuccess() && baseResponse.getResult() != null) {
                            return createData(baseResponse.getResult());
                        } else {
                            if ("2000A0".equals(baseResponse.getCode())) {
                                return Observable.error(new TokenException());
                            }  else if ("909003".equals(baseResponse.getCode())) {//试用过期
                                return Observable.error(new ExpiredExpection());
                            } else if ("200001".equals(baseResponse.getCode())) {//密码账号错误
                                return Observable.error(new WrongAccountOrPassException());
                            }else if ("200002".equals(baseResponse.getCode())) {//请求参数异常
                                return Observable.error(new ParameterException());
                            }  else if ("401704".equals(baseResponse.getCode())) {//新建盘点单中无附条件资产
                                return Observable.error(new NoAssetInCreateInvException());
                            } else if ("301102".equals(baseResponse.getCode())) {//新建盘点单中无附条件资产
                                return Observable.error(new NoUserException());
                            }  else if (baseResponse.getResult() == null) {
                                return Observable.error(new ResultIsNullException());
                            } else {
                                return Observable.error(new OtherException());
                            }
                        }
                    }
                });
            }
        };
    }

    /**
     * 统一返回结果处理(没有result)
     *
     * @return ObservableTransformer
     */
    public static ObservableTransformer<BaseResponse, BaseResponse> handleBaseResponse() {
        return new ObservableTransformer<BaseResponse, BaseResponse>() {
            @Override
            public ObservableSource<BaseResponse> apply(Observable<BaseResponse> upstream) {
                return upstream.flatMap(new Function<BaseResponse, ObservableSource<BaseResponse>>() {
                    @Override
                    public ObservableSource<BaseResponse> apply(BaseResponse baseResponse) throws Exception {
                        if (CommonUtils.isNetworkConnected()) {
                            return createData(baseResponse);
                        } else {
                            return Observable.error(new OtherException());
                        }

                    }
                });
            }
        };
    }

    /**
     * 得到 Observable
     *
     * @param <T> 指定的泛型类型
     * @return Observable
     */
    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


}
