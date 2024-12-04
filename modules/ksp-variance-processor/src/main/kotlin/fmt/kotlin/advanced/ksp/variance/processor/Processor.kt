package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class Processor(codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {

    private val visitor = Visitor(codeGenerator, logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("[VARIANCE INTERFACE] Process sources...")

        TODO("get symbol by annotation and process it with visitor only if invalid")
    }
}
