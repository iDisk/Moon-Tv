import 'package:flutter_app_tv/model/category.dart';
import 'package:flutter_app_tv/model/channel.dart';
import 'package:flutter_app_tv/model/genre.dart';
import 'package:flutter_app_tv/model/poster.dart';

class Slide{
    int id;
    String title;
    String type;
    String image;
    String url;
    Poster poster;
    Category category;
    Genre genre;
    Channel channel;

    Slide({this.id, this.title, this.type, this.image, this.url, this.poster,
      this.category, this.genre, this.channel});

    factory Slide.fromJson(Map<String, dynamic> parsedJson){


      return Slide(
            id: parsedJson['id'],
            title : parsedJson['title'],
            type : parsedJson['type'],
            image : parsedJson['image'],
            url : parsedJson['url'],
            poster : (parsedJson['poster']!= null )?Poster.fromJson(parsedJson['poster']):null,
            genre : (parsedJson['genre']!= null )?Genre.fromJson(parsedJson['genre']):null,
            channel :  (parsedJson['channel']!= null )?Channel.fromJson(parsedJson['channel']):null,
      );
    }
}