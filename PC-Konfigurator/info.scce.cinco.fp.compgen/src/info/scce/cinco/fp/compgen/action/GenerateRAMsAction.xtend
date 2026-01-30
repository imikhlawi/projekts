package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.RAMGenerator

class GenerateRAMsAction extends GeneratorAction {
	
	override generate() { (new RAMGenerator).run }
}