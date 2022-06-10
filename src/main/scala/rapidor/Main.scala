package rapidor
import zio.*

object Application extends ZIOAppDefault:
  override def run = {
    val program = (for {
      _ <- ZIO.log("Starting the program")
      _ <- ZIO.log("Stopping the program")
    } yield ExitCode.success)
      .catchAllCause(err =>
        for {
          _ <- ZIO.logError(s"Failed to run program with err: [$err]")
          a <- ZIO.succeed(ExitCode.failure)
        } yield a
      )
      //.provideCustomLayer(Clock.live ++ (Clock.live >>> rapidor.service.KafkaConsumerService.consumer))
      .provideSomeLayer(Clock.live ++ (Clock.live >>> rapidor.service.RapidorKafkaConsumer.consumer))
      //.provide(co.rapidor.QuillContext.dataSourceLayer, co.rapidor.cipher.services.PersonDataServiceLive.layer)
      .exitCode
    program
  }
