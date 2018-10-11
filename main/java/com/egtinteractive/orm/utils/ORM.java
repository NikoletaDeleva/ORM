package com.egtinteractive.orm.utils;

import java.util.List;

public interface ORM extends AutoCloseable {
    public <E> List<E> findAll(final Class<E> clazz);

    public <E> E find(final Class<E> classGen, final String primaryKey);
}
