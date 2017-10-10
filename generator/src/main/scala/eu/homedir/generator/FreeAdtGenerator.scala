package eu.homedir.generator

import com.google.protobuf.Descriptors.FileDescriptor
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.compiler.PluginProtos.{CodeGeneratorRequest, CodeGeneratorResponse}
import com.trueaccord.scalapb.compiler.{DescriptorPimps, GeneratorParams, ProtobufGenerator}
import com.trueaccord.scalapb.compiler.ProtobufGenerator.parseParameters
import protocbridge.ProtocCodeGenerator

import scala.collection.JavaConverters._

object FreeAdtGenerator extends ProtocCodeGenerator {

  override def run(requestBytes: Array[Byte]): Array[Byte] = {

    val request = CodeGeneratorRequest.parseFrom(requestBytes,  ExtensionRegistry.newInstance())
    val vanillaBuilder = CodeGeneratorResponse.newBuilder(ProtobufGenerator.handleCodeGeneratorRequest(request))
    val params: GeneratorParams = parseParameters(request.getParameter).right.get

    val fileByName: Map[String, FileDescriptor] =
      request.getProtoFileList.asScala.foldLeft[Map[String, FileDescriptor]](Map.empty) {
        case (acc, fp) =>
          val deps = fp.getDependencyList.asScala.map(acc)
          acc + (fp.getName -> FileDescriptor.buildFrom(fp, deps.toArray))
      }

    val generator = new FreeAdtGenerator(params)

    request.getFileToGenerateList.asScala.foldLeft(vanillaBuilder){
      case (b, name) =>
        val file: FileDescriptor = fileByName(name)
        val responseFiles = generator(file)
        b.addAllFile(responseFiles.asJava)
    }.build().toByteArray

  }

}

class FreeAdtGenerator(val params: GeneratorParams) extends DescriptorPimps {
  def apply(file: FileDescriptor): Seq[CodeGeneratorResponse.File] = Seq()

}
