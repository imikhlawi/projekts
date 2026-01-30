package info.scce.cinco.fp.compgen.action

import info.scce.cinco.fp.compgen.generator.PowerSupplyGenerator

class GeneratePowerSuppliesAction extends GeneratorAction {
	
	override generate() { (new PowerSupplyGenerator).run }
}