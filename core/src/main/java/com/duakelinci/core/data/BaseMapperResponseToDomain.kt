package com.duakelinci.core.data

interface BaseMapperResponseToDomain<R, D> {
    fun mapResponseToDomain(response: R): D

    fun mapResponsesToListDomain(responses: List<R>): List<D> {
        val listDomain = mutableListOf<D>()
        responses.map { listDomain.add(mapResponseToDomain(it)) }
        return listDomain
    }
}