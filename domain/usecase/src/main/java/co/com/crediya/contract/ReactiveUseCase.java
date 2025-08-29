package co.com.crediya.contract;

import org.reactivestreams.Publisher;

public interface ReactiveUseCase<I, O extends Publisher<?>> {
    O execute(I input);
}
