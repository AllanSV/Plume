package com.coreoz.plume.db.querydsl.crud;

import java.sql.Connection;

import com.coreoz.plume.db.crud.CrudDao;
import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPath;

public class CrudDaoQuerydsl<T extends CrudEntity> extends QueryDslDao<T> implements CrudDao<T> {

	private final NumberPath<Long> idPath;

	public CrudDaoQuerydsl(TransactionManagerQuerydsl transactionManager,
			RelationalPath<T> table) {
		this(transactionManager, table, null);
	}

	public CrudDaoQuerydsl(TransactionManagerQuerydsl transactionManager,
			RelationalPath<T> table, OrderSpecifier<?> defaultOrder) {
		this(transactionManager, table, defaultOrder, new IdPath(table));
	}

	public CrudDaoQuerydsl(TransactionManagerQuerydsl transactionManager,
			RelationalPath<T> table, OrderSpecifier<?> defaultOrder, NumberPath<Long> idPath) {
		super(transactionManager, table, defaultOrder);
		this.idPath = idPath;
	}

	// API

	@Override
	public T findById(Long id) {
		return selectFrom()
			.where(idPath.eq(id))
			.fetchFirst();
	}

	@Override
	public T save(T entityToUpdate) {
		return transactionManager.executeAndReturn(connection ->
			save(entityToUpdate, connection)
		);
	}

	public T save(T entityToUpdate, Connection connection) {
		if(entityToUpdate.getId() == null) {
			// insert
			entityToUpdate.setId(generateIdentifier());
			transactionManager
				.insert(table, connection)
				.populate(entityToUpdate)
				.execute();
			return entityToUpdate;
		}
		// update
		transactionManager
			.update(table, connection)
			.populate(entityToUpdate)
			.where(idPath.eq(entityToUpdate.getId()))
			.execute();
		return entityToUpdate;
	}

	@Override
	public long delete(Long id) {
		return transactionManager.executeAndReturn(connection ->
			delete(id, connection)
		);
	}

	public long delete(Long id, Connection connection) {
		return transactionManager
			.delete(table, connection)
			.where(idPath.eq(id))
			.execute();
	}

	// dao API

	protected long generateIdentifier() {
		return IdGenerator.generate();
	}

	// internal

	private static class IdPath extends NumberPath<Long> {
		private static final long serialVersionUID = -8749023770318917240L;

		IdPath(RelationalPath<?> base) {
			super(Long.class, base, "id");
		}
	}

}
