package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.User;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired private UserRepository repo;
	
	@Test
	public void testFindUserByName() {
		String username = "usmanshj2@gmail.com";
		User findUserByName = repo.getUserByName(username);
		assertThat(findUserByName).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		
		assertThat(countById).isEqualTo(1);
	}
	
	@Test
	public void testUpdateDisabledStatus() {
		Integer id = 16;
		repo.updateEnabledStatus(id, false);
	}
	@Test
	public void testUpdateEnabledStatus() {
		Integer id = 16;
		repo.updateEnabledStatus(id, true);
	}
}
