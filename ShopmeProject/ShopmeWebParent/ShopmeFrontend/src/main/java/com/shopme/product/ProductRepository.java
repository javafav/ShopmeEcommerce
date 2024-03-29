package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = true"
			+ " AND (p.category.id = ?1 OR p.category.allPaentIds LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listProductByCategory(Integer categoryId, String IdMatch, Pageable pagebale);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND MATCH(name, short_description, full_description) AGAINST (?1 IN NATURAL LANGUAGE MODE)",
		       nativeQuery = true,
		       countQuery = "SELECT COUNT(*) FROM products WHERE enabled = true AND MATCH(name, short_description, full_description) AGAINST (?1 IN NATURAL LANGUAGE MODE)")
		public Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

}
