package com.playonsports.aws.eventstore

import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException
import org.joda.time.DateTime
import play.api.libs.json.Json

import com.playonsports.eventstore._

import scala.collection.JavaConversions._
import scala.concurrent._


class AmazonDynamoDBJournalService(ddb:DynamoDB)(implicit ec:ExecutionContext)
extends EventJournalService {
  val journalTable = ddb.getTable("Journal")

  private def processEvents(id: String)(result: ItemCollection[QueryOutcome]) = {
      (for (item <- result) yield {
        JournalEntry(
          id,
          item.getInt("seqNr"),
          new DateTime(item.getString("timestamp")),
          item.getString("category"),
          Json.parse(item.getJSON("data"))
        )
      }).toSeq
  }

  def getAllEvents(id: String): Future[Seq[JournalEntry]] =
    Future {
      blocking {
        journalTable.query("aggId", id)
      }
    } map processEvents(id)

  def getNewerEvents(id: String, seqNr: Int): Future[Seq[JournalEntry]] =
    Future {
      blocking {
        journalTable.query("aggId", id, new RangeKeyCondition("seqNr").gt(seqNr))
      }
    } map processEvents(id)


  def persist(entry: JournalEntry): Future[JournalEntry] = {
    var itemToPut = new Item()
      .withPrimaryKey("aggId", entry.aggId)
      .withKeyComponent("seqNr", entry.seqNr)
      .withString("timestamp", entry.tstamp.toString)
      .withJSON("data", Json.stringify(entry.data))

    if (entry.category != null)
      itemToPut = itemToPut.withString("category", entry.category)

    val spec =
      new PutItemSpec()
        .withItem(itemToPut)
        .withExpected(
          new Expected("aggId").notExist(),
          new Expected("seqNr").notExist()
        )
    Future {
      blocking {
        try {
          journalTable.putItem(spec)
          entry
        } catch {
          case ex: ConditionalCheckFailedException =>
            throw new ConcurrencyViolation(ex.getMessage)
        }
      }
    }
  }
}
