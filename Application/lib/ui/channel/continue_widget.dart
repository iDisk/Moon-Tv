import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app_tv/model/Media.dart';
import 'package:flutter_app_tv/ui/channel/channel_detail.dart';
import 'package:flutter_app_tv/ui/channel/media_widget.dart';
import 'package:flutter_app_tv/ui/home/home.dart';
import 'package:flutter_app_tv/model/channel.dart';
import 'package:flutter_app_tv/ui/channel/channel_widget.dart';
import 'package:flutter_app_tv/ui/player/Player.dart';
import 'package:scrollable_positioned_list/scrollable_positioned_list.dart';

class ContinueWidget extends StatefulWidget {
  List<Media> medias = [];

  String title;
  double size;

  int posty;
  int postx;
  int jndex;
  ItemScrollController scrollController;

  ContinueWidget(
      {this.posty,
      this.postx,
      this.jndex,
      this.scrollController,
      this.size,
      this.title,
      this.medias});

  @override
  _ContinueWidgetState createState() => _ContinueWidgetState();
}

class _ContinueWidgetState extends State<ContinueWidget> {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 100,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: EdgeInsets.only(left: 50, bottom: 5),
            height: 20,
            child: Text(
              widget.title,
              style: TextStyle(
                  color: (widget.jndex == widget.posty)
                      ? Colors.white
                      : Colors.white60,
                  fontSize: widget.size,
                  fontWeight: FontWeight.w900),
            ),
          ),
          Container(
            height: 75,
            child: ScrollConfiguration(
              behavior: MyBehavior(),
              // From this behaviour you can change the behaviour
              child: ScrollablePositionedList.builder(
                itemCount: widget.medias.length,
                itemScrollController: widget.scrollController,
                scrollDirection: Axis.horizontal,
                itemBuilder: (context, index) {
                  return Padding(
                    padding: EdgeInsets.only(left: (0 == index) ? 40 : 0),
                    child: GestureDetector(
                        onTap: () {
                          setState(() {
                            widget.posty = widget.jndex;
                            widget.postx = index;
                            Future.delayed(Duration(milliseconds: 250), () {
                              if (widget.medias[index].isMovie) {
                                Player.continuePlaying(
                                    context, widget.medias[index]);
                              }
                              // Navigator.push(
                              //   context,
                              //   PageRouteBuilder(
                              //     pageBuilder: (context, animation1, animation2) => ChannelDetail(channel: widget.channels[index]),
                              //     transitionDuration: Duration(seconds: 0),
                              //   ),
                              // );
                            });
                          });
                        },
                        child: MediaWidget(
                            isFocus: ((widget.posty == widget.jndex &&
                                widget.postx == index)),
                            channel: widget.medias[index])),
                  );
                },
              ),
            ),
          )
        ],
      ),
    );
    ;
  }
}
