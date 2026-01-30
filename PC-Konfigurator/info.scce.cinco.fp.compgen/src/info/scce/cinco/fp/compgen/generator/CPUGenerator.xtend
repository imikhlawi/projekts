package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.descriptor.CPUDescriptor
import info.scce.cinco.fp.prodserv.ProductService
import java.util.ArrayList

class CPUGenerator {
	
	ArrayList<CPUDescriptor> cpus
	
	def run() {
		
		cpus = ProductService.CPUs
		template.toString		
	}
	
	 def static String formatCPUDescriptor(CPUDescriptor descriptor) {
        return '''
        CPUDescriptor [
          name = "«descriptor.name»"
          type = "«descriptor.type»"
          powerConsumption = «descriptor.powerConsumption»
          socket = «descriptor.socket»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
	 «cpus.map[formatCPUDescriptor(it)].join('\n')»
		
	   }'''
	}
}