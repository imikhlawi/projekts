package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import java.util.ArrayList
import info.scce.cinco.fp.prodserv.descriptor.GPUDescriptor

class GPUGenerator {
	
	ArrayList<GPUDescriptor> gpus
	
	def run() {
		gpus = ProductService.GPUs
		template.toString
	}
	
	 def static String formatGPUDescriptor(GPUDescriptor descriptor) {
        return '''
        GPUDescriptor [
          name = "«descriptor.name»"
          chip = "«descriptor.chip»"
          memory = «descriptor.memory»
          powerConsumption = «descriptor.powerConsumption»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
		
	 «gpus.map[formatGPUDescriptor(it)].join('\n')»
		
	   }'''
	}
}