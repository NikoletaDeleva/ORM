package com.egtinteractive.orm.utils;

import java.util.List;

public interface Functionality {
    public <E> List<E> findAll(final Class<E> classGen);
    public <E> E find(final Class<E> classGen, String primaryKey);
}
