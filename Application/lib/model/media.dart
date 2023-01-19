class Media {
  Media({
      this.currentTime, 
      this.date, 
      this.episodeId, 
      this.mainId,
      this.id,
      this.isMovie, 
      this.poster, 
      this.response, 
      this.subTitle, 
      this.title, 
      this.totalTime, 
      this.url,});

  Media.fromJson(dynamic json) {
    currentTime = json['currentTime'];
    date = json['date'];
    episodeId = json['episodeId'];
    mainId = json['mainId'];
    id = json['id'];
    isMovie = json['isMovie'];
    poster = json['poster'];
    response = json['response'];
    subTitle = json['subTitle'];
    title = json['title'];
    totalTime = json['totalTime'];
    url = json['url'];
  }
  int currentTime;
  int date;
  int episodeId;
  int mainId;
  int id;
  bool isMovie;
  String poster;
  String response;
  String subTitle;
  String title;
  int totalTime;
  String url;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['currentTime'] = currentTime;
    map['date'] = date;
    map['episodeId'] = episodeId;
    map['mainId'] = mainId;
    map['id'] = id;
    map['isMovie'] = isMovie;
    map['poster'] = poster;
    map['response'] = response;
    map['subTitle'] = subTitle;
    map['title'] = title;
    map['totalTime'] = totalTime;
    map['url'] = url;
    return map;
  }

}