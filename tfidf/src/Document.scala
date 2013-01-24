
class Document(tokens: Array[String]) {
  private var map = Map[String, Int]()

  for (token <- tokens) {
    increment(token)
  }

  private def increment(token: String) {
    map += token -> map.getOrElse(token, 0)
  }

  private def contains(token: String):Boolean= {
    map.contains(token)
  }

  def tf(token: String): Float = {
    map.getOrElse(token,0).toFloat/token.length
  }

  def tfidf(token: String, documents:Seq[Document]):Double ={
    tf(token) * scala.math.log10(documents.length/documents.count((document: Document) => document.contains(token)))
  }

  def tfidf(documents:Seq[Document]):Iterable[(String,Double)]={
      for (token<-map.keys) yield (token,tfidf(token,documents))
  }

}
