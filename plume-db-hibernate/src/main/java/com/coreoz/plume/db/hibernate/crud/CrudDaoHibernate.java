package com.coreoz.plume.db.hibernate.crud;

import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;

import com.coreoz.plume.db.crud.CrudDao;
import com.coreoz.plume.db.hibernate.TransactionManagerHibernate;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.hibernate.HibernateQuery;

public class CrudDaoHibernate<T> implements CrudDao<T> {

	private final EntityPathBase<T> queryDslEntity;
	private final IdPath idPath;
	protected final TransactionManagerHibernate transactionManager;

	private final OrderSpecifier<?> defaultOrder;

	public CrudDaoHibernate(EntityPathBase<T> queryDslEntity, TransactionManagerHibernate transactionManager) {
		this(queryDslEntity, transactionManager, null);
	}

	public CrudDaoHibernate(EntityPathBase<T> queryDslEntity, TransactionManagerHibernate transactionManager, OrderSpecifier<?> defaultOrder) {
		this.queryDslEntity = queryDslEntity;
		this.transactionManager = transactionManager;
		this.defaultOrder = defaultOrder;

		this.idPath = new IdPath(queryDslEntity);
	}

	// API

	@Override
	public List<T> findAll() {
		return search();
	}

	@Override
	public T findById(Long id) {
		return searchOne(idPath.eq(id));
	}

	@Override
	public T save(T entityToUpdate) {
		return transactionManager.executeAndReturn(em -> save(entityToUpdate, em));
	}

	public T save(T entityToUpdate, EntityManager em) {
		return em.merge(entityToUpdate);
	}

	@Override
	public long delete(Long id) {
		return transactionManager.executeAndReturn(em -> delete(id, em));
	}

	public long delete(Long id, EntityManager em) {
		return transactionManager
			.queryDsl(em)
			.delete(queryDslEntity)
			.where(idPath.eq(id))
			.execute();
	}

	// protected

	protected List<T> search(Predicate... predicates) {
		return withPredicate(
			fetchQuery -> {
				if(defaultOrder != null) {
					fetchQuery.orderBy(defaultOrder);
				}

				return fetchQuery.fetch();
			},
			predicates
		);
	}

	protected long searchCount(Predicate... predicates) {
		return withPredicate(HibernateQuery::fetchCount, predicates);
	}

	protected T searchOne(Predicate... predicates) {
		return withPredicate(HibernateQuery::fetchOne, predicates);
	}

	protected<R> R withPredicate(Function<HibernateQuery<T>, R> toResult, Predicate... predicates) {
		return transactionManager.queryDslExecuteAndReturn(query -> {
			HibernateQuery<T> fetchQuery = query.selectFrom(queryDslEntity);

			if(predicates != null && predicates.length > 0) {
				fetchQuery.where(predicates);
			}

			return toResult.apply(fetchQuery);
		});
	}

	// internal

	private static class IdPath extends NumberPath<Long> {
		private static final long serialVersionUID = -8749023770318917240L;

		IdPath(EntityPathBase<?> base) {
			super(Long.class, base, "id");
		}
	}

}
