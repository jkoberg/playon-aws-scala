package com.playonsports.aws

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.{AmazonSQSAsync, AmazonSQSAsyncClient}

/**
 * Provide some nice Scala-ized future methods for AWS
 */
class AmazonSQSAdapter(sqs:AmazonSQSAsync=new AmazonSQSAsyncClient(new DefaultAWSCredentialsProviderChain())) {
  def receive =     AmazonFuturesHelper.adapt[    ReceiveMessageRequest,     ReceiveMessageResult](sqs.receiveMessageAsync)(_)
  def send =        AmazonFuturesHelper.adapt[       SendMessageRequest,        SendMessageResult](sqs.sendMessageAsync)(_)
  def sendBatch =   AmazonFuturesHelper.adapt[  SendMessageBatchRequest,   SendMessageBatchResult](sqs.sendMessageBatchAsync)(_)
  def delete =      AmazonFuturesHelper.adapt[     DeleteMessageRequest,                     Void](sqs.deleteMessageAsync)(_)
  def deleteBatch = AmazonFuturesHelper.adapt[DeleteMessageBatchRequest, DeleteMessageBatchResult](sqs.deleteMessageBatchAsync)(_)
  def createQueue = AmazonFuturesHelper.adapt[       CreateQueueRequest,        CreateQueueResult](sqs.createQueueAsync)(_)
  def deleteQueue = AmazonFuturesHelper.adapt[       DeleteQueueRequest,                     Void](sqs.deleteQueueAsync)(_)
}

