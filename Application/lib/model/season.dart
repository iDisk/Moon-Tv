import 'package:flutter_app_tv/model/episode.dart';

class Season {
  int id;
  String title;

  List<Episode> episodes = [];

  Season({this.id, this.title, this.episodes});

  factory Season.fromJson(Map<String, dynamic> parsedJson) {
    List<Episode> episodes = [];
    if (parsedJson['episodes'] != null)
      for (Map i in parsedJson['episodes']) {
        Episode episode = Episode.fromJson(i);
        episodes.add(episode);
      }

    return Season(
        id: parsedJson['id'], title: parsedJson['title'], episodes: episodes);
  }

  Map<String, dynamic> toMap() {
    return {
      'id': this.id,
      'title': this.title,
      'episodes': this.episodes.map((e) => e.toMap()),
    };
  }
}
