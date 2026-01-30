package info.scce.cinco.product.fp.pcconfig.mb.check
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mcam.modules.checks.MainboardCheck
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.Mainboard

class RAMTypeCheck extends MainboardCheck   {
	
	override check(Mainboard model) {
		
		
		// check if all RAMS installed on the Main-board have the same RAMType as the Main board RAMType 
		
		val mbContainers = model.getMainboardContainers()
		for (mbContainer : mbContainers){

          val mainboardRamType = mbContainer.getTypeMemorySlots		
		  val RAMs = mbContainer.getRAMSlots().map[if (!it.getRAMNodes().isEmpty()){
		     it.getRAMNodes().get(0)
		  }else{
		     	null
		  }]	
		  
		  RAMs.forEach[if (it != null && it.ramType != mainboardRamType){
		    	it.addError("This RAM has not the same RAM type of the Main-board!")
		  }]
			
		}
	}
	
}