import 'package:assets_audio_player/assets_audio_player.dart';


/// this file helps for playing sound
class AudioUtil {
  final _bellPlayer = AssetsAudioPlayer();
  var _tickPlayer;
  final _bellAudio = Audio("assets/audios/bell_sound.mp3");
  final _tickAudio = Audio("assets/audios/tick_sound.mp3");

  static final AudioUtil _instance = AudioUtil._internal();

  factory AudioUtil() {
    return _instance;
  }

  AudioUtil._internal() {
    _bellPlayer.open(_bellAudio);
  }

  //play tick sound
  playTickSound() {
    if (_tickPlayer == null) {
      _tickPlayer = AssetsAudioPlayer();
      _tickPlayer.open(_tickAudio);
      _tickPlayer.play();
    }
    _tickPlayer.play();
  }

  //play bell sound
  playBellSound() => _bellPlayer.play();
}
