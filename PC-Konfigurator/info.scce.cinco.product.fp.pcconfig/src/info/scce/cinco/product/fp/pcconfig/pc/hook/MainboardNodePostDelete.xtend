package info.scce.cinco.product.fp.pcconfig.pc.hook
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.MainboardNode
import de.jabc.cinco.meta.runtime.hook.CincoPostDeleteHook

class MainboardNodePostDelete extends CincoPostDeleteHook<MainboardNode>{
	
 override getPostDeleteFunction(MainboardNode mainboardNode) {
		
 	val driveContainer = mainboardNode.getContainer().getContainer().getDriveContainers().get(0)
 	
 	return[
 		
 		for (driveNode:driveContainer.getDriveNodes()){
 			driveNode.delete()
 		}	
	  ] 			
   }
}