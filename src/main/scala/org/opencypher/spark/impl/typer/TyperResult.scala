package org.opencypher.spark.impl.typer

case class TyperResult[A](value: A, recorder: TypeRecorder, tracker: TypeTracker)
