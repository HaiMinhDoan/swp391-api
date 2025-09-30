package com.devmam.swp391api.models.dto;

public interface IBaseDTO<T,M> {

    T toDTO(M m) throws UnsupportedOperationException;

    M toModel() throws UnsupportedOperationException;
}
