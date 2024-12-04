package fmt.kotlin.advanced.ksp.variance.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class Visitor(codeGenerator: CodeGenerator, private val logger: KSPLogger) : KSVisitorVoid() {

    private val generator = Generator(codeGenerator, logger)

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        // start code generation
    }
}