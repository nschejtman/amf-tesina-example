package pipeline
import pipeline.consumer.Consumer
import pipeline.producer.Producer
import pipeline.transformer.Transformer

case class Pipeline[R](producer: Producer, transformations: Seq[Transformer], consumer: Consumer[R]) {
  def run(): R = {
    val inputModel       = producer.produce()
    val transformedModel = transformations.foldLeft(inputModel)((m, t) => t.transform(m))
    consumer.consume(transformedModel)
  }
}
