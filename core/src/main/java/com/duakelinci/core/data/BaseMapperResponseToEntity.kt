package com.duakelinci.core.data

interface BaseMapperResponseToEntity<R, E> {

    fun mapResponseToEntity(response: R): E

    fun mapResponsesToListEntity(responses: List<R>): List<E> {
        val listEntity = mutableListOf<E>()
        responses.map { listEntity.add(mapResponseToEntity(it)) }
        return listEntity
    }
}