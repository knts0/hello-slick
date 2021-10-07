import slick.basic.{DatabaseConfig, StaticDatabaseConfig}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.jdbc.JdbcProfile

@StaticDatabaseConfig("file:src/main/resources/application.conf#testdb")
object TypedSQL
  extends App {
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("testdb")
  val db = dbConfig.db

  import slick.jdbc.MySQLProfile.api._

  def getUsers(id: Int): DBIO[Seq[(Int, String, Int)]] =
    tsql"select id, name, age from users where id > $id"

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
