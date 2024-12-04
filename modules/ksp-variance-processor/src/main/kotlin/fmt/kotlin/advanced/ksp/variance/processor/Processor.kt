package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import fmt.kotlin.advanced.annotation.variance.VarianceInterface
import com.google.devtools.ksp.validate

class Processor(codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {

    private val visitor = Visitor(codeGenerator, logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("[VARIANCE INTERFACE]Process sources...")

        val annotationName = VarianceInterface::class.qualifiedName
        return if (annotationName != null) {
            val (validSymbols, invalidSymbols) = resolver.getSymbolsWithAnnotation(annotationName)
                .partition { it.validate() }

            (// ?).onEach { symbol ->
                symbol.accept(visitor, Unit)
            }
            // ?
        } else {
            emptyList()
        }.also { logger.warn("") }
    }
}