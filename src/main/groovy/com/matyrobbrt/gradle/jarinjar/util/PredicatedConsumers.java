/*
 * Copyright (c) 2022 Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.gradle.jarinjar.util;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicatedConsumers<T, R> implements Function<T, Consumer<R>> {
    private final Set<PredicateData<T, R>> datas = new HashSet<>();

    @Override
    public Consumer<R> apply(T t) {
        Consumer<R> cons = e -> {};
        for (final PredicateData<T, R> data : this.datas) {
            if (data.predicate().test(t)) {
                cons = cons.andThen(e -> data.consumer.accept(t, e));
            }
        }
        return cons;
    }

    public void add(Predicate<T> predicate, BiConsumer<T, R> consumer) {
        this.datas.add(new PredicateData<>(predicate, consumer));
    }

    private static final class PredicateData<T, R> {
        private final Predicate<T> predicate;
        private final BiConsumer<T, R> consumer;

        private PredicateData(Predicate<T> predicate, BiConsumer<T, R> consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }

        public Predicate<T> predicate() {
            return predicate;
        }

        public BiConsumer<T, R> consumer() {
            return consumer;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            PredicateData that = (PredicateData) obj;
            return Objects.equals(this.predicate, that.predicate) &&
                    Objects.equals(this.consumer, that.consumer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(predicate, consumer);
        }

        @Override
        public String toString() {
            return "PredicateData[" +
                    "predicate=" + predicate + ", " +
                    "consumer=" + consumer + ']';
        }
    }
}
