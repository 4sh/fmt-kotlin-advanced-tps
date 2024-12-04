package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*

import com.squareup.kotlinpoet.ksp.*

private val ignoredFunctions = setOf("<init>", "toString")

class Generator(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {

    private val dependencies: MutableMap<String, KSFile> = mutableMapOf()

    fun generateInterfaces(classType: KSClassDeclaration) {
        val classSimpleName = classType.simpleName.asString()
        val classPackageName = classType.packageName.asString()
        val classQualifiedName = "$classPackageName.$classSimpleName"

        logger.warn("Generating interface for $classQualifiedName")


        val fileSpec: FileSpec = generateFile(classPackageName, classSimpleName, classType)
        logger.warn("originatingKSFiles ${fileSpec.originatingKSFiles()}")
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
            addType(generateInterface(packageName, simpleName, classType, readable = true))
            addType(generateInterface(packageName, simpleName, classType, readable = false))
        }.build()

    private fun generateInterface(
        packageName: String,
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
            .build().also { dependencies[packageName + "." + it.name] = classType.containingFile!! }
    }


    private fun generateFunctions(
        classType: KSClassDeclaration,
        typeParamResolver: TypeParameterResolver,
        readable: Boolean = true
    ): List<FunSpec> {
        val classTypeArgumentName = classType.typeParameters.map { it.name.asString() }.toSet()
        return classType.getDeclaredFunctions()
            .filter { func -> ignoredFunctions.none { func.simpleName.asString() == it } }
            .filter { func -> func.getVisibility() == Visibility.PUBLIC }
            .filter { func ->
                if (readable) {
                    val parametersTypeNames = func.parameters.flatMap { valueParameter ->
                        valueParameter.type.getTypeNames()
                    }
                    parametersTypeNames.intersect(classTypeArgumentName).isEmpty()
                } else {
                    val returnTypeNames = func.returnType?.getTypeNames() ?: listOf()
                    returnTypeNames.intersect(classTypeArgumentName).isEmpty()
                }
            }
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
            val modifiers = listOf(KModifier.ABSTRACT) + function.modifiers.filter { it == Modifier.OPERATOR }
                .mapNotNull { it.toKModifier() }

            FunSpec.builder(function.simpleName.asString())
                .addModifiers(modifiers)
                .returns(returnType.toTypeName(typeParamResolver))
                .addParameters(function.parameters.mapNotNull { param -> generateParam(param, typeParamResolver) })
                .build()
        }

    private fun generateParam(
        param: KSValueParameter,
        typeParamResolver: TypeParameterResolver
    ): ParameterSpec? = param.name?.let {
        ParameterSpec.builder(
            it.asString(), param.type.toTypeName(typeParamResolver)
        ).build()
    }

    private fun generateProperties(
        classType: KSClassDeclaration,
        typeParamResolver: TypeParameterResolver
    ): List<PropertySpec> = classType.getDeclaredProperties()
        .filter { prop -> prop.getVisibility() == Visibility.PUBLIC }
        .toList()
        .map { prop ->
            logger.warn("generate property ${prop.simpleName.asString()}")

            PropertySpec.builder(prop.simpleName.asString(), prop.type.toTypeName(typeParamResolver), listOf(KModifier.ABSTRACT))
                .mutable(prop.isMutable)
                .build()
        }

    private fun KSTypeReference.getTypeNames() =
        resolve().arguments.mapNotNull { it.type?.toString() } + resolve().declaration.simpleName.asString()
}