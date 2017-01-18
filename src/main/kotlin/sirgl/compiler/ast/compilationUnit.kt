package sirgl.compiler.ast;

data class CompilationUnit(var classDefinition: ClassDefinition,
                           var packageDeclaration: PackageDeclaration,
                           var importDeclarations: List<ImportDeclaration>)

data class PackageDeclaration(var name: String) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(name: String, line: Int, position: Int) : this(name) {
        this.line = line
        this.position = position
    }
}

data class ImportDeclaration(var className: String) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(className: String, line: Int, position: Int) : this(className) {
        this.line = line
        this.position = position
    }
}