package co.com.crediya.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class PageResponse<T> {
    private final Flux<T> content;
    private final Mono<Long> totalElements;
    private final int pageNumber;
    private final int pageSize;

    public static <T> PageResponse<T> of(Flux<T> content, Mono<Long> totalElements, PageRequest pageRequest) {
        return new PageResponse<>(content, totalElements, pageRequest.pageNumber(), pageRequest.pageSize());
    }

    public <R> PageResponse<R> mapTo(Function<? super T, R> mapper) {
        var mappedContent = content.map(mapper);

        return new PageResponse<>(mappedContent, totalElements, pageNumber, pageSize);
    }
}