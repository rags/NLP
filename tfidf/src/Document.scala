
class Document(tokens: Array[String]) {
  var map = Map[String, Int]()

  for (token <- tokens) {
    increment(token)
  }

  private def increment(token: String) {
    map += token -> map.getOrElse(token, 0)
  }

  private def contains(token: String) {
    map.contains(token)
  }

  def tf(token: String) {
//    map.get(token)/token.length
  }

  //def idf(token:String, )

}
