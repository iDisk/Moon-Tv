import 'dart:ffi';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_app_tv/model/Media.dart';
import 'package:flutter_app_tv/utils/utils.dart';
import 'package:flutter_app_tv/widget/AudioUtil.dart';

class MediaWidget extends StatelessWidget {
  bool isFocus;

  Media channel;

  MediaWidget({this.isFocus, this.channel});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 5.0, vertical: 5),
      child: AnimatedContainer(
        duration: Duration(milliseconds: 150),
        onEnd: () {
          if (isFocus) AudioUtil().playTickSound();
        },
        child: ClipRRect(
            child: Stack(
              fit: StackFit.loose,
              children: [
                CachedNetworkImage(
                  width: double.infinity,
                  imageUrl: channel.poster,
                  errorWidget: (context, url, error) => Icon(Icons.error),
                  fit: BoxFit.cover,
                ),
                Positioned(
                  child: Text(
                    '${Utils.getTime(channel.currentTime)}',
                    style: TextStyle(color: Colors.white),
                  ),
                  bottom: 6,
                  right: 2,
                ),
                Positioned(
                    bottom: 0,
                    width: 136,
                    height: 4,
                    child: LinearProgressIndicator(
                      value: (channel.currentTime) / channel.totalTime,
                      color: Colors.redAccent,
                      backgroundColor: Colors.grey,
                      minHeight: 10,
                    ))
              ],
            ),
            borderRadius: BorderRadius.circular(5)),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(7),
          color: Colors.blueGrey,
          border: (isFocus)
              ? Border.all(color: Colors.purple, width: 2)
              : Border.all(color: Colors.transparent, width: 0),
          boxShadow: [
            BoxShadow(
                color: (isFocus) ? Colors.purple : Colors.white.withOpacity(0),
                offset: Offset(0, 0),
                blurRadius: 6),
          ],
        ),
        width: 136,
      ),
    );
  }


}
