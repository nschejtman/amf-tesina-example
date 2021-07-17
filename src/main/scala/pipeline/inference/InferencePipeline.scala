package pipeline.inference

import org.apache.jena.rdf.model.Model
import utils.ec.HasExecutionContext

import scala.concurrent.Future

sealed trait InferencePipeline {
  type StepType
  type ResultType

  val steps: Seq[StepType]
  def run(input: Model): ResultType
}

case class SyncInferencePipeline(steps: Seq[SyncInferenceStep]) extends InferencePipeline {
  override type StepType   = SyncInferenceStep
  override type ResultType = Model

  override def run(input: Model): Model = steps.foldLeft(input)((m, step) => step.run(m))
}

case class AsyncPipeline(steps: Seq[AsyncInferenceStep]) extends InferencePipeline with HasExecutionContext {
  override type StepType   = AsyncInferenceStep
  override type ResultType = Future[Model]

  override def run(input: Model): Future[Model] = ???
}
