package com.github.gr3gdev.sso.service

import com.github.gr3gdev.sso.dao.AbstractDAO
import java.util.*

interface IService<H, D> {

    fun getDAO(): AbstractDAO<D>

    fun map(dao: D): H

    fun findByName(name: String): Optional<H> {
        var res = Optional.empty<H>()
        getDAO().findByName(name).ifPresent {
            res = Optional.of(map(it!!))
        }
        return res
    }

    fun findAll(): Set<H> {
        return getDAO().findAll().map {
            map(it)
        }.toSet()
    }

}