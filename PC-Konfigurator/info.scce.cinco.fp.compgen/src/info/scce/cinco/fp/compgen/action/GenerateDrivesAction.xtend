package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.DriveGenerator

class GenerateDrivesAction extends GeneratorAction {
	
	override generate() { (new DriveGenerator).run }
}