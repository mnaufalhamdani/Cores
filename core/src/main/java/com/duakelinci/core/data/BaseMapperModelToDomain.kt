package com.duakelinci.core.data

interface BaseMapperModelToDomain<M, D> {
    fun mapModelToDomain(model: M): D
    fun mapDomainToModel(domain: D): M

    fun mapModelsToListDomain(models: List<M>): List<D> {
        val listDomain = mutableListOf<D>()
        models.map { listDomain.add(mapModelToDomain(it)) }
        return listDomain
    }

    fun mapDomainsToListModel(domains: List<D>): List<M> {
        val listModels = mutableListOf<M>()
        domains.map { listModels.add(mapDomainToModel(it)) }
        return listModels
    }
}