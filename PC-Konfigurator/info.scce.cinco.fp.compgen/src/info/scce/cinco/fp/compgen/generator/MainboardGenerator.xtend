package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import info.scce.cinco.fp.prodserv.descriptor.MainboardDescriptor
import java.util.ArrayList

class MainboardGenerator {
	
	ArrayList<MainboardDescriptor> mainboards
	
	def run() {
		mainboards = ProductService.mainboards
		template.toString
	}

	 def static String formatMainboardDescriptor(MainboardDescriptor descriptor) {
        return '''
        MainboardDescriptor [
          name = "«descriptor.name»"
          socket = «descriptor.socket»
          chipset = "«descriptor.chipset»"
          typeMemorySlots = «descriptor.typeMemorySlots»
          numMemorySlots = «descriptor.numMemorySlots»
          numPCIe16Slots = «descriptor.numPCIe16Slots»
          numSataPorts = «descriptor.numSataPorts»
          powerConsumption = «descriptor.powerConsumption»
          formFactor = «descriptor.formFactor»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
		
	 «mainboards.map[formatMainboardDescriptor(it)].join('\n')»
		
	   }'''
	}
}