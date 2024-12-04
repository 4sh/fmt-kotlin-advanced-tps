package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

class Generator(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {

    fun generateInterfaces(classType: KSClassDeclaration) {
        val classSimpleName = classType.simpleName.asString()
        val classPackageName = classType.packageName.asString()
        val classQualifiedName = "$classPackageName.$classSimpleName"

        logger.warn("generating interface for $classQualifiedName")

        val fileSpec: FileSpec = generateFile(classPackageName, classSimpleName, classType)

        TODO("write file")
    }

    private fun generateFile(
        packageName: String,
        simpleName: String,
        classType: KSClassDeclaration
    ): FileSpec =
        TODO("build file")

    private fun generateInterface(
        simpleName: String,
        classType: KSClassDeclaration,
        readable: Boolean,
    ): TypeSpec {
        TODO("build interface")
    }
}
