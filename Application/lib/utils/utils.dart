import 'package:intl/intl.dart' as intl;

class Utils {
  static String getTime(int time) {
    var date = DateTime(2000);
    var format = intl.DateFormat("HH:mm:ss");
    return format.format(date.add(Duration(milliseconds: time)));
  }
}
