package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import info.scce.cinco.fp.prodserv.descriptor.RAMDescriptor
import java.util.ArrayList

class RAMGenerator {
	
	ArrayList<RAMDescriptor> rams
	
	def run() {
		rams = ProductService.RAMs
		template.toString
	}
	
	 def static String formatRAMDescriptor(RAMDescriptor descriptor) {
        return '''
        RAMDescriptor [
          name = "«descriptor.name»"
          type = «descriptor.type»
          capacity = «descriptor.capacity»
          powerConsumption = «descriptor.powerConsumption»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
	 «rams.map[formatRAMDescriptor(it)].join('\n')»
		
	   }'''
	}
}