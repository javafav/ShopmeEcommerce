package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listFirstPage() {

		return defaultRedirectURL;
	}
	
	@GetMapping("/users/user_module_info")
	public String userModuleInfo() {

		return "users/user_module_info";
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
         service.listByPage(pageNum, helper);

		return "users/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserExcelExporter exporter = new UserExcelExporter();

		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserPdfExporter exporter = new UserPdfExporter();

		exporter.export(listUsers, response);
	}

	@GetMapping("/users/new")
	public String createNewUser(Model model) {
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("listRoles", service.listRoles());
		model.addAttribute("user", user);
		model.addAttribute("pageTitle", "Create New User");

		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			     service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The User has been saved successfuly!");
		return getURLOfAffectedUser(user);
	}

	private String getURLOfAffectedUser(User user) {
		String emailFirstPart = user.getEmail().split("@")[0];

		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + emailFirstPart;
	}

	@GetMapping("/users/edit/{id}")
	public String updateUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.getUser(id);

			model.addAttribute("listRoles", service.listRoles());
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User with ID( " + id + " )");
			return "users/user_form";

		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			service.deleteUser(id);
			redirectAttributes.addFlashAttribute("message", "The user wih (ID " + id + ")  deleted successfuly!");

			return defaultRedirectURL;
		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

		}

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			service.updateUserEnableStatus(id, status);
			String messageEnabledOrDisabled = ( status == true ? "enabled" : "disabled" );
			redirectAttributes.addFlashAttribute("message",
					"The user wih (ID " + id + ") " + messageEnabledOrDisabled + " successfuly!");
			
			return defaultRedirectURL;
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

		}

	

	}
}
