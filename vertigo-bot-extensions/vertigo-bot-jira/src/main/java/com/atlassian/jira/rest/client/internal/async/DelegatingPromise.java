/*
 * Copyright (C) 2013 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.jira.rest.client.api.RestClientException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.atlassian.util.concurrent.Promise;

/**
 * This class delegates all calls to given delegate Promise. Additionally it throws new RestClientException
 * with original RestClientException given as a cause, which gives a more useful stack trace.
 */
public class DelegatingPromise<T> implements Promise<T> {

    private final Promise<T> delegate;

    public DelegatingPromise(Promise<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T claim() {
        try {
            return delegate.claim();
        } catch (RestClientException e) {
            throw new RestClientException(e);
        }
    }

    @Override
    public Promise<T> done(Consumer<? super T> e) {
        return delegate.done(e);
    }

    @Override
    public Promise<T> fail(Consumer<Throwable> e) {
        return delegate.fail(e);
    }

    @Override
    public Promise<T> then(TryConsumer<? super T> consumer) {
        return delegate.then(consumer);
    }

    @Override
    public <B> Promise<B> map(Function<? super T, ? extends B> function) {
        return delegate.map(function);
    }

    @Override
    public <B> Promise<B> flatMap(Function<? super T, ? extends Promise<? extends B>> function) {
        return delegate.flatMap(function);
    }

    @Override
    public Promise<T> recover(Function<Throwable, ? extends T> handleThrowable) {
        return delegate.recover(handleThrowable);
    }

    @Override
    public <B> Promise<B> fold(Function<Throwable, ? extends B> handleThrowable, Function<? super T, ? extends B> function) {
        return delegate.fold(handleThrowable, function);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
