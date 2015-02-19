name := "playon-aws-scala"

version := "1.0.0"

scalaVersion := "2.11.5"

organization := "com.playonsports"

val playonArtifacts = "PlayOn S3" at "s3://ivy2-test.playonsports.com/snapshots"

publishTo := Some(playonArtifacts)

resolvers += playonArtifacts

libraryDependencies += "com.playonsports" %% "playon-aggregates" % "1.0.0"


libraryDependencies += "com.amazonaws" % "aws-java-sdk-sqs" % "1.9.16"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-sns" % "1.9.16"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.9.16"




