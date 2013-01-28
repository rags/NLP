import opennlp.tools.tokenize.{TokenizerModel, TokenizerME, Tokenizer}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.FunSpec
import scala.util.control.Breaks._

class X extends App{
  Document.tfidf
}
class TdIdfGenerator extends FunSpec {

  def tokenizer: Tokenizer = {
    new TokenizerME(new TokenizerModel(getClass.getResourceAsStream("en-token.bin")))
  }

  describe("tfidf") {
    it("should do foo") {
      val (docCnt,tagCounter) = idf

      println(docCnt)
      println(tagCounter.words.size)
      //for (token <- tagCounter.words) {
       // println(token + " " + tagCounter.count(token).toString)
      //}

    }
  }


  def idf:(Int,TagCounter)= {
    var docCnt = 0
    val tagCounter = new TagCounter()
    val tzer = tokenizer
    val json: _root_.org.json4s.JValue = parse(new StreamInput(getClass.getResourceAsStream("yelp_reviews_3.txt")))

    for (restaurant <- json.children) {
      for (review <- restaurant.children) {
        docCnt += 1

        val text: String = compact(review \ "text").replaceAll("\\.[\r\n]", " ").replaceAll("[\r\n]", " ").toLowerCase()
        val tokens: Set[String] = new Document(tzer.tokenize(text)).words
        for (token <- tokens) {
          tagCounter.increment(token)
        }
      }
    }
    (docCnt,tagCounter)
  }
}
