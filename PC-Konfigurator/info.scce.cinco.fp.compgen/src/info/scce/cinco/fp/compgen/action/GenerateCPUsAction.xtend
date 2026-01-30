package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.CPUGenerator

class GenerateCPUsAction extends GeneratorAction {
	
	override generate() { (new CPUGenerator).run }
}