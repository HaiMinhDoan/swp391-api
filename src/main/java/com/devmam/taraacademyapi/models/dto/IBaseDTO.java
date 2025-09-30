package com.devmam.taraacademyapi.models.dto;

public interface IBaseDTO<T,M> {

    T toDTO(M m) throws UnsupportedOperationException;

    M toModel() throws UnsupportedOperationException;
}
