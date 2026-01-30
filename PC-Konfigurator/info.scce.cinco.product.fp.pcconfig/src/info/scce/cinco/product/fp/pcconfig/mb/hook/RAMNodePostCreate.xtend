package info.scce.cinco.product.fp.pcconfig.mb.hook

import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.RAMNode
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.fp.compdsl.componentDsl.Ram;


class RAMNodePostCreate extends CincoPostCreateHook<RAMNode> {
	
 override postCreate(RAMNode ram_node) {
	ram_node.x = 3
   	ram_node.y = 3
   	ram_node.resize(ram_node.container.width-6,ram_node.container.height-6)  
   	  	
   	setAttributes(ram_node)   
   	   
    } 
    
    
     /***
     * @param ram_node: set attributes of ram_node.
     * 
     ***/
    
     private def void setAttributes(RAMNode ram_node){
     	   
	    val ram = ram_node.getReferencedRAM() as Ram
	   	
	   	ram_node.setName(ram.getName())
	    ram_node.setRamType(ram.getType().toString())
	    ram_node.setCapacity(ram.getCapacity())
	    ram_node.setPowerConsumption(ram.getPowerConsumption())
		ram_node.setPrice(ram.getPrice())   	
     	     	
     }
    
}