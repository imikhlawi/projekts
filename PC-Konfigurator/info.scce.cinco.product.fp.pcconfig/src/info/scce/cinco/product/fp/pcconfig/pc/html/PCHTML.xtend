package info.scce.cinco.product.fp.pcconfig.pc.html
import de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass
import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PC
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.CaseContainer
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.MainboardNode
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.PSUNode
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.DriveNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.CPUNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.GPUNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.RAMNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.MainboardContainer
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.IProgressMonitor
import de.jabc.cinco.meta.core.utils.EclipseFileUtils
import java.util.List

class PCHTML extends CincoRuntimeBaseClass implements IGenerator<PC> { 
	
	String fileName
    float totalPrice = 0.0f
		    
    override generate(PC model, IPath path, IProgressMonitor arg2) {
 	    val fullFileName = model.file.name
 	    fileName = fullFileName.substring(0, fullFileName.lastIndexOf('.'))
        val targetFile = workspaceRoot.getFileForLocation(path.append(fileName + ".html"))
 	    EclipseFileUtils.writeToFile(targetFile, template(model))
 	    
	}
	
	private def calculate_totalPrice(CaseContainer caseContainer){
	    
	    totalPrice = 0
		
		val mbContaniner = caseContainer.getPCMainboardContainers().get(0)
	
		for (mainBoard: mbContaniner.getMainboardNodes()){
					
			// 1. retrieve referenced Main board graph model 
			val referencedMBModel = mainBoard.getReferencedMB()
			val referencedMBContainer = referencedMBModel.getMainboardContainers().get(0)
			
			// Main board price
			totalPrice += referencedMBContainer.price
			
			// Case price
			totalPrice += caseContainer.casePrice
			
			// PSU price
			caseContainer.getPSUContainers().get(0).getPSUNodes().forEach[
			totalPrice += it.psuPrice	
			]
			
			// CPU  price
			referencedMBContainer.getCPUSlots().get(0).getCPUNodes().forEach[totalPrice = totalPrice + it.price]
			
			// GPU price
			referencedMBContainer.getGPUSlots().get(0).getGPUNodes().forEach[totalPrice = totalPrice + it.price]
			
			// RAMs prices
			referencedMBContainer.getRAMSlots().forEach[ if (!it.getRAMNodes().isEmpty()) {
				totalPrice = totalPrice + it.getRAMNodes().get(0).price
			 }]
			 
			 // Drives prices
			caseContainer.getDriveContainers().get(0).getDriveNodes().forEach[{
				totalPrice = totalPrice + it.drivePrice
			}]
			
		}
	    
	}
	    			
	private def template(PC model){
		
		 for (caseContainer: model.getCaseContainers()){

		 val referencedMBContainer = caseContainer.getPCMainboardContainers()
		 
		 if (referencedMBContainer.get(0).getMainboardNodes().isEmpty()){
         	throw new IllegalArgumentException("Please finish Modeling PC and install Main board model.") }		
        	 
         val mainboardNode = referencedMBContainer.get(0).getMainboardNodes().get(0)
         calculate_totalPrice(caseContainer)
	     return to_html(caseContainer,mainboardNode)
	     			
		} 
		
		return  '''
		
		«html_head»
		<body>
		<h1 id="page-title">PC Model Details</h1>
		</body>
		</html> 
		 
		'''
	 }

	private def to_html(CaseContainer caseContainer, MainboardNode mainboardNode ){
		
		val referencedMBModel = mainboardNode.getReferencedMB()
		val referencedMBContainer = referencedMBModel.getMainboardContainers().get(0)
	    val cpu = referencedMBContainer.getCPUSlots().get(0).getCPUNodes().get(0)
	    val gpu = referencedMBContainer.getGPUSlots().get(0).getGPUNodes().get(0) 		
	    val rams = referencedMBContainer.getRAMSlots().map[
	  
	    	if (!it.getRAMNodes().isEmpty()){
	    	   it.getRAMNodes().get(0)
	         }else{
	            null	
	         }
	  	  ]
		
		
		val drives = caseContainer.getDriveContainers().get(0).getDriveNodes()
		
		var PSUNode psu = null
		if (!caseContainer.getPSUContainers().get(0).getPSUNodes().isEmpty()){
			psu = caseContainer.getPSUContainers().get(0).getPSUNodes().get(0)
		}
			
		return '''
		
		«html_head»
		
		<body>
		
		 <h1 id="page-title">PC Model Details</h1>
		
		 <div id="total-price-container">
		       <h2>Total price</h2>
		       <p id="total-price"> «totalPrice»</p>
		  </div>       
		«mainboard_to_html(referencedMBContainer,cpu,gpu,rams)»
	    «pc_to_html(caseContainer,psu,drives)»
		</body>
		</html> 
		
		'''			
	}
	
	private def String html_head(){
		
		return '''
		
		<!DOCTYPE html>
		<html>
		<head>
		<title>Mainboard Details</title>
		    
		 <style>
		    
		    body {
		        font-family: Arial, sans-serif;
		        margin: 0;
		        padding: 0;
		        background-color: #f4f4f4;
		        color: #333;
		    }
		    
		    .component {
		        background-color: #fff;
		        border: 1px solid #ddd;
		        border-radius: 4px;
		        padding: 20px;
		        margin: 20px;
		        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
		    }
		    
		    .component h2 {
		        color: #444;
		    }
		    
		    .component ul {
		        list-style-type: none;
		        padding: 0;
		    }
		    
		    .component li {
		        margin-bottom: 10px;
		    }
		    
						
			table {
			    width: 100%;
			    border-collapse: collapse;
			    margin-bottom: 20px;
			    border: 2px solid #4D4D4D;
			}
			
			th, td {
			    padding: 8px;
			    text-align: left;
			    border-bottom: 1px solid #4D4D4D; 
			}
			
			th {
			    background-color: #4CAF50;
			    color: white;
			}
			
			tr:nth-child(even) {
			    background-color: #f2f2f2;
			}
			
			tr:hover {
			    background-color: #ddd;
			}
			
			td, th {
			    border-left: 1px solid #4D4D4D;
			}
			
			th:first-child, td:first-child {
			    border-left: none;
			}
			
			#page-title {
			    text-align: center;
			    color: #2F4F4F; 
			    font-size: 36px; 
			    margin-bottom: 30px; 
			    padding: 10px;
			    border-bottom: 2px solid #4D4D4D; 
			    max-width: 70%;
			    margin-left: auto;
			    margin-right: auto;
			    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
			}
			
			
			#total-price-container {
			    text-align: center;
			    background-color: #ffffff; /* Weißer Hintergrund für Kontrast */
			    padding: 20px;
			    margin-top: 30px;
			    border-radius: 10px; /* Abgerundete Ecken */
			    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); /* Schatten für Tiefe */
			    width: 50%;
			    margin-left: auto;
			    margin-right: auto;
			}
			
			#total-price-container h2 {
			    margin: 0;
			    color: #2F4F4F;
			    font-size: 24px;
			}
			
			#total-price {
			    color: #E85D04; /* Lebendige Farbe für den Preis */
			    font-size: 23px;
			    font-weight: bold;
			    margin-top: 20px;
			}
		 </style>
		    
		</head>
		
		'''
		
	}
	
	private def String mainboard_to_html(MainboardContainer mbcontainer,CPUNode cpu, GPUNode gpu, List<RAMNode> rams){
		
		return '''	
		
		    <div class="component">
		        <h2>Mainboard</h2>
		         <table> 
		            «html_mainboard(mbcontainer)»
		         </table> 
		    </div>
			
		    <div class="component">
		        <h2>CPU</h2>
		         <table> 
		            «html_cpu(cpu)»
		         </table> 
		    </div>
		    
		    <div class="component">
		        <h2>GPU</h2>
		         <table> 
		            «html_gpu(gpu)»
		         </table> 
		    </div>
		    
		    
		    <div class="component">
		        <h2>RAMs</h2>
		
		«FOR ram : rams»
		    «IF ram != null»     
		    <div class="component">
		       <h3>RAM</h3>
		       	 <table>«html_ram(ram)»</table>		          	 
		    </div>
		    «ENDIF»     
	    «ENDFOR»
		</div>'''	
	}
	
	private def String pc_to_html(CaseContainer caseContainer,PSUNode psu, List<DriveNode> driveNodes){
		
		return '''		
		    <div class="component">
		        <h2>Case</h2>
		         <table> 
		            «html_case(caseContainer)» 
		         </table> 
		    </div>
		    
		    <div class="component">
		        <h2>PSU</h2>
		         <table> 
		            «html_psu(psu)»
		         </table> 
		    </div>
		    
		    
		    <div class="component">
		        <h2>Drives</h2>
		
		«FOR drive : driveNodes»
		    
		    <div class="component">
		       <h3>Hard Drive</h3>
		       	 <table>«html_drive(drive)»</table>		          	 
		    </div>
		    
	    «ENDFOR»
		</div>'''	
	}
	
	private def html_mainboard(MainboardContainer mbContainer){
							   
		return '''
	        <tr><td>Name </td><td>«mbContainer.name»</td></tr>
	        <tr><td>Socket </td><td>«mbContainer.socket»</td></tr>
	        <tr><td>Chipset: </td><td>«mbContainer.chipset»</td></tr>
	        <tr><td>Type Memory Slots </td><td>«mbContainer.typeMemorySlots»</td></tr>
	        <tr><td>Number of Memory Slots </td><td>«mbContainer.numMemorySlots»</td></tr>
	        <tr><td>Number of PCIe x16 Slots </td><td>«mbContainer.numPCIe16Slots»</td></tr>
	        <tr><td>Number of Sata Ports </td><td>«mbContainer.numSataPorts»</td></tr>
	        <tr><td>Power consumption </td><td>«mbContainer.powerConsumption» Watt</td></tr>
	        <tr><td>Factor </td><td>«mbContainer.formFactor»</td></tr>
	        <tr><td>price: </td><td>«mbContainer.price» Euro</td></tr>
		'''
		
	}
	
	private def html_cpu(CPUNode cpu ){
		    
		return '''
	       <tr><td> Name </td><td>«cpu.name»</td></tr>
	       <tr><td> Type </td><td>«cpu.cpuType»</td></tr>
	       <tr><td> Power Consumption </td><td>«cpu.powerConsumption» Watt</td></tr>
	       <tr><td> Socket </td><td>«cpu.socket»</td></tr>
	       <tr><td> price </td><td>«cpu.price» Euro</td></tr>
		'''
	}	
	
	private def html_gpu(GPUNode gpu ){
	 		  	
	  	return '''
	  	     <tr><td> Name </td><td>«gpu.name»</td></tr>
	  	     <tr><td> Chip </td><td>«gpu.chip»</td></tr>
	  	     <tr><td> Memory </td><td>«gpu.memory» GB</td></tr>
	  	     <tr><td> Power consumption </td><td>«gpu.powerConsumption» Watt</td></tr>
	  	     <tr><td> Price </td><td>«gpu.price» Euro</td></tr>
	  	'''
	 }	
	 
	private def html_ram(RAMNode ram ){
	     	         
	 return '''
	   <tr> <td> Name </td> <td> «ram.name»</td></tr>
	   <tr> <td> Type </td><td>«ram.ramType»</td></tr>
	   <tr> <td> Capacity </td><td>«ram.capacity» GB</td></tr>
	   <tr> <td>Power consumption </td> <td>«ram.powerConsumption» Watt</td></tr>
	   <tr> <td> Price </td> <td>«ram.price» Euro</td></tr>
	  '''	     
	 }
	 	 
	private def html_case(CaseContainer caseContainer ){
	     	         
	  return '''
	   <tr> <td> Name </td> <td> «caseContainer.caseName»</td></tr>
	   <tr> <td> Type </td><td>«caseContainer.caseFormFactor»</td></tr>
	   <tr> <td> Internal Slots </td><td>«caseContainer.caseInternalSlots»</td></tr>
	   <tr> <td> External Slots </td><td>«caseContainer.caseExternalSlots»</td></tr>
	   <tr> <td> Price </td> <td>«caseContainer.casePrice» Euro</td></tr>
	  '''	     
	 }
	  
	private def html_psu(PSUNode psu ){
	     	         
	 return '''
	   <tr> <td> Name </td> <td> «psu.psuName»</td></tr>
	   <tr> <td> Power </td><td>«psu.psuPower» Watt</td></tr>
	   <tr> <td> Price </td><td>«psu.psuPrice» Euro</td></tr>
	  '''	     
	 }
	
	private def html_drive(DriveNode drive ){
	     	         
	 return '''
	   <tr> <td> Name </td> <td> «drive.driveName»</td></tr>
	   <tr> <td> Type </td><td>«drive.driveType»</td></tr>
	   <tr> <td> Description </td><td>«drive.driveDescription»</td></tr>
	   <tr> <td> Power consumption </td> <td>«drive.drivePowerConsumption» Watt</td></tr>
	   <tr> <td> Price </td> <td>«drive.drivePrice» Euro</td></tr>
	  '''	     
	 }
}