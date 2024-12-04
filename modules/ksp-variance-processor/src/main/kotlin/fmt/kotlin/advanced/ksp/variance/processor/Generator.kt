package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*

import com.squareup.kotlinpoet.ksp.*

class Generator(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {

    fun generateInterfaces(classType: KSClassDeclaration) {
        val classSimpleName = classType.simpleName.asString()
        val classPackageName = classType.packageName.asString()
        val classQualifiedName = "$classPackageName.$classSimpleName"

        logger.warn("generating interface for $classQualifiedName")

        val fileSpec: FileSpec = generateFile(classPackageName, classSimpleName, classType)

        // write file
    }

    private fun generateFile(
        packageName: String,
        simpleName: String,
        classType: KSClassDeclaration
    ): FileSpec =
        // build file

    private fun generateInterface(
        simpleName: String,
        classType: KSClassDeclaration,
        readable: Boolean,
    ): TypeSpec {
        // build interface
    }

}