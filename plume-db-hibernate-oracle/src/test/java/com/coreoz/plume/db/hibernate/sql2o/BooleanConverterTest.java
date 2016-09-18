package com.coreoz.plume.db.hibernate.sql2o;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sql2o.Connection;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.db.hibernate.DbHibernateOracleTestModule;
import com.coreoz.plume.db.hibernate.TransactionManagerOracle;

@RunWith(GuiceTestRunner.class)
@GuiceModules(DbHibernateOracleTestModule.class)
public class BooleanConverterTest {

	@Inject
	private TransactionManagerOracle transactionManagerOracle;

	@Test
	public void converted_should_be_used() {
		try(Connection connection = transactionManagerOracle.sql2o().open()) {
			User user = connection
				.createQuery("SELECT ID, NAME, ACTIVE FROM USER")
				.executeAndFetchFirst(User.class);
			assertThat(user.isActive()).isTrue();
		}
	}

}
