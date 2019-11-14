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
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val operators=MathOperators.operators++LogicOperators.operators++BasicOperators.operators
  val initialCtx=Map("dialog"->"start","turn"->"1")
  val s1=Scenario("pierwszy scenariusz",Scenario.readScenarioFile("scenario.txt"),operators,initialCtx)
  val gs=GameServer(List(s1))
  val gameId=gs.startGame(0)
  val p1=gs.getGame(gameId)
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
  {
    println("REQUEST:",request)
    println(p1)
    Ok(views.html.index())
  }
  }
}
