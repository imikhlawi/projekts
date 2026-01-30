package info.scce.cinco.product.fp.pcconfig.pc.check

import info.scce.cinco.product.fp.pcconfig.pc.mgl.mcam.modules.checks.PCCheck
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PC
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.CaseContainer


class PowerConsumptionCheck  extends PCCheck{

	override check(PC model) {
		
		val caseContianers = model.getCaseContainers()
	
		for (caseContainer:caseContianers){
			
			val psuContainer = caseContainer.getPSUContainers().get(0)
			
			for (psuNode: psuContainer.getPSUNodes()){
				val power = psuNode.psuPower
				val compnentsPowerConsumption = calculatePCPowerConsumption(caseContainer)
				
				val requieredPower = 1.25 * compnentsPowerConsumption
				
				if (power < requieredPower){
					psuNode.addError("Power of this PSU does not satisfy the power requirement for this PC")
				}
			}	
		}
 	}
	
	
	var powerConsumption = 0

	private def int calculatePCPowerConsumption(CaseContainer caseContainer){
		
		 powerConsumption = 0
		
		val mbContaniner = caseContainer.getPCMainboardContainers().get(0)
	
		for (mainBoard: mbContaniner.getMainboardNodes()){
					
			// 1. retrieve referenced Main board graph model 
			val referencedMBModel = mainBoard.getReferencedMB()
			val referencedMBContainer = referencedMBModel.getMainboardContainers().get(0)
			
			// Main board powerConsumption
			powerConsumption += referencedMBContainer.powerConsumption
			
			// CPU  powerConsumption
			referencedMBContainer.getCPUSlots().get(0).getCPUNodes().forEach[powerConsumption = powerConsumption + it.powerConsumption]
			
			// GPU powerConsumption
			referencedMBContainer.getGPUSlots().get(0).getGPUNodes().forEach[powerConsumption = powerConsumption + it.powerConsumption]
			
			// RAMs powerConsumption
			referencedMBContainer.getRAMSlots().forEach[ if (!it.getRAMNodes().isEmpty()) {
				powerConsumption = powerConsumption + it.getRAMNodes().get(0).powerConsumption
			 }]
			 
			 // Drives powerConsumption
			caseContainer.getDriveContainers().get(0).getDriveNodes().forEach[{
				powerConsumption = powerConsumption + it.drivePowerConsumption
			}]
			
		}
		
		
		return powerConsumption
		
	}
	
}