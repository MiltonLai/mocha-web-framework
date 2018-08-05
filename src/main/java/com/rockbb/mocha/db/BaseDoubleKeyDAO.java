package com.rockbb.mocha.db;

import com.rockbb.mocha.commons.Pager;

import java.util.List;
import java.util.Map;

public interface BaseDoubleKeyDAO<R, S, T> {

    int insert(T entity);

    int update(T entity);

    int delete(R id1, S id2);

    T select(R id1, S id2);

    List<T> list(Pager pager, Map<String, Object> args);

    long count(Map<String, Object> args);
}
