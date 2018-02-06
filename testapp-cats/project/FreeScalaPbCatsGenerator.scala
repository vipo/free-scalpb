import com.google.protobuf.Descriptors.{FileDescriptor, ServiceDescriptor}
import com.google.protobuf.compiler.PluginProtos.{CodeGeneratorRequest, CodeGeneratorResponse}
import com.trueaccord.scalapb.compiler.{DescriptorPimps, FunctionalPrinter, GeneratorParams}
import protocbridge.ProtocCodeGenerator

import scala.collection.JavaConverters._

object FreeScalaPbCatsGenerator extends ProtocCodeGenerator with DescriptorPimps {

  private val AdtTraitSuffix = "Method"

  private def generateDsl(service: ServiceDescriptor): FunctionalPrinter = {
    val ServiceName = service.getName
    val TraitName = s"$ServiceName$AdtTraitSuffix"

    def cases(p: FunctionalPrinter) = service.getMethods.asScala.foldLeft(p) { (p, m) =>
      p.add(s"case class ${m.getName.capitalize}(value: ${m.scalaIn}) extends $TraitName[${m.scalaOut}]")
    }

    def methods(p: FunctionalPrinter) = service.getMethods.asScala.foldLeft(p) { (p, m) =>
      p.add(s"def ${m.getName}(value: ${m.scalaIn}): Free[C, ${m.scalaOut}] = Free.inject[$TraitName, C](${m.getName.capitalize}(value))")
    }

    FunctionalPrinter()
      .add("package " + service.getFile.scalaPackageName)
      .newline
      .add("import cats._")
      .add("import cats.data._")
      .add("import cats.free._")
      .add("import cats.implicits._")
      .newline
      .add(s"import ${ServiceName}Algebra._")
      .newline
      .add(s"object ${ServiceName}Algebra {")
      .indent
      .add(s"sealed trait $TraitName[A]")
      .call(cases)
      .outdent
      .add("}")
      .newline
      .add(s"class ${ServiceName}Free[C[_]](implicit i: InjectK[$TraitName, C]) {")
      .indent
      .call(methods)
      .outdent
      .add("}")
      .newline
      .add(s"object ${ServiceName}Free {")
      .indent
      .add(s"implicit def inject[C[_]](implicit i: InjectK[$TraitName, C]): ${ServiceName}Free[C] = new ${ServiceName}Free[C]")
      .outdent
      .add("}")
      .newline
  }

  private def generateInterpreter(service: ServiceDescriptor): FunctionalPrinter = {
    val ServiceName = service.getName
    val TraitName = s"$ServiceName$AdtTraitSuffix"

    def methods(p: FunctionalPrinter) = service.getMethods.asScala.foldLeft(p) { (p, m) =>
      p.add(s"case ${m.getName.capitalize}(v) => stub.${m.getName}(v)")
    }

    FunctionalPrinter()
      .add(s"package ${service.getFile.scalaPackageName}")
      .newline
      .add("import cats.{Id, ~>}")
      .newline
      .add(s"import ${ServiceName}Grpc._")
      .add(s"import ${ServiceName}Algebra._")
      .newline
      .add(s"class ${ServiceName}Interpreter(channel: io.grpc.Channel, options: io.grpc.CallOptions = io.grpc.CallOptions.DEFAULT) extends ($TraitName ~> Id) {")
      .indent
      .add(s"private val stub = new ${ServiceName}BlockingStub(channel, options)")
      .newline
      .add(s"def apply[A](method: $TraitName[A]) = method match {")
      .indent
      .call(methods)
      .outdent
      .add("}")
      .outdent
      .add("}")
  }

  override def run(req: Array[Byte]): Array[Byte] = {
    val request = CodeGeneratorRequest.parseFrom(req)
    handleCodeGeneratorRequest(request).toByteArray
  }

  private def generate(file: FileDescriptor): List[CodeGeneratorResponse.File] =
    for {
      service <- file.getServices.asScala.toList
      response = responseFile(file, service)_
      result <- List(
        response("Dsl",         generateDsl),
        response("Interpreter", generateInterpreter)
      )
    } yield result

  private def responseFile(file: FileDescriptor, service: ServiceDescriptor)(suffix: String, printer: ServiceDescriptor => FunctionalPrinter): CodeGeneratorResponse.File = {
    val b = CodeGeneratorResponse.File.newBuilder()
    b.setName(file.scalaDirectory + "/" + service.getName + s"Grpc$suffix.scala")
    b.setContent(printer(service).result())
    b.build
  }

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

  override def params: GeneratorParams = GeneratorParams()
}
