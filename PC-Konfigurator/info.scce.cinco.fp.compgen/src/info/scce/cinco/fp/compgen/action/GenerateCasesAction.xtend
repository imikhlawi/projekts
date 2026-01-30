package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.CaseGenerator

class GenerateCasesAction extends GeneratorAction {
	
	override generate() { (new CaseGenerator).run }
}