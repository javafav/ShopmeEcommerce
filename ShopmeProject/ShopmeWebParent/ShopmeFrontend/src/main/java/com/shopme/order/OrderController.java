package com.shopme.order;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.product.Product;

import com.shopme.review.ReviewService;

@Controller
public class OrderController {
	
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc&keywordForm";
	
	@Autowired	private OrderService orderService;

    @Autowired private ReviewService reviewService;
	@Autowired private ControllerHelper controllerHelper;

	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return defaultRedirectURL;
	}
	

	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(Model model, HttpServletRequest request, @PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir,@RequestParam("keywordForm") String orderKeyword) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);

		Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, orderKeyword);
		List<Order> listOrders = page.getContent();

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keywordForm", orderKeyword);
		model.addAttribute("moduleURL", "/orders");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);

		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		model.addAttribute("moduleURL", "/orders");
		return "orders/orders_customer";
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(Model model, @PathVariable(name = "id") Integer id, HttpServletRequest request) {
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Order order = orderService.getOrder(id, customer);
		
		setProductReviewableStatus(customer, order);
		
		
		model.addAttribute("order", order);

		return "orders/order_details_modal";
	}

	private void setProductReviewableStatus(Customer customer, Order order) {
	 Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();
		
		while(iterator.hasNext()) {
			OrderDetail orderDetail = iterator.next();
			Product product = orderDetail.getProduct();
			Integer productId = product.getId();
			
			boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productId);
			product.setReviewedByCustomer(didCustomerReviewProduct);
			
			if (!didCustomerReviewProduct) {
				boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productId);
				product.setCustomerCanReview(canCustomerReviewProduct);
			}
			
		}
   }
}
