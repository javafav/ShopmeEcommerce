



$(document).ready(function() {

	$("#buttonAdd2Cart").on("click", function(evt) {
		addToCart();
		

	});
	addToCartFromCategory();
});

function addToCart() {
	quantity = $("#quantity" + productId).val();
	url = contextPath + "cart/add/" + productId + "/" + quantity;

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showModalDialog("Shopping Cart", response);
		totalCartItem();
	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart.");
	});
}


function addToCartFromCategory() {
	$(".addToCart").on("click",function(e){
		e.preventDefault();
		productURL = $(this).attr("href");
		url = contextPath  + productURL 
	
		$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showModalDialog("Shopping Cart", response);
		
	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart.");
	});
	});
	
}
