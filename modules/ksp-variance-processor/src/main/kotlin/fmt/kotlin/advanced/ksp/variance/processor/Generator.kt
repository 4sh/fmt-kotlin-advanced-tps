package fmt.kotlin.advanced.ksp.variance.processor


import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*

import com.squareup.kotlinpoet.ksp.*

private val ignoredFunctions = setOf("<init>", "toString")

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
        val interfaceName = readable.takeIf { it }?.let { "Readable$simpleName" } ?: "Writeable$simpleName"
        logger.warn("generate $interfaceName")
        val typeParamResolver = classType.typeParameters.toTypeParameterResolver()
        return TypeSpec.interfaceBuilder(interfaceName)
            .addTypeVariables(classType.typeParameters.map { typeParam ->
                TypeVariableName(
                    typeParam.name.asString(),
                    variance = readable.takeIf { it }?.let { KModifier.OUT } ?: KModifier.IN)
            })
            .addOriginatingKSFile(classType.containingFile!!)
            .addFunctions(generateFunctions(classType, typeParamResolver, readable))
            .addProperties(generateProperties(classType, typeParamResolver))
            .build()
    }


    private fun generateFunctions(
        classType: KSClassDeclaration,
        typeParamResolver: TypeParameterResolver,
        readable: Boolean = true
    ): List<FunSpec> {
        val classTypeArgumentName = classType.typeParameters.map { it.name.asString() }.toSet()
        return classType.getDeclaredFunctions()
            //
            .toList()
            .mapNotNull { func ->
                generateFunction(func, typeParamResolver)
            }
    }

    private fun generateFunction(
        function: KSFunctionDeclaration,
        typeParamResolver: TypeParameterResolver
    ): FunSpec? =
        function.returnType?.let { returnType ->
            logger.warn("generate function ${function.simpleName.asString()}")



        }

    private fun generateParam(
        param: KSValueParameter,
        typeParamResolver: TypeParameterResolver
    ): ParameterSpec? = param.name?.let {

    }

    private fun generateProperties(
        classType: KSClassDeclaration,
        typeParamResolver: TypeParameterResolver
    ): List<PropertySpec> = classType.getDeclaredProperties()
        .filter { prop -> prop.getVisibility() == Visibility.PUBLIC }
        .toList()
        .map { prop ->
            logger.warn("generate property ${prop.simpleName.asString()}")


        }

    private fun KSTypeReference.getTypeNames() =
        resolve().arguments.mapNotNull { it.type?.toString() } + resolve().declaration.simpleName.asString()
}