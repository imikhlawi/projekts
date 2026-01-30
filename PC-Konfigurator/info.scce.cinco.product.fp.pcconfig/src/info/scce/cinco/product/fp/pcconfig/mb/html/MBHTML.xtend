package info.scce.cinco.product.fp.pcconfig.mb.html

import de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass
import de.jabc.cinco.meta.plugin.generator.runtime.IGenerator
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.Mainboard
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.CPUNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.GPUNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.RAMNode
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.MainboardContainer
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.IProgressMonitor
import de.jabc.cinco.meta.core.utils.EclipseFileUtils
import java.util.List

class MBHTML extends CincoRuntimeBaseClass implements IGenerator<Mainboard> { 
	
   String fileName
   float totalPrice = 0.0f
		    
   override generate(Mainboard model, IPath path, IProgressMonitor arg2) {
 	    val fullFileName = model.file.name
 	    fileName = fullFileName.substring(0, fullFileName.lastIndexOf('.'))
        val targetFile = workspaceRoot.getFileForLocation(path.append(fileName + ".html"))
 	    EclipseFileUtils.writeToFile(targetFile, template(model))
	}
	
   private def template(Mainboard model){
		
		for (mbContainer: model.getMainboardContainers()){
		 
		    val mbcontainer = mbContainer
		    val cpus = mbcontainer.getCPUSlots().get(0).getCPUNodes()
		    val gpus = mbcontainer.getGPUSlots().get(0).getGPUNodes() 		
		    val rams = mbcontainer.getRAMSlots().map[
		    	
		    	if (!it.getRAMNodes().isEmpty()){
		    		it.getRAMNodes().get(0)
		    	}
		    ]
		 
	      	if (cpus.isEmpty() || gpus.isEmpty() || rams.isEmpty()){
         	throw new IllegalArgumentException("Please finish Modeling Mainboard and install a CPU, GPU and at least one RAM!") }		
        	 
            calculate_totalPrice(mbContainer)
            
	        return to_html(mbcontainer)
		} 
		
		return  '''
		
		«html_head»
		<body>
		<h1 id="page-title">Mainboard Model Details</h1>
		</body>
		</html> 
		 
		'''
	 }

   private def to_html(MainboardContainer mbContainer){
   		
   		val cpu = mbContainer.getCPUSlots().get(0).getCPUNodes().get(0)
	    val gpu = mbContainer.getGPUSlots().get(0).getGPUNodes().get(0) 		
	    val rams = mbContainer.getRAMSlots().map[
			if (!it.getRAMNodes().isEmpty()){
	    		it.getRAMNodes().get(0)
	    	}else{
	    		null
	    	}
	     ]
   		
		return '''
		
		«html_head»
		
		<body>
		
		 <h1 id="page-title">Mainboard Model Details</h1>
		
		 <div id="total-price-container">
		       <h2>Total price</h2>
		       <p id="total-price"> «totalPrice»</p>
		  </div>       
		«mainboard_to_html(mbContainer,cpu,gpu,rams)»
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
   	
   private def calculate_totalPrice(MainboardContainer mbContainer){
	    
	    totalPrice = 0
						
		// Main board price
		totalPrice += mbContainer.price
		
		// CPU  price
		mbContainer.getCPUSlots().get(0).getCPUNodes().forEach[totalPrice = totalPrice + it.powerConsumption]
		
		// GPU price
		mbContainer.getGPUSlots().get(0).getGPUNodes().forEach[totalPrice = totalPrice + it.price]
		
		// RAMs prices
		mbContainer.getRAMSlots().forEach[ if (!it.getRAMNodes().isEmpty()) {
			totalPrice = totalPrice + it.getRAMNodes().get(0).price
	    }]
				    
	}
	
}