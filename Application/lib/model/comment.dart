import 'dart:convert';


class Comment{
  int id;
  String user;
  String image;
  String content;
  String created;
  bool enabled;
  String clear;


  Comment({this.id, this.user, this.image, this.content, this.created, this.enabled,this.clear});

  factory Comment.fromJson(Map<String, dynamic> parsedJson){
    String _clear ="";
    try{
      _clear =  utf8.decode(base64Url.decode(parsedJson['content'].toString().replaceAll("\n", "")));
    }catch(ex){
      print(ex);
    }


    return Comment(
        id: parsedJson['id'],
        user : parsedJson['user'],
        image : parsedJson['image'],
        content : parsedJson['content'],
        created : parsedJson['created'],
        enabled : parsedJson['enabled'],
        clear : _clear
    );
  }
}