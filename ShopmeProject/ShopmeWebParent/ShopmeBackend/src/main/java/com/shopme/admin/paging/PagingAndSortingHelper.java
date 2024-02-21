package com.shopme.admin.paging;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.shopme.common.entity.Brand;

public class PagingAndSortingHelper {

	private ModelAndViewContainer model;
	private String listName;
	private String moduelURL;
	private String sortField;
	private String sortDir;
	private String keyword;

	public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String moduelURL, String sortField,
			String sortDir, String keyword) {

		this.model = model;
		this.listName = listName;
		this.moduelURL = moduelURL;
		this.sortField = sortField;
		this.sortDir = sortDir;
		this.keyword = keyword;
	}

	public void updateModelAttributes(int pageNum, Page<?> page) {

		List<?> listItems = page.getContent();
		int pageSize = page.getSize();

		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());

		model.addAttribute(listName, listItems);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
	

	}

	public void listEntities(int pageNum, int pageSize, SearchAndPagingRepository<?, Integer> repo) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
		Page<?> page = null;

		if (keyword != null) {
			page = repo.findAll(keyword, pageable);
		} else {
			page = repo.findAll(pageable);
		}
		updateModelAttributes(pageNum, page);
	}

	public Pageable createPageable(int pageSize, int pageNum) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		return PageRequest.of(pageNum - 1, pageSize, sort);
	}

	public String getSortField() {
		return sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public String getKeyword() {
		return keyword;
	}

}
