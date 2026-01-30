package info.scce.cinco.product.fp.pcconfig.pc.hook

import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PSUNode
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.fp.compdsl.componentDsl.PowerSupply


class PSUNodePostCreate extends CincoPostCreateHook<PSUNode> {
	
    /* Post Create Hook for PSUNodes.
     * change position of [node] and resize it to fill the container 
     * @param psuNode: created PSUNode 
	 */
  override postCreate (PSUNode psu_node) {
	
	psu_node.x = 10
   	psu_node.y = 10
   	psu_node.resize(psu_node.container.width-20,psu_node.container.height-20)  
   	
	setAttributes(psu_node) 	   	  
	 	
    }
    
    
     /***
     * @param psu_node: set attributes of psu_node.
     * 
     ***/
    private def void setAttributes(PSUNode psu_node){
    		
     	val referencedPSU = psu_node.getReferencedPSU() as PowerSupply
   		psu_node.setPsuName(referencedPSU.name)
   		psu_node.setPsuPower(referencedPSU.power)
   		psu_node.setPsuPrice(referencedPSU.price)
    	
    }
    
}


