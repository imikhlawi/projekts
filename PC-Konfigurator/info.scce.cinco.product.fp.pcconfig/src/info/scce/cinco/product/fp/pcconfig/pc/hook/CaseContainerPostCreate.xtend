package info.scce.cinco.product.fp.pcconfig.pc.hook
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.CaseContainer
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.fp.compdsl.componentDsl.Case

class CaseContainerPostCreate extends CincoPostCreateHook<CaseContainer> {
	
	/***
	 * Post Create Hook for CaseContainer.
	 * Create PSU, Main board and Drive slots. 
	 * @param case_container: new created CaseContainer. 
	 ***/
	override postCreate (CaseContainer case_container) {
		
		case_container.resize(850,760)
		// 1. Create PSU Container in the PC model
		val psu = case_container.newPSUContainer(50,30)
		psu.resize(770,150)	
		
		// 2. Create Main board Container in the PC model
		val mainboardContainer = case_container.newPCMainboardContainer(50,200)
		mainboardContainer.resize(500,500)	
		
		// 3. Create Drive Container  in the PC model. 
		val driveContainer = case_container.newDriveContainer(570,200)
		driveContainer.resize(250,500)	
		
		
		// set attributes
		setAttributes(case_container)
		
							  
    } 	
    
        /***
     * @param drive_node: set attributes of drive_node.
     * 
     ***/
    private def void setAttributes(CaseContainer case_container){
    		
		val referencedCase = case_container.getReferencedCase() as Case
		
		case_container.setCaseName(referencedCase.name)
		case_container.setCaseFormFactor(referencedCase.formFactor.toString())
		case_container.setCaseInternalSlots(referencedCase.internalSlots)
	    case_container.setCaseExternalSlots(referencedCase.externalSlots)
	    case_container.setCasePrice(referencedCase.price)
    	
    }
    
    
}