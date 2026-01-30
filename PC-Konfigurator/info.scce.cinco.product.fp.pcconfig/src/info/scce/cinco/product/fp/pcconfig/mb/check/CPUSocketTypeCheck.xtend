package info.scce.cinco.product.fp.pcconfig.mb.check

import info.scce.cinco.product.fp.pcconfig.mb.mgl.mcam.modules.checks.MainboardCheck
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.Mainboard
import info.scce.cinco.fp.compdsl.componentDsl.Cpu

class CPUSocketTypeCheck extends  MainboardCheck{
	
   override check(Mainboard model) {
			
	  for (mbcontainer: model.getMainboardContainers()){
	  	
	  	val socket = mbcontainer.getCPUSlots().get(0).getSocket()
	  	for (cpu : mbcontainer.getCPUSlots().get(0).getCPUNodes()){
	  		
	  	    val referencedCPU = cpu.getReferencedCPU() as Cpu
	  		if (!referencedCPU.socket.toString().equals(socket)){
	  			cpu.addError("The socket does not pass to this CPU")
	  	  }
	  	}
	  }	
	
	}	
}