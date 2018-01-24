package com.github.vipo

import com.google.protobuf.Descriptors.FileDescriptor
import com.google.protobuf.compiler.PluginProtos.{CodeGeneratorRequest, CodeGeneratorResponse}

import protocbridge.ProtocCodeGenerator
import scala.collection.JavaConverters._

object FreeScalaPbGenerator extends ProtocCodeGenerator {

  override def run(req: Array[Byte]): Array[Byte] = {
    val request = CodeGeneratorRequest.parseFrom(req)
    handleCodeGeneratorRequest(request).toByteArray
  }

  private def generate(file: FileDescriptor): Seq[CodeGeneratorResponse.File] = Nil

  private def handleCodeGeneratorRequest(request: CodeGeneratorRequest): CodeGeneratorResponse = {
    val fileByName: Map[String, FileDescriptor] =
      request.getProtoFileList.asScala.foldLeft[Map[String, FileDescriptor]](Map.empty) {
        case (acc, fp) =>
          val deps = fp.getDependencyList.asScala.map(acc)
          acc + (fp.getName -> FileDescriptor.buildFrom(fp, deps.toArray))
      }
    request.getFileToGenerateList.asScala.foldLeft(CodeGeneratorResponse.newBuilder) {
      case (b, name) => b.addAllFile(generate(fileByName(name)).asJava)
    }.build()
  }
}
