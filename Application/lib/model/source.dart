class Source{
   int id;
  String type;
  String title;
  String quality;
  String size;
  String kind;
  String premium;
  bool external;
  String url;

  Source({this.id, this.type, this.title, this.quality, this.size, this.kind,
      this.premium, this.external, this.url});

   factory Source.fromJson(Map<String, dynamic> parsedJson){
     return Source(
          id: parsedJson['id'],
          title : parsedJson['title'],
          type : parsedJson['type'],
          quality : parsedJson['quality'],
          size : parsedJson['size'],
          premium : parsedJson['premium'],
          external : parsedJson['external'],
          url : parsedJson['url']
     );
   }

}

