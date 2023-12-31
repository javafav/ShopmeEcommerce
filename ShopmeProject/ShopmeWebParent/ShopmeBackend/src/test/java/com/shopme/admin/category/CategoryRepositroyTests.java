package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositroyTests {

	@Autowired
	private CategoryRepository repo;

	@Test
	public void testCreateRootCategory() {
		String name = "Memmory";

		Category category = new Category(name);
		Category savedCategory = repo.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);

	}

	@Test
	public void testCreateSubCategory() {
		Category parent = repo.findById(3).get();
		// Category subCategory = new Category("Oppo" ,parent);
		Category subCategory1 = new Category("Gaming Desktops", parent);
		Category savedCategory = repo.save(subCategory1);
		assertThat(savedCategory.getId()).isGreaterThan(0);

	}

	@Test
	public void testGetCategory() {
		Category rootCategory = repo.findById(2).get();

		Set<Category> children = rootCategory.getChildren();
		children.forEach(child -> System.out.println(child.getName()));

	}

	@Test
	public void testListAll() {
		Iterable<Category> listCategories = repo.findAll();
		listCategories.forEach(category -> System.out.println(category.getName()));
	}

	@Test
	public void testPrintHierarchicalCategories() {
		List<Category> categories = (List<Category>) repo.findAll();

		for (Category category : categories) {
			if (category.getParent() == null) {
				System.out.println(category.getName());

				Set<Category> children = category.getChildren();
				for (Category child : children) {
					System.out.println("--" + child.getName());
					printChildren(child, 1);
				}
			}
		}
	}

	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		for (Category child : parent.getChildren()) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(child.getName());
			printChildren(child, newSubLevel);
		}
	}
	
	
	@Test
	public void testFindAllRootCategories() {
		List<Category> listCategories = repo.findAllRootCategories();
		listCategories.forEach(cat -> System.out.println(cat.getName()));
	}
}
