package rapidor.service

import zio.Clock, zio.Console.printLine
import zio._
import zio.kafka.consumer._
import zio.kafka.serde._

import rapidor.config.KafkaSettings.{settings => consumerSettings}
import java.io.IOException

object RapidorKafkaConsumer {
	val subscription = Subscription.topics("rapidor-events-qa")

	val consumerManaged: ZIO[Scope, Throwable, Consumer] = Consumer.make(consumerSettings)
	val consumer: ZLayer[Clock, Throwable, Consumer] = ZLayer.scoped(consumerManaged)
	val startConsumer = Consumer.consumeWith(consumerSettings, subscription, Serde.string, Serde.string) { case (key, value) =>
		ZIO.log(s"Received message ${key}: ${value}")
	}

	val data: RIO[Clock, Chunk[CommittableRecord[String, String]]] = (Consumer.subscribe(subscription) *> Consumer.plainStream(Serde.string, Serde.string).take(50).runCollect).provideSomeLayer(consumer)
	val consumerSubscriptionAndRun = Consumer.subscribeAnd(subscription).plainStream(Serde.string, Serde.string).tap(cr => printLine(s"key: ${cr.record.key}, value: ${cr.record.value}")).map(_.offset)
	.aggregateAsyncWithin(Consumer.offsetBatches, Schedule.fixed(consumerSettings.pollInterval)).mapZIO(_.commit).runDrain
}
