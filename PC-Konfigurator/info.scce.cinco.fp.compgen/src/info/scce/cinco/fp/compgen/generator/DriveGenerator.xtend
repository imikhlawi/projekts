package info.scce.cinco.fp.compgen.generator

import info.scce.cinco.fp.prodserv.ProductService
import info.scce.cinco.fp.prodserv.descriptor.DriveDescriptor
import java.util.ArrayList

class DriveGenerator {
	
	ArrayList<DriveDescriptor> drives
	
	def run() {
		drives = ProductService.drives
		template.toString
	}
		
	 def static String formatDriveDescriptor(DriveDescriptor descriptor) {
        return '''
        DriveDescriptor [
          name = "«descriptor.name»"
          type = «descriptor.type»
          description = "«descriptor.description»"
          powerConsumption = «descriptor.powerConsumption»
          price = «descriptor.price»
        ]'''
    }
	
	def template(){
	'''{
		
	 «drives.map[formatDriveDescriptor(it)].join('\n')»
		
	   }'''
	}
}