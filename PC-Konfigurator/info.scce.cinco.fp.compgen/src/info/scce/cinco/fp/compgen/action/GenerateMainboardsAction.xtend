package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.MainboardGenerator

class GenerateMainboardsAction extends GeneratorAction {
	
	override generate() { (new MainboardGenerator).run }
}