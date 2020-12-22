package nl.jvhaastert.springutils.services

import nl.jvhaastert.springutils.exceptions.EntityNotFoundException
import nl.jvhaastert.springutils.interfaces.Entity
import org.springframework.data.repository.CrudRepository

abstract class EntityService<T, R>(
    protected val repository: R
) where T : Entity, R : CrudRepository<T, Long> {

    open fun getAll(): Iterable<T> {
        return repository.findAll()
    }

    open fun existsById(id: Long): Boolean {
        return repository.existsById(id)
    }

    open fun getById(id: Long): T {
        return repository
            .findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    open fun create(entity: T): T {
        return repository.save(entity)
    }

    open fun update(id: Long, entity: T): T {
        if (!repository.existsById(id)) throw EntityNotFoundException()

        entity.id = id
        return repository.save(entity)
    }

    open fun delete(id: Long) {
        if (!repository.existsById(id)) throw EntityNotFoundException()
        repository.deleteById(id)
    }

}
