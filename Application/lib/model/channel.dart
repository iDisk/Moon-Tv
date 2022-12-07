import 'package:flutter_app_tv/model/category.dart';
import 'package:flutter_app_tv/model/country.dart';
import 'package:flutter_app_tv/model/genre.dart';
import 'package:flutter_app_tv/model/source.dart';
import 'package:json_annotation/json_annotation.dart';

class Channel{
  int id ;
  String title ;
  String type ;
  String label ;
  String sublabel ;
  String downloadas ;
  bool comment ;
  String playas ;
  String description ;
  String classification ;
  String duration ;
  double rating ;
  String image ;
  String website ;



  List<Category> categories;
  List<Country> countries;
  List<Source> sources;


  Channel({
    this.id,
    this.title,
    this.type,
    this.label,
    this.sublabel,
    this.downloadas,
    this.comment,
    this.playas,
    this.description,
    this.classification,
    this.duration,
    this.rating,
    this.image,
    this.website,
    this.countries,
    this.categories,
    this.sources
  });

  factory Channel.fromJson(Map<String, dynamic> parsedJson){

    List<Country> countries =  [];

    for(Map i in parsedJson ['countries']){
      Country country = Country.fromJson(i);
      countries.add(country);
    }


    List<Category> categories =  [];

    for(Map i in parsedJson ['categories']){
      Category category = Category.fromJson(i);
      categories.add(category);
    }


    List<Source> sources =  [];

    for(Map i in parsedJson ['sources']){

      Source source = Source.fromJson(i);
      if(source.kind != "download")
        sources.add(source);
    }
    return Channel(
        id: parsedJson['id'],
        title : parsedJson['title'],
        type : parsedJson['type'],
        label : parsedJson['label'],
        sublabel : parsedJson['sublabel'],
        downloadas : parsedJson['downloadas'],
        comment : parsedJson['comment'],
        playas : parsedJson['playas'],
        description :(parsedJson['description'] == null)? "": parsedJson['description'],
        classification : parsedJson['classification'],
        duration  : parsedJson['duration'],
        rating  :(parsedJson['rating'] == null || parsedJson['rating'] == 0 )? 0 : parsedJson['rating']*1.00,
        image  : parsedJson['image'],
        website  : parsedJson['website'],
        sources: sources,
        categories: categories,
        countries: countries,
    );
  }

  String getCategoriesList() {
    String _categories = "";
    for(Category g in categories){
      _categories = _categories + " • "+g.title;
    }
    return _categories;
  }

}