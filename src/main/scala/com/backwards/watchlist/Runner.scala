package com.backwards.watchlist

import scala.language.higherKinds
import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.routes.{HealthRoutes, RoutesProxy, ServiceErrorRoutesProxy, WatchlistRoutes}
import com.backwards.watchlist.service.ServiceError
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter
import com.olegpy.meow.hierarchy._

object Runner extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    println("===> Running...") // TODO Logging
    stream[IO]
  }

  def stream[F[_]: ConcurrentEffect]: F[ExitCode] = {
    implicit val serviceErrorRoutesProxy: RoutesProxy[F, ServiceError] = new ServiceErrorRoutesProxy[F]

    val healthRoutes = HealthRoutes[F]
    val watchlistRoutes = WatchlistRoutes[F](WatchlistServiceInterpreter[F](InMemoryWatchlistRepository[F]))

    val routes: HttpRoutes[F] = healthRoutes <+> watchlistRoutes

    val httpApp = Router("/" -> routes).orNotFound

    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}

/*

val httpService: HttpService[F] = HealthRoutes[F]() <+>

===============================================================

import scala.language.higherKinds
import cats.effect._
import org.http4s._

object HealthRoutes {
  def apply[F[_]: Effect](): HttpService[F] = new HealthRoutes[F].routes
}

class HealthRoutes[F[_]: Effect] extends Routes[F] {
  def routes: HttpService[F] = prefix("healthz") {
    HttpService {
      case GET -> Root =>
        Ok("Well howdy do")
    }
  }
}

================================================

class Boot[F[_]: Effect](client: Client[F])(implicit ec: ExecutionContext) extends StreamApp[F] with HttpServiceOps with HttpClientOps {
  val (host, port) = (http.uri.host.get.value, http.uri.port.get)

  override def stream(args: List[String], requestShutdown: F[Unit]): Stream[F, ExitCode] = {
    BlazeBuilder[F]
      .bindHttp(port, host)
      .mountService(Boot.httpService[F](client))
      .withIdleTimeout(120 seconds)
      .serve
  }
}

object Boot extends HttpServiceOps with HttpClientOps {
  def httpService[F[_]: Effect](client: Client[F])(implicit ec: ExecutionContext): HttpService[F] = {
    implicit val system: ActorSystem = ActorSystem("SapWS")
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val clientWithTracing = client withTrace api.trace

    val gatewayConverter = new GatewayConverter
    val sapConverter = new SapConverter
    val cbsConverter = new CbsConverter

    val wsWorkerSupervisor = system actorOf WSWorkerSupervisor()

    val sapWsClient = new SapWsClient[F](wsWorkerSupervisor)
    val sapClient = new SapClient[F](clientWithTracing)
    val cbsClient = new CbsClient[F](clientWithTracing)

    val identitiesService = new IdentitiesService[F](sapWsClient, gatewayConverter, sapConverter)
    val smartContractsService = new SmartContractsService[F](sapClient, gatewayConverter, sapConverter)
    val assetsService = new AssetsService[F](sapWsClient, cbsClient, gatewayConverter, sapConverter, cbsConverter)

    val httpService: HttpService[F] = HealthRoutes[F]() <+> IdentitiesRoutes[F](identitiesService) <+> ServicesRoutes[F](smartContractsService) <+> AssetsRoutes[F](assetsService)


    ////////////
    println(s"===> ${Commands.name}")
    ////////////

    httpService withTrace api.trace
  }
}

=====================================================

import java.util.concurrent.ForkJoinPool

import cats.effect.IO
import errors.ServiceError._
import errors.ThrowableInstances._
import fs2.StreamApp
import interpreters.{Dependencies, Logger}
import io.circe.generic.auto._
import model.DomainModel._
import monix.execution.Scheduler
import org.http4s.circe._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.{EntityDecoder, EntityEncoder}
import service.PriceService
import model.DomainModelCodecs._

import scala.concurrent.ExecutionContext

object Main extends StreamApp[IO] {

  implicit val futureExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(new ForkJoinPool())

  implicit val monixTaskScheduler: Scheduler =
    Scheduler.global

  /**
    * encoding / decoding
    */
  implicit val priceRequestPayloadDecoder: EntityDecoder[IO, PricesRequestPayload] =
    jsonOf[IO, PricesRequestPayload]

  implicit val priceResponsePayloadEncoder: EntityEncoder[IO, List[Price]] =
    jsonEncoderOf[IO, List[Price]]

  implicit val healthCheckResponsePayloadEncoder: EntityEncoder[IO, ServiceSignature] =
    jsonEncoderOf[IO, ServiceSignature]

  /**
    * server
    */
  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .mountService(HealthCheckHttpApi[IO].service(), "/pricing-api/health-check")
      .mountService(PriceHttpApi[IO].service(priceService), "/pricing-api/prices")
      .enableHttp2(true)
      .serve

  private lazy val priceService: PriceService[IO] =
    PriceService(Dependencies[IO], Logger[IO])
}

 */