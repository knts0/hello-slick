import org.h2.engine.Database
import slick.driver.JdbcProfile

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.backend.{DatabaseConfig, StaticDatabaseConfig}

@StaticDatabaseConfig("file:src/main/resources/application.conf#testdb")
object TypedSQL
  extends App {
  val db = Database.forConfig("testdb")
  import dc.driver.api._

  def getUsers(id: Int): DBIO[Seq[(Int, String, String, String, String, String)]] =
    tsql"select id, name, age from users where id > $id"

  val db = dc.db
  try {

    val a: DBIO[Unit] =
      getUsers(1).map { u =>
        println("All users > 1:")
        u.foreach(println)
      }

    val f: Future[Unit] = db.run(a)
    Await.result(f, Duration.Inf)
  } finally db.close
}
