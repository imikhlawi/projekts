package info.scce.cinco.product.fp.pcconfig.pc.hook

import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.DriveNode
import de.jabc.cinco.meta.runtime.hook.CincoPostDeleteHook

class DriveNodePostDelete extends CincoPostDeleteHook<DriveNode> {
	
	override Runnable getPostDeleteFunction(DriveNode drive_node) {

     val driveContainer = drive_node.getContainer() // retrieve DriveContainer  
     val driveNodeHeight = drive_node.height
     
      return [ 
      	     
      	 var counter = 0 
      	
	      	for (driveNode : driveContainer.getDriveNodes()){
	      	
	      	  driveNode.resize(230,driveNodeHeight)
	          driveNode.x = 10
	          driveNode.y = 10 + ((driveNodeHeight + 10)*(counter))
	          counter = counter + 1
	          
	          }
	    ]   
	}	
}