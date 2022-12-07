import 'package:flutter_app_tv/model/genre.dart';
import 'package:flutter_app_tv/model/source.dart';
import 'package:json_annotation/json_annotation.dart';

class Episode{
  int id ;
  String title ;
  String downloadas ;
  String playas ;
  String description ;
  String duration ;
  String image ;



  List<Source> sources;


  Episode({
    this.id,
    this.title,
    this.downloadas,
    this.playas,
    this.description,

    this.duration,
    this.image,

    this.sources
  });

  factory Episode.fromJson(Map<String, dynamic> parsedJson){



    List<Source> _sources =  [];

    for(Map i in parsedJson ['sources']){
      Source source = Source.fromJson(i);
      if(source.kind != "download")
        _sources.add(source);
    }
    return Episode(
        id: parsedJson['id'],
        title : parsedJson['title'],
        downloadas : parsedJson['downloadas'],
        playas : parsedJson['playas'],
        description :(parsedJson['description'] == null)? "": parsedJson['description'],
        duration  : parsedJson['duration'],
        image  : parsedJson['image'],
        sources: _sources,

    );
  }

}