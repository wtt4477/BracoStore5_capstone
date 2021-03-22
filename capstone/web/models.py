from django.db import models
from django.contrib.auth.models import User

# Create your models here.

import uuid # Required for unique restaurant id

class Restaurant(models.Model):
	"""
	Model representing restaurant info
	"""
	#Res_id = models.UUIDField(primary_key=True, default=uuid.uuid4, help_text="Unique ID for restaurant")
	Res_id = models.CharField(primary_key=True, max_length=200, help_text="Unique ID for restaurant")
	Res_name = models.CharField(max_length=200, help_text="Restaurant Name")
	Style = models.CharField(max_length=200)
	Phone = models.CharField(max_length=200)
	Price_range = models.CharField(max_length=10)
	Business_hours = models.CharField(max_length=200)
	Address = models.CharField(max_length=200)
	Default_tips = models.DecimalField(max_digits=3, decimal_places=2, default=0.15)
	Created_time = models.DateTimeField(auto_now_add=True)
	Updated_time = models.DateTimeField(auto_now=True)

	def __str__(self):
		"""
		String for representing the Model object (in Admin site etc.)
		"""
		return self.Res_name

		
class Product(models.Model):
	"""
	Model representing restaurant menu items
	"""
	Product_id = models.CharField(primary_key=True, max_length=200, help_text="Unique ID for Product")
	Product_name = models.CharField(max_length=200)
	Price = models.DecimalField(max_digits=30, decimal_places=20)
	Description = models.TextField(max_length=500)

	def __str__(self):
		"""
		String for representing the Model object (in Admin site etc.)
		"""
		return self.Product_name		

		
class Menu(models.Model):
	"""
	Model representing restaurant menu
	"""
	Menu_id = models.CharField(primary_key=True, max_length=200, help_text="Unique ID for Menu")
	Res_id = models.ForeignKey('Restaurant', on_delete=models.CASCADE)
	
	Menu_type = (
		('BK', 'Breakfast'),
		('BR', 'Brunch'),
		('LN', 'Lunch'),
		('DN', 'Dinner'),
		('AL', 'All Day'),
		('HR', 'Happy Hour'),
	)
	Type = models.CharField(
		max_length=2,
		choices=Menu_type,
		default='AL'
	)
	
	def __str__(self):
		"""
		String for representing the Model object (in Admin site etc.)
		"""
		return '%s %s' % (self.Menu_id, self.Type)
		

class Menu_Item(models.Model):
	"""
	Model representing restaurant menu items
	"""
	Menu_id = models.ForeignKey('Menu', on_delete=models.CASCADE)
	Product_id = models.ForeignKey('Product', on_delete=models.CASCADE)


class Order(models.Model):
	"""
	Model representing Orders info
	"""
	Order_id = models.UUIDField(primary_key=True, default=uuid.uuid4, help_text="Unique ID for Order")
	Res_id = models.ForeignKey('Restaurant', on_delete=models.CASCADE)
	Timestamp = models.DateTimeField(auto_now_add=True)
	Table_id = models.CharField(max_length=10)
	
	class Meta:
		ordering = ["Timestamp"]
		
	def __str__(self):
		"""
		String for representing the Model object (in Admin site etc.)
		"""
		return '%s %s' % (str(self.Order_id), self.Res_id)
		
		
class Order_Item(models.Model):
	"""
	Model representing Orders details
	"""
	Order_id = models.ForeignKey('Order', on_delete=models.CASCADE)
	Product_id = models.ForeignKey('Product', on_delete=models.CASCADE)
	Quantity = models.IntegerField()	
	Status = models.CharField(max_length=10, default='CK')
	
class Payment(models.Model):
	"""
	Model representing payment info
	"""
	Order_id = models.ForeignKey('Order', on_delete=models.CASCADE)
	Timestamp = models.DateTimeField(auto_now_add=True)
	Pre_tips = models.DecimalField(max_digits=30, decimal_places=20)
	Tips = models.DecimalField(max_digits=30, decimal_places=20)
	Total = models.DecimalField(max_digits=30, decimal_places=20)
	

class Beacon(models.Model):
	"""
	Model representing Beacon info
	"""
	Beacon_id = models.UUIDField(primary_key=True, default=uuid.uuid4, help_text="Unique ID for Beacon")
	Res_id = models.ForeignKey('Restaurant', on_delete=models.CASCADE)
