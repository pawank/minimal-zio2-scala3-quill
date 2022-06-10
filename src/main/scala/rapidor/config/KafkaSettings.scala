package rapidor.config

object KafkaSettings {
	import zio._
	import zio.kafka.consumer._

	val settings: ConsumerSettings = 
	ConsumerSettings(List("kafka1:9092", "kafka2:9092", "kafka3:9092"))
	.withGroupId("groupRapidorEventsQa")
	//.withClientId("clientRapidorEventsQa")
	.withCloseTimeout(30.seconds)
}
