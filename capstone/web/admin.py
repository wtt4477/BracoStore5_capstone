from django.contrib import admin

# Register your models here.

from .models import Restaurant, Menu, Menu_Item, Product, Order, Order_Item, Payment, Beacon

# Define the admin class
class ResAdmin(admin.ModelAdmin):
	list_display = ('Res_name','Style', 'Phone', 'Price_range', 'Business_hours', 'Address', 'Default_tips')
	
# Register the admin class with the associated model
admin.site.register(Restaurant, ResAdmin)

# Register the Admin classes using the decorator and display the contents	
@admin.register(Menu)	
class MenuAdmin(admin.ModelAdmin):
	list_display = ('Menu_id', 'Res_id', 'Type')
	list_filter = ('Menu_id',)

	
@admin.register(Menu_Item)	
class MenuItemAdmin(admin.ModelAdmin):
	list_display = ('Menu_id', 'Product_id')
	list_filter = ('Menu_id',)
	
	
@admin.register(Product)	
class ProdAdmin(admin.ModelAdmin):
	list_display = ('Product_id', 'Product_name', 'Price', 'Description')
	
	
@admin.register(Order)	
class OrderAdmin(admin.ModelAdmin):
	list_display = ('Order_id','Res_id', 'Table_id')	

@admin.register(Order_Item)
class OrderItemAdmin(admin.ModelAdmin):
	list_display = ('Order_id', 'Product_id', 'Quantity', 'Status')	

@admin.register(Payment)	
class PaymentAdmin(admin.ModelAdmin):
	list_display = ('Order_id', 'Timestamp', 'Pre_tips', 'Tips', 'Total')	

@admin.register(Beacon)	
class BeaconAdmin(admin.ModelAdmin):
	list_display = ('Beacon_id','Res_id')	
