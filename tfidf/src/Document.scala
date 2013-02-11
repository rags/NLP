import collection.mutable.ListBuffer
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonAST.JString
import org.json4s.StreamInput
import java.io._


class TagCounter {
  private var map = Map[String, Int]()

  def increment(token: String) {
    map += token -> (count(token) + 1)
  }

  def contains(token: String): Boolean = {
    map.contains(token)
  }

  def count(token: String): Int = {
    map.getOrElse(token, 0)
  }

  def words: Set[String] = {
    map.keySet
  }
}

class Document(tokens: Array[String]) {

  val tagCounter = new TagCounter()

  for (token <- tokens) {
    tagCounter.increment(token)
  }

  def contains(token: String): Boolean = tagCounter.contains(token)

  def tf(token: String): Float = {
    tagCounter.count(token).toFloat / tokens.length
  }


  def words = tagCounter.words

}

object Document extends App {
  Document.tfidf

  def documents: Seq[(String, Document)] = {
    val docs = ListBuffer[(String, Document)]()
    val json: _root_.org.json4s.JValue = parse(new StreamInput(getClass.getResourceAsStream("yelp_reviews_3.txt")))
    val tzer = new TokenizerME(new TokenizerModel(getClass.getResourceAsStream("en-token.bin")))

    for (restaurant <- json.children) {
      for (review <- restaurant.children) {

        val text: String = (review \ "text").values.toString.replaceAll("\\.[\r\n]", " ").replaceAll("[\r\n]", " ").toLowerCase()
        val sentiment: String = (review \ "sentiment").values.toString
        val document: Document = new Document(tzer.tokenize(text))
        docs += ((sentiment, document))
      }
    }

    docs
  }

  def hello {
    println("sgd")
  }

  def tfidf {
    println("Started tfidf collection")
    val docs = documents
    println("Collected %s docs", docs.size)
    val writer = new BufferedWriter(new FileWriter(new File("tfidf.csv")))
    val idfs: Map[String, Double] = idf(docs)
    println("Done collecting idfs")
    var cnt = 0
    for ((sentiment, doc) <- docs) {
      cnt += 1
      if (cnt % 100 == 0) {
        println("processing doc %s", cnt)
      }
      writer.write(sentiment + " ")
      for (token <- doc.words) {
        writer.write(token.replaceAll("=",""))
        writer.write("=")
        writer.write((doc.tf(token) * idfs.getOrElse(token, 0d)).toString)
        writer.write(" ")
      }
      writer.write("\n")
    }
    writer.flush()
    writer.close()

  }

  def idf(docs: Seq[(String, Document)]): Map[String, Double] = {
    val tagCounter = new TagCounter()

    for ((_, doc) <- docs) {
      for (token <- doc.words) {
        tagCounter.increment(token)
      }
    }
    var map = Map[String, Double]()
    for (token <- tagCounter.words) {
      map += token -> scala.math.log10(docs.size.toFloat / tagCounter.count(token))
    }
    map
  }

}