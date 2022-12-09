import 'dart:convert';
import 'dart:core';
import 'dart:core';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_app_tv/api/api_rest.dart';
import 'dart:convert' as convert;
import 'package:flutter_app_tv/model/subtitle.dart' as model;
import 'package:image_fade/image_fade.dart';
import 'package:intl/intl.dart' as intl;
import 'package:url_launcher/url_launcher.dart';

import '../../key_code.dart';
import '../../model/subtitle.dart';

class Player {
  static final platform = MethodChannel('VIDEO_PLAYER_CHANNEL');

  static playTrailer(BuildContext context, String url, String title,
      String description) async {
    print("trailer played Url == $url");
    RegExp reg = RegExp(
        r'http(?:s?):\/\/(?:www\.)?youtu(?:be\.com\/watch\?v=|\.be\/)([\w\-\_]*)(&(amp;)?‌​[\w\?‌​=]*)?');

    if (reg.hasMatch(url)) {
      if (await canLaunch(url)) {
        await launch(url);
      } else {
        throw 'Could not launch $url';
      }
    } else {
      Player.openPlayer(context, 0, url, title, description, false, false,
          isTrailer: true);
    }
  }

  static openPlayer(BuildContext context, int id, String url, String title,
      String description, bool liveTV, bool isMovie,
      {bool isTrailer = false}) async {
    if (isTrailer) {
      launchPlayer([], 0, url, title, description, liveTV, isMovie, false,
          isTrailer: true);
      return;
    }
    var result =
        await platform.invokeMethod("getVideoLastTime", {'id': id}) as int;

    var date = DateTime(2000);
    var format = intl.DateFormat("HH:mm:ss");
    var time = format.format(date.add(Duration(milliseconds: result)));

    showProgress(context);
    var subTitleList = await getSubtitlesList(id, isMovie);
    Navigator.pop(context);

    if (result > 0) {
      showResumeDialog(time, context, id, url, title, description, liveTV,
          isMovie, subTitleList);
    } else {
      launchPlayer(
          subTitleList, id, url, title, description, liveTV, isMovie, true);
    }
  }

  static String convertToJson(List<Subtitle> subTitles) {
    List<Map<String, dynamic>> jsonData =
        subTitles.map((word) => word.toMap()).toList();
    return jsonEncode(jsonData);
  }

  static Future<List<model.Subtitle>> getSubtitlesList(
      int id, bool isMovie) async {
    var response;
    List<model.Subtitle> subTitleList = [];

    try {
      if (isMovie)
        response = await apiRest.getSubtitlesByMovie(id);
      else
        response = await apiRest.getSubtitlesByEpisode(id);

      if (response != null) {
        if (response.statusCode == 200) {
          var jsonData = convert.jsonDecode(response.body);
          for (Map language in jsonData) {
            for (Map subtitle in language["subtitles"]) {
              print(subtitle["url"]);
              model.Subtitle _subtitle = model.Subtitle(
                  id: subtitle["id"],
                  type: subtitle["type"],
                  url: subtitle["url"],
                  image: language["image"],
                  language: language["language"]);
              subTitleList.add(_subtitle);
              // subTitleList.add(_subtitle);
              // subTitleList.add(_subtitle);
            }
          }
        }
      }
    } catch (e) {}
    print("subtitles = ${subTitleList.map((e) => print(e.language))}");
    return subTitleList;
  }

  static void showProgress(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => WillPopScope(
        onWillPop: () => Future.value(false),
        child: Center(
          child: Container(
            width: 100.0,
            height: 100.0,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(4.0),
            ),
            child: Padding(
              padding: const EdgeInsets.all(12.0),
              // child: CupertinoActivityIndicator(),
              child: CircularProgressIndicator(),
            ),
          ),
        ),
      ),
    );
  }

  static void showResumeDialog(
      String time,
      BuildContext context,
      int id,
      String url,
      String title,
      String description,
      bool liveTV,
      bool isMovie,
      List<Subtitle> subTitleList) {
    showDialog(
      context: context,
      builder: (context) => WillPopScope(
        onWillPop: () => Future.value(true),
        // child: Scaffold(
        //   backgroundColor: Colors.white30,
        //   body: Container(
        //     padding: EdgeInsets.symmetric(horizontal: 150),
        //     child: Center(
        //       child: Column(
        //         crossAxisAlignment: CrossAxisAlignment.center,
        //         mainAxisAlignment: MainAxisAlignment.center,
        //         children: [
        //           Text("Moon TV",
        //               style: TextStyle(color: Colors.white, fontSize: 35)),
        //           SizedBox(height: 10,),
        //           Text("Desea reanudar la transmision a $time mins?",
        //               style: TextStyle(color: Colors.white, fontSize: 20)),
        //           SizedBox(
        //             height: 30,
        //           ),
        //           Row(
        //             mainAxisAlignment: MainAxisAlignment.center,
        //             children: [
        //               Expanded(
        //                 child: ElevatedButton(
        //
        //                   onPressed: () {
        //                     Navigator.pop(context);
        //                     launchPlayer(subTitleList, id, url, title,
        //                         description, liveTV, isMovie, true);
        //                   },
        //                   child: Text("Yes"),
        //                   style: ElevatedButton.styleFrom(
        //                     shape: RoundedRectangleBorder(),
        //                     backgroundColor: Colors.black87,
        //                   ),
        //                 ),
        //               ),
        //               SizedBox(
        //                 width: 50,
        //               ),
        //               Expanded(
        //                 child: ElevatedButton(
        //                   onPressed: () {
        //                     Navigator.pop(context);
        //                     launchPlayer(subTitleList, id, url, title,
        //                         description, liveTV, isMovie, false);
        //                   },
        //                   child: Text("No"),
        //                   style: ElevatedButton.styleFrom(
        //                       shape: RoundedRectangleBorder(),
        //                       backgroundColor: Colors.black87),
        //                 ),
        //               )
        //             ],
        //           )
        //         ],
        //       ),
        //     ),
        //   ),
        // ),
        child: ResumeDiaLog(time, subTitleList, id, url, title, description,
            liveTV, isMovie, true),
      ),
    );
  }

  static Future<void> launchPlayer(
      List<Subtitle> subTitleList,
      int id,
      String url,
      String title,
      String description,
      bool liveTV,
      bool isMovie,
      bool resume,
      {bool isTrailer = false}) async {
    print("here ==> ${convertToJson(subTitleList)}");
    var result = await platform.invokeMethod('launchVideoPlayer', {
      'id': id,
      'url': url,
      'title': title,
      'description': description,
      'resume': resume,
      'isTrailer': isTrailer,
      // 'subTitle': subTitleList.length > 0 ? subTitleList?.first?.url : ""
      'subTitle': convertToJson(subTitleList)
    });
    print("hre ==> $result");
  }
}

class ResumeDiaLog extends StatefulWidget {
  String time;
  List<Subtitle> subTitleList;
  int id;
  String url;
  String title;
  String description;
  var liveTV = false;
  var isMovie = false;
  var bool = false;

  ResumeDiaLog(this.time, this.subTitleList, this.id, this.url, this.title,
      this.description, this.liveTV, this.isMovie, this.bool);

  @override
  State<ResumeDiaLog> createState() => _ResumeDiaLogState();
}

class _ResumeDiaLogState extends State<ResumeDiaLog> {
  int pos_y = 0;
  FocusNode main_focus_node = FocusNode();

  @override
  void initState() {
    super.initState();
    Future.delayed(Duration.zero, () {
      FocusScope.of(context).requestFocus(main_focus_node);
    });
  }

  @override
  Widget build(BuildContext context) {
    return RawKeyboardListener(
      focusNode: main_focus_node,
      onKey: (RawKeyEvent event) {
        if (event is RawKeyDownEvent && event.data is RawKeyEventDataAndroid) {
          RawKeyDownEvent rawKeyDownEvent = event;
          RawKeyEventDataAndroid rawKeyEventDataAndroid = rawKeyDownEvent.data;
          print("Focus Node 0 ${rawKeyEventDataAndroid.keyCode}");
          switch (rawKeyEventDataAndroid.keyCode) {
            case KEY_CENTER:
              Navigator.pop(context);
              Player.launchPlayer(
                  widget.subTitleList,
                  widget.id,
                  widget.url,
                  widget.title,
                  widget.description,
                  widget.liveTV,
                  widget.isMovie,
                  pos_y == 0);
              break;

            case KEY_LEFT:
              if (pos_y == 0) {
                print("play sound");
              } else {
                pos_y--;
              }
              if (pos_y == 0) {
                FocusScope.of(context).requestFocus(null);
                FocusScope.of(context).requestFocus(main_focus_node);
              }
              break;

              break;
            case KEY_RIGHT:
              if (pos_y == 1) {
                print("play sound");
              } else {
                pos_y++;
              }
              break;
            default:
              break;
          }
          setState(() {});
        }
      },
      child: Scaffold(
        backgroundColor: Colors.white30,
        body: Container(
          padding: EdgeInsets.symmetric(horizontal: 150),
          child: Center(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text("Moon TV",
                    style: TextStyle(color: Colors.white, fontSize: 35)),
                SizedBox(
                  height: 10,
                ),
                Text("Reanudar Donde Se Quedo?",
                    // Text("Desea reanudar la transmision a ${widget.time} mins?",
                    style: TextStyle(color: Colors.white, fontSize: 20)),
                SizedBox(
                  height: 30,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Expanded(
                      child: ElevatedButton(
                        onPressed: () {
                          Navigator.pop(context);
                          // launchPlayer(subTitleList, id, url, title,
                          //     description, liveTV, isMovie, true);
                        },
                        child: Text("Si"),
                        style: ElevatedButton.styleFrom(
                            shape: RoundedRectangleBorder(
                                borderRadius:
                                    BorderRadius.all(Radius.circular(0.0)),
                                side: BorderSide(
                                    color: pos_y == 0
                                        ? Colors.grey
                                        : Colors.transparent,
                                    width: 2)),
                            backgroundColor: Colors.black),
                      ),
                    ),
                    SizedBox(
                      width: 50,
                    ),
                    Expanded(
                      child: ElevatedButton(
                        onPressed: () {
                          Navigator.pop(context);
                          // launchPlayer(subTitleList, id, url, title,
                          //     description, liveTV, isMovie, false);
                        },
                        child: Text("No"),
                        style: ElevatedButton.styleFrom(
                            shape: RoundedRectangleBorder(
                                borderRadius:
                                    BorderRadius.all(Radius.circular(0.0)),
                                side: BorderSide(
                                  color: pos_y == 1
                                      ? Colors.grey
                                      : Colors.transparent,
                                  width: 2,
                                )),
                            backgroundColor: Colors.red),
                      ),
                    )
                  ],
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
