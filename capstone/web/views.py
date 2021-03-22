from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.contrib.auth.mixins import LoginRequiredMixin
from django.views import generic

# Import models
from .models import Beacon, Menu, Menu_Item, Order, Order_Item, Payment, Restaurant, Product


# Create index page
def index(request):
	"""
	View function for home page of site.
	"""
	num_restaurants=Restaurant.objects.all().count()
	
	# Render the HTML template index.html with the data in the context variable
	return render(
		request,
		'index.html',
		context={'num_restaurants':num_restaurants},
    )


# Create order page	
class OrderDetail(LoginRequiredMixin,generic.ListView):
	#model = Order_Item
	paginate_by = 6
	context_object_name = 'Order_Details'   # your own name for the list as a template variable
	def get_queryset(self):
		return Order_Item.objects.select_related('Order_id').filter(Order_id__Res_id=self.request.user.username).filter(Status='CK')
	#queryset = Order_Item.objects.select_related('Order_id').fliter(Order_id.res_id=self.request.user)
	template_name = 'web/order_list.html'
	#def userstr(self):
		#return self.request.user.username		
		

# Update the delivery status
def statusUpdate(request, pk):
	if pk:
		Order_Item.objects.filter(pk=pk).update(Status='Delivered')
		return HttpResponseRedirect('/web/order')

		
# Create Payment Page		
class PaymentDetail(LoginRequiredMixin,generic.ListView):
	paginate_by = 6
	context_object_name = 'Payment_Details'   
	def get_queryset(self):
		return Payment.objects.select_related('Order_id').filter(Order_id__Res_id=self.request.user.username)
	template_name = 'web/payment_list.html'
