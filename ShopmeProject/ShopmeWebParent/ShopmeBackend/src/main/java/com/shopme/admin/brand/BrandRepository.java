package com.shopme.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	@Query("SELECT COUNT(b) FROM Brand b WHERE b.id = ?1")
	public Long count(Integer id);
	
	public Brand findByName(String name);
	

	
	@Query("SELECT b FROM Brand b WHERE b.name LIKE %?1% OR b.id LIKE  %?1% ")
	public Page<Brand> findAll(String keyword, Pageable pageable);
}