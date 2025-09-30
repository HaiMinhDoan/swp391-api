package com.devmam.swp391api.service;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {

    T create(T entity);

    T update(ID id, T entity);

    void delete(ID id);

    T changeStatus(ID id, Integer status);

    List<T> getAll();

    Optional<T> getOne(ID id);

    boolean exists(ID id);

    long count();

    Page<T> filter(BaseFilterRequest request);
}


