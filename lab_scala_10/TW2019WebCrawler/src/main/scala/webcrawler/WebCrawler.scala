package webcrawler

import org.htmlcleaner.TagNode



object WebCrawler extends App {
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL
  import scala.util.{Failure, Success}
  import scala.concurrent.{Await, Future}
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val basicURL = "https://www.java67.com/2017/05/difference-between-var-val-and-def-in-Scala.html"
  val maxDeep : Int = 5;
  //val html = Source.fromURL(url)

  val cleaner = new HtmlCleaner
  val props = cleaner.getProperties

  //val rootNode = cleaner.clean(html.mkString) 
  val rootNode = cleaner.clean(new URL(basicURL))

  val elements = rootNode.getElementsByName("a", true)
  visitLinks(elements, 1)

  def visitLinks(links: Array[TagNode], level: Int) {
    if(level > maxDeep) {
      println("max deep")
      return
    };
    links.map(linkElement => {
      val link = linkElement.getAttributeByName("href")
      if(link != null) {
        var url: String = ""

        if(link.toString.contains("http://") || link.toString.contains("https://")) {
          url = link.toString
        }
        else if(link.toString.length > 2 && link.toString.substring(0, 2).equals("//")) {
          url = "http:" + link.toString
        }
        else {
          url = basicURL + link;
        }

        println(url)

        val furute = Future {
          val html = cleaner.clean(new URL(url));
          val aElements = html.getElementsByName("a", true)
          aElements
        }

        furute.onComplete {
          case Success(value) => visitLinks(value, level + 1)
          case Failure(e) => {;}
        }
      }
      else {
        println("empty link");
      }

    })
  }

  val result = Await.result(Future(0), 100.second)
  Thread.sleep(100000)
}
