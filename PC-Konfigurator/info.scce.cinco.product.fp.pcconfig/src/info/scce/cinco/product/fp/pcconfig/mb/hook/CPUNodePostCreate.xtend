package info.scce.cinco.product.fp.pcconfig.mb.hook

import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.CPUNode
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.fp.compdsl.componentDsl.Cpu;

class CPUNodePostCreate extends CincoPostCreateHook<CPUNode> {
	
	override postCreate (CPUNode cpu_node) {
	cpu_node.x = 5
   	cpu_node.y = 5
   	cpu_node.resize(cpu_node.container.width-10,cpu_node.container.height-10)  
 
	setAttributes(cpu_node)
   	   	   	
    } 
    
    
     /***
     * @param cpu_node: set attributes of cpu_node.
     * 
     ***/
    
     private def void setAttributes(CPUNode cpu_node){
     	
	    val cpu = cpu_node.getReferencedCPU() as Cpu  
	   	cpu_node.setName(cpu.getName())
	   	cpu_node.setCpuType(cpu.getType())
	    cpu_node.setPowerConsumption(cpu.getPowerConsumption())
	   	cpu_node.setSocket(cpu.getSocket().toString())
	   	cpu_node.setPrice(cpu.getPrice())
     	
     }
    
    
}