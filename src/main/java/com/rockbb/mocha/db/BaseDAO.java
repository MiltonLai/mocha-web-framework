package com.rockbb.mocha.db;

import com.rockbb.mocha.commons.Pager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDAO<S, T> {

    int insert(T entity);

    int update(T entity);

    int delete(S id);

    T select(S id);

    List<T> list(Pager pager, Map<String, Object> args);

    long count(Map<String, Object> args);
}
