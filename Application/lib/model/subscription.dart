import 'dart:convert';


class Subscription{
  int id;
  String price;
  String date;
  String expired;
  String state;
  String pack;


  Subscription({this.id, this.price, this.date, this.expired, this.state, this.pack});

  factory Subscription.fromJson(Map<String, dynamic> parsedJson){



    return Subscription(
        id: parsedJson['id'],
        price : parsedJson['price'],
        date : parsedJson['date'],
        expired : parsedJson['expired'],
        state : parsedJson['state'],
        pack : parsedJson['pack']
    );
  }
}