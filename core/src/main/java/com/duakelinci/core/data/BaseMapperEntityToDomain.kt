package com.duakelinci.core.data

interface BaseMapperEntityToDomain<E, D> {
    fun mapEntityToDomain(entity: E): D

    fun mapEntitysToListDomain(entitys: List<E>): List<D> {
        val listDomain = mutableListOf<D>()
        entitys.map { listDomain.add(mapEntityToDomain(it)) }
        return listDomain
    }
}