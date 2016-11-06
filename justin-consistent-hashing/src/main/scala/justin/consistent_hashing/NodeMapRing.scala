package justin.consistent_hashing

case class NodeMapRing(private val ring: Map[NodeMapRing.RingPartitionId, NodeId]) {
  import NodeMapRing.RingPartitionId

  def getByPartitionId(id: RingPartitionId): Option[NodeId] = ring.get(id)

  lazy val size: Int = ring.size

  lazy val nodesId: Set[NodeId] = ring.values.toSet

  lazy val swap: Map[NodeId, List[RingPartitionId]] = {
    ring.groupBy { case (_, nodeId) => nodeId }
      .mapValues(_.keys.toList.sorted)
  }
}

object NodeMapRing {
  type RingPartitionId = Int

  def apply(N: Int = 3, S: Int = 50): NodeMapRing = {
    val ring = for {
      id      <- 0 until N
      ringKey <- id until S by N
    } yield (ringKey, NodeId(id))

    NodeMapRing(ring.toMap)
  }
}
