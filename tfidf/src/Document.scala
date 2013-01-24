
class Document(tokens: Array[String]) {
  private var map = Map[String, Int]()

  for (token <- tokens) {
    increment(token)
  }

  private def increment(token: String) {
    map += token -> map.getOrElse(token, 0)
  }

  def contains(token: String): Boolean = {
    map.contains(token)
  }

  def tf(token: String): Float = {
    map.getOrElse(token, 0).toFloat / token.length
  }

  def tfidf(documents: Seq[Document]): Iterable[(String, Double)] = {
    for (token <- map.keys) yield (token, tf(token) * idf(token, documents))
  }

}


def idf(token: String, documents: Seq[Document]): Double = {
  scala.math.log10(documents.length / documents.count((document: Document) =>
    document.contains(token)))
}