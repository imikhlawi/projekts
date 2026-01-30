package info.scce.cinco.product.fp.pcconfig.mb.hook

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.MainboardContainer
import info.scce.cinco.fp.compdsl.componentDsl.Mainboard;

class MainboardContainerPostCreate extends CincoPostCreateHook<MainboardContainer> {
	
	override postCreate(MainboardContainer mainboard_container) {
   	     	  
   	   val cpuSlot = mainboard_container.newCPUSlot(20,20)
   	   cpuSlot.resize(200,200)
   	  
   	   val gpuSlot = mainboard_container.newGPUSlot(20,240)
   	   gpuSlot.resize(200,200)
 
   	   val referencedMB = mainboard_container.getReferencedMB() as Mainboard
       cpuSlot.setSocket(referencedMB.getSocket().toString()) 
     	 
	   setAttributes(mainboard_container)
		  	   
   	   val numMemorySlots = referencedMB.getNumMemorySlots()
   	   
   	   for (var i=1; i<=numMemorySlots ; i++){
   	   	  
   	   	  val ramSlot = mainboard_container.newRAMSlot(240 + (60*(i-1)),20)
   	  	  ramSlot.resize(50,420)
   	  	  ramSlot.setRamType(referencedMB.typeMemorySlots.toString())
   	    
   	   }
   	   
   	   if (numMemorySlots == 2){
   	   	   	  mainboard_container.resize(370,480)
   	   }else{
   	   	   	 mainboard_container.resize(490,480)
   	   }
    } 
    
    
     /***
     * @param mainboard_container: set attributes of mainboard_container.
     * 
     ***/
    
     private def void setAttributes(MainboardContainer mainboard_container){
     	
     	val referencedMB = mainboard_container.getReferencedMB() as Mainboard
     	
     	mainboard_container.setName(referencedMB.getName())
        mainboard_container.setSocket(referencedMB.getSocket().toString())     	 
        mainboard_container.setChipset(referencedMB.getChipset())
    	mainboard_container.setTypeMemorySlots(referencedMB.typeMemorySlots.toString())
    	mainboard_container.setNumMemorySlots(referencedMB.getNumMemorySlots())
    	mainboard_container.setNumPCIe16Slots(referencedMB.getNumPCIe16Slots())
    	mainboard_container.setNumSataPorts(referencedMB.getNumSataPorts())
       	mainboard_container.setPowerConsumption(referencedMB.getPowerConsumption())
   	   	mainboard_container.setFormFactor(referencedMB.getFormFactor().toString())
   	    mainboard_container.setPrice(referencedMB.getPrice())
     	
     }
   }