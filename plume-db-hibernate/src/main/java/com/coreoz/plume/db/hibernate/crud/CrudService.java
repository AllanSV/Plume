package com.coreoz.plume.db.hibernate.crud;

import java.util.List;
import java.util.Optional;

public class CrudService<T> {

	private final CrudDaoHibernate<T> crudDao;

	public CrudService(CrudDaoHibernate<T> crudDao) {
		this.crudDao = crudDao;
	}

	public List<T> findAll() {
		return crudDao.findAll();
	}

	public T findById(Long id) {
		if(id == null) {
			return null;
		}
		return crudDao.findById(id);
	}

	public Optional<T> findByIdOptional(Long id) {
		return Optional.ofNullable(crudDao.findById(id));
	}

	public T save(T entityToSave) {
		return crudDao.save(entityToSave);
	}

	public void delete(Long id) {
		crudDao.delete(id);
	}

}
