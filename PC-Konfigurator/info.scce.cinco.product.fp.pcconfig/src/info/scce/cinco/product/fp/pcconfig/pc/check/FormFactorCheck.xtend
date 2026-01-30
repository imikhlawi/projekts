package info.scce.cinco.product.fp.pcconfig.pc.check

import info.scce.cinco.product.fp.pcconfig.pc.mgl.mcam.modules.checks.PCCheck
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PC

class FormFactorCheck extends PCCheck{
	
	override check(PC model) {
		
		val caseContianers = model.getCaseContainers()
		for (caseContainer:caseContianers){
			
		val caseContainerFormFactor = caseContainer.caseFormFactor
		val mbContaniner = caseContainer.getPCMainboardContainers().get(0)
		mbContaniner.getMainboardNodes().forEach[
			val referencedMBModel = it.getReferencedMB()
			val referencedMBContainer = referencedMBModel.getMainboardContainers().get(0)
			val mainBoardFormFactor = referencedMBContainer.formFactor
			
			if (!caseContainerFormFactor.equals(mainBoardFormFactor)){
			     	it.addError("Main board size does not pass to the case size!")
		    	}
		    ]			
		}
	}
}