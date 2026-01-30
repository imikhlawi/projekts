package info.scce.cinco.product.fp.pcconfig.pc.check

import info.scce.cinco.product.fp.pcconfig.pc.mgl.mcam.modules.checks.PCCheck
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PC

class NumberOfDriveNodesCheck extends PCCheck {
	
	override check(PC model) {
		
		val caseContianers = model.getCaseContainers()
		for (caseContainer:caseContianers){
			
			val mbContaniner = caseContainer.getPCMainboardContainers().get(0)
			val numOfInstalledDrives = caseContainer.getDriveContainers().get(0).getDriveNodes().size()
			
				for (mainBoard: mbContaniner.getMainboardNodes()){
					
					// 1. retrieve referenced Main-board graph model 
					val referencedMBModel = mainBoard.getReferencedMB()
					val referencedMBContainer = referencedMBModel.getMainboardContainers().get(0)
					val numSATASLots = referencedMBContainer.getNumSataPorts()
					
					if (numOfInstalledDrives > numSATASLots){
						val driveContainer = caseContainer.getDriveContainers().get(0)
						driveContainer.addError("Maximum number of SATA Slots is " + numSATASLots)
			 	    }			 	    
			 	    
			}
		}
	}
	
	
	
}