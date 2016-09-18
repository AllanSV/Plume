package com.coreoz.plume.db.hibernate.utils;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class HibernateIdGenerator implements IdentifierGenerator {

	public static final String NAME = "plume";

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		return IdGenerator.generate();
	}


}
