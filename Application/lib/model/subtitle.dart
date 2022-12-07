import 'dart:convert';

class Subtitle{
  int id;
  String type;
  String language;
  String url;
  String image;

  Subtitle({this.id, this.type,  this.language, this.url,this.image});

  factory Subtitle.fromJson(Map<String, dynamic> parsedJson){
    print(parsedJson);
    return Subtitle(
        id: parsedJson['id'],
        type : parsedJson['type'],
        language : parsedJson['language'],
        url : parsedJson['url'],
        image : parsedJson['image']
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': this.id,
      'type': this.type,
      'language': this.language,
      'url': this.url,
    };
  }

}

