package justin.db

import justin.db.StorageNodeActor.{StorageNodeWriteData, StorageNodeWritingResult}

import scala.concurrent.{ExecutionContext, Future}

class RemoteDataSavingService(implicit ec: ExecutionContext) {
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._

  private implicit val timeout = Timeout(3.seconds) // TODO: tune this value

  def apply(storageNodeRefs: List[StorageNodeActorRef], data: Data): Future[List[StorageNodeWritingResult]] = {
    Future.sequence(storageNodeRefs.map(putLocalValue(_, data)))
  }

  private def putLocalValue(node: StorageNodeActorRef, data: Data): Future[StorageNodeWritingResult] = {
    (node.storageNodeActor ? StorageNodeWriteData.Local(data))
      .mapTo[StorageNodeWritingResult]
      .recover { case _ => StorageNodeWritingResult.FailedWrite }
  }
}
