package fmt.kotlin.advanced.ksp.variance.processor


import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo

class Generator(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {

    fun generateInterfaces(classType: KSClassDeclaration) {
        val classSimpleName = classType.simpleName.asString()
        val classPackageName = classType.packageName.asString()
        val classQualifiedName = "$classPackageName.$classSimpleName"

        logger.warn("generating interface for $classQualifiedName")
        val fileSpec: FileSpec = generateFile(classPackageName, classSimpleName, classType)
        fileSpec.writeTo(codeGenerator, aggregating = false)
    }

    private fun generateFile(
        packageName: String,
        simpleName: String,
        classType: KSClassDeclaration
    ): FileSpec =
        FileSpec.builder(
            packageName = packageName,
            fileName = simpleName
        ).apply {
            addType(generateInterface(simpleName, classType, readable = true))
            addType(generateInterface(simpleName, classType, readable = false))
        }.build()

    private fun generateInterface(
        simpleName: String,
        classType: KSClassDeclaration,
        readable: Boolean,
    ): TypeSpec {
        val interfaceName = readable.takeIf { it }?.let { "TmpReadable$simpleName" } ?: "TmpWriteable$simpleName"
        logger.warn("generate $interfaceName")
        return TypeSpec.interfaceBuilder(interfaceName)
            .addTypeVariables(classType.typeParameters.map { typeParam ->
                TypeVariableName(
                    typeParam.name.asString(),
                    variance = readable.takeIf { it }?.let { KModifier.OUT } ?: KModifier.IN)
            })
            .addOriginatingKSFile(classType.containingFile!!)
            .build()
    }
}