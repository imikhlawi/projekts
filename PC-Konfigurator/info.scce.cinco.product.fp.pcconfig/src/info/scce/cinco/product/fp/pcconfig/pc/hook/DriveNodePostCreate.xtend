package info.scce.cinco.product.fp.pcconfig.pc.hook

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.DriveNode
import info.scce.cinco.fp.compdsl.componentDsl.Drive;


class DriveNodePostCreate extends CincoPostCreateHook<DriveNode> {
	
	/***
	* Post Create Hook for DriveNode.
	* Resize and calculate the position of [node] in the DriveContainer
    * @param drive_node: new created DriveNode
    * @throws IllegalArgumentException when no Main board model is installed
    ***/
	 
    override postCreate (DriveNode drive_node) {
    		
	     
	     val mainBoardNodes = drive_node.getContainer().getContainer().getPCMainboardContainers().get(0).getMainboardNodes()
	   	 
	   	 // throw IllegalArgumentException when no Main board model is installed 
	   	 if (mainBoardNodes.isEmpty()){
	        throw new IllegalArgumentException("You can not add Hard drives until you install a Main board model\n")
	   	 }
	   	
	   	// Retrieve Main board container of the Main board graph model
	   	val referencedMBContainer = mainBoardNodes.get(0).getReferencedMB().getMainboardContainers().get(0)
	   
	   	// Retrieve number of SATA Ports
	    val numSATAPorts = referencedMBContainer.getNumSataPorts()  	
	   	val container = drive_node.getContainer() // retrieve DriveContainer
	   	val numDrives = container.getDriveNodes().size() // retrieve number of already created DriveNodes
	   	val driveNodeHeight = (container.height - 100) / numSATAPorts // calculate DriveNode height based on numSATAPorts and DriveContainer height 
	   
	    // resize 
	   	drive_node.resize(230 ,driveNodeHeight)  
	   		
	   	// calculate DriveNode position based on number of installed Drives and DriveNodeheight height 
	    drive_node.y = 10 + ((driveNodeHeight + 10)*(numDrives-1)) 
	    drive_node.x = 10 
	    
	    // Set Drive Attributes
	    setAttributes(drive_node)
    } 
    
    
    /***
     * @param drive_node: set attributes of drive_node.
     * 
     ***/
    private def void setAttributes(DriveNode drive_node){
    		
    	val referencedDrive = drive_node.getReferencedDrive() as Drive
    	drive_node.setDriveName(referencedDrive.name) 
    	drive_node.setDriveType(referencedDrive.type.toString()) 
    	drive_node.setDriveDescription(referencedDrive.description)
    	drive_node.setDrivePowerConsumption(referencedDrive.powerConsumption)
    	drive_node.setDrivePrice(referencedDrive.price)
    	
    }
}