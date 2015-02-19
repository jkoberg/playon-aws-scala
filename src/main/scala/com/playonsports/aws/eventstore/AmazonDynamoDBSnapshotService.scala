package com.playonsports.aws.eventstore

import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec
import com.playonsports.eventstore._
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.concurrent._


class AmazonDynamoDBSnapshotService(ddb:DynamoDB)(implicit ec:ExecutionContext) extends EventSnapshotService {
  val snapshotTable = ddb.getTable("Snapshots")

  def getSnapshot(id:String): Future[Option[SnapshotEntry]] = {
    Future {
      blocking {
        snapshotTable.getItem("aggId", id) match {
          case null => None
          case item => Some(SnapshotEntry(
            id,
            item.getInt("seqNr"),
            new DateTime(item.getString("timestamp")),
            item.getString("purpose"),
            Json.parse(item.getJSON("data"))
          ))
        }
      }
    }
  }

  def persistSnapshot(entry:SnapshotEntry) : Future[SnapshotEntry] = {
    var newItem = new Item()
      .withPrimaryKey  ("aggId",     entry.aggId)
      .withInt         ("seqNr",     entry.seqNr)
      .withString      ("timestamp", entry.tstamp.toString)
      .withJSON        ("data",      Json.stringify(entry.data))

    if(entry.purpose != null)
      newItem = newItem.withString("purpose", entry.purpose)

    Future {
      blocking {
        val result = snapshotTable.putItem(new PutItemSpec().withItem(newItem))
        entry
      }
    }
  }


  def invalidate(entry:SnapshotEntry) = Future {
    blocking {
      snapshotTable.deleteItem("aggId", entry.aggId)
    }
  }

}