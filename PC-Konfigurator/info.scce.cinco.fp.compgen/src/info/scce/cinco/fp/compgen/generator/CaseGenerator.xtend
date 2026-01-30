package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import info.scce.cinco.fp.prodserv.descriptor.CaseDescriptor
import java.util.ArrayList

class CaseGenerator {
	
	ArrayList<CaseDescriptor> cases
	
	def run() {
		cases = ProductService.cases
		template.toString
	}
				
	 def static String formatCaseDescriptor(CaseDescriptor descriptor) {
        return '''
        CaseDescriptor [
          name = "«descriptor.name»"
          formFactor = «descriptor.formFactor»
          internalSlots = «descriptor.internalSlots»
          externalSlots = «descriptor.externalSlots»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
		
	 «cases.map[formatCaseDescriptor(it)].join('\n')»
		
	   }'''
	}
}