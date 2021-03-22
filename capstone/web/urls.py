from django.conf.urls import url

from . import views

#mapping for web pages
urlpatterns = [
	url(r'^$', views.index, name='index'),
	url(r'^order/$', views.OrderDetail.as_view(), name='order'),
	url(r'^(?P<pk>\d+)/$', views.statusUpdate, name='status'),
	url(r'^payment/$', views.PaymentDetail.as_view(), name='payment'),
]
