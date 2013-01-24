
class TagCounter{
  private var map = Map[String, Int]()

  def increment(token: String) {
    map += token -> (count(token) + 1)
  }

  def contains(token: String): Boolean = {
    map.contains(token)
  }

  def count(token:String) : Int ={
    map.getOrElse(token,0)
  }

  def words : Set[String] = {
    map.keySet
  }
}

class Document(tokens: Array[String]) {

  val tagCounter = new TagCounter()

  for (token <- tokens) {
    tagCounter.increment(token)
  }

  def contains(token:String): Boolean = tagCounter.contains(token)

  def tf(token: String): Float = {
    tagCounter.count(token).toFloat / token.length
  }

  def tfidf(documents: Seq[Document]): Iterable[(String, Double)] = {
    for (token <- tagCounter.words) yield (token, tf(token) * Document.idf(token, documents))
  }

}

object Document{

def idf(token: String, documents: Seq[Document]): Double = {
  scala.math.log10(documents.length / documents.count((document: Document) =>
    document.contains(token)))
}
}