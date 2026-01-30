package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import info.scce.cinco.fp.prodserv.descriptor.PowerSupplyDescriptor
import java.util.ArrayList

class PowerSupplyGenerator {
	
	ArrayList<PowerSupplyDescriptor> supplies
	
	def run() {
		supplies = ProductService.powerSupplies
		template.toString
	}
		
	 def static String formatPowerSupplyDescriptor(PowerSupplyDescriptor descriptor) {
        return '''
        PowerSupplyDescriptor [
          name = "«descriptor.name»"
          power = «descriptor.power»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
	 «supplies.map[formatPowerSupplyDescriptor(it)].join('\n')»
		
	   }'''
	}
}