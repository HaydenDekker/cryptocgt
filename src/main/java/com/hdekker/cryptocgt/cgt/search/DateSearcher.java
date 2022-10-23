package com.hdekker.cryptocgt.cgt.search;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface DateSearcher {

	public <T> BiFunction<T, List<T>, Optional<T>> search(Function<T, LocalDateTime> dateGetter);
	
}
