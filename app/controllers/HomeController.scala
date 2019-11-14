package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import site.jans.screept._
import site.jans.game._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  val operators = MathOperators.operators ++ LogicOperators.operators ++ BasicOperators.operators
  val initialCtx = Map("dialog" -> "start", "turn" -> "1")
  val s1 = Scenario(
    "pierwszy scenariusz",
    Scenario.readScenarioFile("scenario.txt"),
    operators,
    initialCtx
  )
  val gs = GameServer(List(s1))
  val gameId = gs.startGame(0)
  val p1 = gs.getGame(gameId)

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    {
      println("REQUEST:", request)
      // println(p1)
      Ok(views.html.index())
    }
  }
  def startGame(scenario_id: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        println("SID", scenario_id)
        val gameId=gs.startGame(scenario_id.toInt)
        Ok("GameId"+gameId)
      }

  }
  def show(gameId: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough=gs.getGame(gameId.toInt)
        Ok("Game"+playthrough.show())
      }

  }
  def playOption(gameId: String,option:String)=Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough=gs.getGame(gameId.toInt)
        playthrough.play(option.toInt)
        Ok("Game"+playthrough.show())
      }
  }

  def game_show(gameId: String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough=gs.getGame(gameId.toInt)
        Ok(views.html.game(playthrough.show(),gameId))
      }

  }
  def game_option(gameId: String,option:String) = Action {
    implicit request: Request[AnyContent] =>
      {
        val playthrough=gs.getGame(gameId.toInt)
        playthrough.play(option.toInt)
        Ok(views.html.game(playthrough.show(),gameId))
      }

  }
}
