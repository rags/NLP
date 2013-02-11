import collection.mutable.ListBuffer
import collection.mutable.HashMap
import collection.SortedMap
import io.{BufferedSource, Source}
import java.io.{FileWriter, BufferedWriter}
import util.control.Breaks._

object TFIDFAnalyzer extends App {


  val load1: HashMap[String, HashMap[String, ListBuffer[Double]]] = load
  println(load1)
  save(load1)

  def load:HashMap[String, HashMap[String, ListBuffer[Double]]]= {
    var categories = HashMap[String, HashMap[String, ListBuffer[Double]]](
      "P" -> HashMap[String, ListBuffer[Double]](),
      "U" -> HashMap[String, ListBuffer[Double]](),
      "N" -> HashMap[String, ListBuffer[Double]]())
    assert(categories.contains("P"))
    assert(categories.contains("N"))
    assert(categories.contains("U"))
    val file: BufferedSource = Source.fromFile("tfidf.csv")

    val lines: Iterator[String] = file.getLines()
    for (line <- lines) {
      val parts: Array[String] = line.split(" ")
      val category = parts(0)


      var scoredToken = SortedMap[Double, String]()(Ordering[Double].reverse)
      //println(parts.mkString(","))
      for (i <- 1 to parts.length-1) {
        var tokenParts:Array[String] = parts(i).split("=")
        scoredToken += tokenParts(1).toDouble -> tokenParts(0)
      }
      //println(scoredToken)
      categories.get(category) match {
        case Some(option) => {
          breakable {
            var cnt = 0
            for (key: Double <- scoredToken.keys) {
              cnt += 1
              if (cnt > 10) {
                break
              }
              val token = scoredToken.getOrElse(key, "")
              val scores: ListBuffer[Double] = option.getOrElse(token, new ListBuffer[Double]())
              scores += key
              option += (token -> scores)
            }
          }

          categories += category->option
        }
        case None => sys.error("this shudnt happen : " + category + categories.contains(category).toString + categories.toString())
      }
    }
   categories
  }

  def write(file: BufferedWriter, token: String, str: String) {
    file.write(token)
    file.write(" ")
    file.write(str)
    file.newLine()
  }

  def save(categories:HashMap[String, HashMap[String, ListBuffer[Double]]]) {
    val p: BufferedWriter = new BufferedWriter(new FileWriter("p.csv"))
    val n: BufferedWriter = new BufferedWriter(new FileWriter("n.csv"))
    val u: BufferedWriter = new BufferedWriter(new FileWriter("u.csv"))

    val p10: BufferedWriter = new BufferedWriter(new FileWriter("p10.csv"))
    val n10: BufferedWriter = new BufferedWriter(new FileWriter("n10.csv"))
    val u10: BufferedWriter = new BufferedWriter(new FileWriter("u10.csv"))

    for (key <- categories.keys) {
      val file: BufferedWriter = if (key == "P") p else if (key == "N") n else u
      val file10: BufferedWriter = if (key == "P") p10 else if (key == "N") n10 else u10
      val amp: HashMap[String, ListBuffer[Double]] = categories.getOrElse(key, HashMap[String, ListBuffer[Double]]())
      for((token,scores)<-amp){
        var str:String = ""
        for (score<-scores){
          str += (if (str=="") score.toString else "," + score.toString)
        }
        write(file, token, str)
        if (scores.length>10){
          write(file10, token, str)
        }
      }

    }
    for (f<-Array(p,u,n,p10,u10,n10)){
      f.flush()
      f.close()
    }
  }



}
