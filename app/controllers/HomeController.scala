package controllers

import javax.inject._
import play.api._
import play.api.http._
import play.api.mvc._
import site.jans.screept._
import site.jans.game._
import akka.util._
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  val operators = Screept.getCoreOperators()
  val initialCtx = Map("dialog" -> "start", "turn" -> "1")
  val s1 = Scenario(
    "pierwszy scenariusz",
    Scenario.readScenarioFile("scenario.txt"),
    operators,
    initialCtx
  )
  val s2 = Scenario(
    "London Life",
    Scenario.readScenarioFile("scen2.txt"),
    operators,
    initialCtx
  )
  val gs = GameServer(List(s1, s2))
  val gameId = gs.startGame(0)
  val p1 = gs.getGame(gameId)

  def mapToJSON(m: Map[String, Any]): String = {
    def wrap(x:Any):Any =x match {
      case x: Int  => x
      case x: Boolean  => x
      case x: Seq[Any] => x map wrap mkString("[ ",", "," ]")
      case x: String  => s""" "${x.replaceAll("\n","\\\\n")}" """
      case _ => ""
    }
    val result = (for {
      (k, v) <- m
      val str = s""" "${k}": ${wrap(v)} """
     
    } yield str)
    "{\n" + result.mkString(", ") + "\n}"
  }

  def respondWithJSON(s:String)= {
    Result(ResponseHeader(200,Map.empty[String,String]),HttpEntity.Strict(ByteString(s),Some("application/json")))
  }
  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    {
      println(mapToJSON(initialCtx))
      println(mapToJSON(Map("id" -> 12, "name" -> "Jan")))
      println(mapToJSON(Map("id" -> 12, "name" -> "Jan","jj"->List(1,2,3))))
      println(mapToJSON(Map("id" -> 12, "name" -> "Jan","jj"->List("1",2,true,"3"))))

      println("REQUEST:", request)
      val scenarios = gs.getScenarios()
      val playthroughs = gs.getStartedGames()
      // println(p1)
      Ok(views.html.index(scenarios, playthroughs))
    }
  }
 
  

  def APIstartGame(scenario_id: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        println("SID", scenario_id)
        val gameId = gs.startGame(scenario_id.toInt)
        respondWithJSON(mapToJSON(Map("game_id"->gameId)))
      }

  }
  def APIshow(gameId: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough = gs.getGame(gameId.toInt)
        val response=playthrough.show() match {
          case (head,intro,options)=>Map("head"->head,"intro"->intro,"options"->options)
        }
        respondWithJSON(mapToJSON(response))
      }

  }
  def APIgetGames() = Action {
    implicit request: Request[AnyContent] =>
      {
        respondWithJSON(mapToJSON(gs.getStartedGames().map(pair=>(pair._1+"",pair._2)).toMap))
      }
  }
  def APIgetScenarios() = Action {
    implicit request: Request[AnyContent] =>
      {
        respondWithJSON(mapToJSON(gs.getScenarios().map(pair=>(pair._1+"",pair._2)).toMap))
      }
  }
  def APIplayOption(gameId: String, option: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough = gs.getGame(gameId.toInt)
        playthrough.play(option.toInt)

         val response=playthrough.show() match {
          case (head,intro,options)=>Map("head"->head,"intro"->intro,"options"->options)
        }
        respondWithJSON(mapToJSON(response))
      }
  }

  def game_show(gameId: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough = gs.getGame(gameId.toInt)
        Ok(views.html.game(playthrough.show(), gameId))
      }

  }
  def game_option(gameId: String, option: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough = gs.getGame(gameId.toInt)
        playthrough.play(option.toInt)
        Ok(views.html.game(playthrough.show(), gameId))
      }
  }
  def game(scenario_id: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val gameId = gs.startGame(scenario_id.toInt)
        val playthrough = gs.getGame(gameId)
        Ok(views.html.game(playthrough.show(), gameId + ""))
      }
  }
}
