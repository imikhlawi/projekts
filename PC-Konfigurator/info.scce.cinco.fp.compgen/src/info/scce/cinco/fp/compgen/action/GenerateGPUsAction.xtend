package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.GPUGenerator

class GenerateGPUsAction extends GeneratorAction {
	
	override generate() { (new GPUGenerator).run }
}