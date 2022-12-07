class Actor{
  int id;
  String name;
  String type;
  String role;
  String image;
  String born;
  String height;
  String bio;

  Actor({this.id, this.name, this.type, this.role, this.image, this.born,
    this.height, this.bio});

  factory Actor.fromJson(Map<String, dynamic> parsedJson){
    return Actor(
        id: parsedJson['id'],
        name : parsedJson['name'],
        type : parsedJson['type'],
        role : parsedJson['role'],
        image : parsedJson['image'],
        born : parsedJson['born'],
        height : parsedJson['height'],
        bio : parsedJson['bio']
    );
  }

}

