package sirgl.compiler.transform

import com.sun.xml.internal.fastinfoset.QualifiedName
import sirgl.compiler.ParserException
import sirgl.compiler.ast.*

fun LangParser.CompilationUnitContext.toAst(): CompilationUnit {
    return CompilationUnit(classDefinition().toAst(), packageDeclaration().toAst(), importDeclaration().map { it.toAst() })
}

fun LangParser.ImportDeclarationContext.toAst(): ImportDeclaration {
    return ImportDeclaration(qualifiedName().toAst(), start.line, start.charPositionInLine)
}


fun LangParser.PackageDeclarationContext.toAst(): PackageDeclaration {
    return PackageDeclaration(qualifiedName().toAst(), start.line, start.charPositionInLine)
}

fun LangParser.QualifiedNameContext.toAst() = Identifier().map { s -> s.text }.reduceRight { s1, s2 -> "$s1.$s2" }