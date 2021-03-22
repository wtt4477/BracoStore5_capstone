from web.models import *
from django.http import JsonResponse
from restapi.serializers import *
from django.views.decorators.csrf import csrf_exempt
from rest_framework.decorators import api_view
from rest_framework import status
from rest_framework.response import Response
import datetime


CONTENT_NOT_FOUND = {'Message': 'Content Not Found'}


# Providing a beacon id, return avalibale menus for this resturant.
@api_view(['GET', 'POST'])
@csrf_exempt
def menu_list2(request):
    if request.method == 'POST':
        beacon_id = request.data['beacon']
        menus = Menu.objects.filter(Res_id_id=Beacon.objects.get(Beacon_id=beacon_id).Res_id)
        now = datetime.datetime.now().hour
        # find the avaliable menus upon the current time
        if now < 11:  # morning time
            ava_menus = menus.exclude(Type='DN').exclude(Type='LN')
        elif now < 16:  # lunch time
            ava_menus = menus.exclude(Type='DN').exclude(Type='BK')
        else:  # dinner time
            ava_menus = menus.exclude(Type='LN').exclude(Type='BK')

        if menus:
            ser = MenuSerializer(ava_menus, many=True)
            return Response(ser.data, status=status.HTTP_200_OK)
        return JsonResponse(CONTENT_NOT_FOUND, safe=False,status=status.HTTP_404_NOT_FOUND)

    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# Record the payment info providing an order id
@api_view(['POST'])
@csrf_exempt
def pay(request):
    if request.method == 'POST':
        # prevent the user double pay an order
        if Payment.objects.filter(Order_id=request.data['Order_id']):
            return Response("You have paid the order already", status=status.HTTP_200_OK)
        # post a payment entry in database
        ser = PaymentSerializer(data=request.data)
        if ser.is_valid():
            ser.save()
            return Response(ser.data, status=status.HTTP_200_OK)
        # the client did not post the valid data
        return Response(ser.errors, status=status.HTTP_400_BAD_REQUEST)

    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# Place an item in the order when a customer adds an item in the shopping cart
@api_view(['POST'])
@csrf_exempt
def order_item(request):
    if request.method == 'POST':
        serializer = OrderItemSerializer(data=request.data)

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# Create a new order
@api_view(['POST'])
@csrf_exempt
def new_order(request):
    if request.method == 'POST':
        serializer = OrderSerializer(data=request.data)

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# Given the resturant id, get the information of this resturant.
def get_res_info(request, str):
    if request.method == 'GET':
        info = Restaurant.objects.get(Res_id=str)

        if info:
            ser = ResSerializer(info)
            return JsonResponse(ser.data, safe=False)
        return JsonResponse(CONTENT_NOT_FOUND, safe=False, status=status.HTTP_404_NOT_FOUND)

    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# Get the product info for a menu
def menu_item(request, str):
    if request.method == 'GET':
        items = Product.objects.filter(menu_item__Menu_id=str)

        if items:
            ser = MenuItemSerializer(items, many=True)
            return JsonResponse(ser.data, safe=False)
        return JsonResponse(CONTENT_NOT_FOUND, safe=False, status=status.HTTP_404_NOT_FOUND) 
    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)


# test code
def menu_list(request, str):
    if request.method == 'GET':
        menus = Menu.objects.filter(Res_id_id=str)
        ser = MenuSerializer(menus, many=True)
        return JsonResponse(ser.data, safe=False)
    return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)
