package info.scce.cinco.product.fp.pcconfig.mb.hook

import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.GPUNode
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.fp.compdsl.componentDsl.Gpu;

class GPUNodePostCreate extends CincoPostCreateHook<GPUNode> {
	
    override postCreate(GPUNode gpu_node) {
	gpu_node.x = 5
   	gpu_node.y = 5
   	gpu_node.resize(gpu_node.container.width-10,gpu_node.container.height-10)  
   	
  	setAttributes(gpu_node)  
  	
    } 
    
     /***
     * @param gpu_node: set attributes of gpu_node.
     * 
     ***/
    private def void setAttributes(GPUNode gpu_node){
     
     val gpu = gpu_node.getReferencedGPU() as Gpu
  	  
  	 gpu_node.setName(gpu.getName())
  	 gpu_node.setChip(gpu.getChip())
  	 gpu_node.setMemory(gpu.getMemory())
  	 gpu_node.setPowerConsumption(gpu.getPowerConsumption())
  	 gpu_node.setPrice(gpu.getPrice())
    	
    	
    }
	
}