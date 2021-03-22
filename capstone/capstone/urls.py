"""capstone URL Configuration


The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""

from restapi.views import*
from django.conf.urls import url
from django.contrib import admin

urlpatterns = [
	
   # urls for the REST services: 
    url(r'^menu/',menu_list2),
    url(r'^order_item/',order_item),	   
    url(r'^admin/', admin.site.urls),
    url(r'^menus/(\w+)',menu_list),
    url(r'^menu_item/(\w+)',menu_item),
    url(r'^create_order/',new_order),
    url(r'^resturant/(\w+)',get_res_info),
    url(r'^pay/',pay),
   

]

# Use include() to add URLS from the web application 
from django.conf.urls import include

urlpatterns += [
	url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
	url(r'^web/', include('web.urls')),
	url(r'^markdownx/', include('markdownx.urls')),
]

# Add URL maps to redirect the base URL to our application
from django.views.generic import RedirectView
urlpatterns += [
	url(r'^$', RedirectView.as_view(url='/web/', permanent=True)),
]

# Use static() to add url mapping to serve static files during development (only)
from django.conf import settings
from django.conf.urls.static import static

urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)

#Add Django site authentication urls (for login, logout, password management) - Xie 11/6
urlpatterns += [
	url(r'^accounts/', include('django.contrib.auth.urls')),
]

