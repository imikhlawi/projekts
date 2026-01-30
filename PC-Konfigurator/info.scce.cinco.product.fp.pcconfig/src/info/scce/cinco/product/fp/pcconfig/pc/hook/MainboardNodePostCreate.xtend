package info.scce.cinco.product.fp.pcconfig.pc.hook
import info.scce.cinco.product.fp.pcconfig.pc.mgl.pc.MainboardNode
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook
import info.scce.cinco.product.fp.pcconfig.mb.mgl.mainboard.MainboardContainer

class MainboardNodePostCreate extends CincoPostCreateHook<MainboardNode> {
	
	/* Used to know if there is at least one RAM installed on the Main board model  */
	boolean containRams
	
	/***
	 * Post Create Hook. 
	 * checks if the referenced Main board Graph Model contains CPU, GPU and at least one RAM.
	 * It creates then a CPU, GPU and RAM nodes and add them to the [MainboardNode] in the PC Graph Model.
	 * 
	 * @param mainboardNode: new created MainboardNode in the PC Model 
	 * @throws IllegalArgumentException when the chosen Main board model does not contain 
	 *  a CPU or GPU or at least on RAM
	 ***/
	
	override postCreate(MainboardNode mainboardNode) {
	    
	    // getReferencedMB() returns the Main board graphModel.	    	
    	val referencedMBContainers = mainboardNode.getReferencedMB().getMainboardContainers()
    	
    	// Check if the graph model is not Empty. 
    	if (!referencedMBContainers.isEmpty()){
    		
          val cpuSlot = referencedMBContainers.get(0).getCPUSlots().get(0)
          val gpuSlot = referencedMBContainers.get(0).getGPUSlots().get(0)
          val ramSlots = referencedMBContainers.get(0).getRAMSlots()
          
          // Check if the there is at least on RAM installed on the Main board
          containRams = false
          val  rams = ramSlots.map[!it.getRAMNodes().isEmpty()]
          rams.forEach[if (it){ this.containRams = true}]
          
          if (cpuSlot.getCPUNodes().isEmpty() || gpuSlot.getGPUNodes().isEmpty() || !containRams){
          	throw new IllegalArgumentException("Please finish Modeling the Main board.\n Install CPU, GPU and RAMs")
          }
          
          	 // Change position and resize the installed Main board
			 mainboardNode.x = 5
	    	 mainboardNode.y = 5	
             mainboardNode.resize(490,490)
    	     mainboardNode.setName(referencedMBContainers.get(0).name)
    	     
    	     changeLyaout(mainboardNode)
    	   
    	      // Create Main board components as in the referenced Main board model. CPU,GPU,Rams
              createComponentNodes(mainboardNode,referencedMBContainers.get(0))
            
            return
            
      	}
      	
      	 	// throw IllegalArgumentException when no Main board created is
      	    throw new IllegalArgumentException("Please finish Modeling the Main board.")
      	
    }
  
    // Change location and size of the drive and PSU Container based on number of SATA Slots
    private def void changeLyaout(MainboardNode mainboardNode){
    	
    	val referencedMBContainer = mainboardNode.getReferencedMB().getMainboardContainers().get(0)
    	val numSATAPorts = referencedMBContainer.getNumSataPorts()
    	val DriveContainer = mainboardNode.getContainer().getContainer().getDriveContainers().get(0)
    	val PSUContainer = mainboardNode.getContainer().getContainer().getPSUContainers().get(0)
    	            	
    	if (numSATAPorts < 5){
    		PSUContainer.x = 50
    		PSUContainer.y = 30
    		PSUContainer.resize(770,150)
    		DriveContainer.x = 570
    		DriveContainer.y = 200
    		DriveContainer.resize(250,500)	
    		              					  	
    	}else{
    		
    		PSUContainer.x = 50
    		PSUContainer.y = 30
    		PSUContainer.resize(500,150)
    	    DriveContainer.resize(250,670)				  
    		DriveContainer.x = 570
    		DriveContainer.y = 30      
        }
       
         // Resize the PSUNode in the PSUContainer based on the size of the PSUContainer	
         for (PSUNode: PSUContainer.getPSUNodes()){
         	PSUNode.resize(PSUContainer.width-20,PSUContainer.height-20)         	
       }
    }
    
    
    /***
     * Create Main board components as in the referenced Main board model. CPU,GPU,Rams
     * @param mainboardNode: is a container in the PC Model. 
     * @param referencedMainboardContainer: refers to the Main board container in the Main board model. 
     ***/
    
    private def void createComponentNodes(MainboardNode mainboardNode, MainboardContainer referencedMainboardContainer){
    		
    	// 1. Create CPUNode as in the referenced MB model
    	val referencedCPUSlot = referencedMainboardContainer.getCPUSlots().get(0)
       	val cpuNode = mainboardNode.newPCCPUNode(referencedCPUSlot.x,referencedCPUSlot.y) // new CPU Node in the PC model
    	cpuNode.resize(referencedCPUSlot.width,referencedCPUSlot.height)
    	cpuNode.setName(referencedCPUSlot.getCPUNodes().get(0).name)
    	
    	//2. Create GPUNode as in the referenced MB model
    	val referencedGPUSlot = referencedMainboardContainer.getGPUSlots().get(0)
       	val gpuNode = mainboardNode.newPCGPUNode(referencedGPUSlot.x,referencedGPUSlot.y) // new GPU Node in the PC model
    	gpuNode.resize(referencedGPUSlot.width,referencedGPUSlot.height)
    	gpuNode.setName(referencedGPUSlot.getGPUNodes().get(0).name)
    	
    	
    	//3. Create Rams as in the referenced MB model
    	val referencedRAMSlots = referencedMainboardContainer.getRAMSlots()
    	val referencedRAMs = referencedRAMSlots.map[if (!it.getRAMNodes().isEmpty()) {it.getRAMNodes().get(0)}]
    	 
    	 var index = 0;
    	 for (referencedRAM:referencedRAMs){

    	     val referencedRAMSlot = referencedRAMSlots.get(index)
    	   
    	     if (!referencedRAMSlot.getRAMNodes().isEmpty()){
    	     
    	      val ramNode = mainboardNode.newPCRAMNode(referencedRAMSlot.x,referencedRAMSlot.y) // new RAM Node in the PC model
    	      ramNode.resize(referencedRAMSlot.width,referencedRAMSlot.height)    
    	      	
    	     }else{
    	       
    	       val emptyRAMSlot = mainboardNode.newPCRAMSlot(referencedRAMSlot.x,referencedRAMSlot.y) // new RAM Node in the PC model
    	       emptyRAMSlot.resize(referencedRAMSlot.width,referencedRAMSlot.height) 
    	       emptyRAMSlot.setSlotType(referencedMainboardContainer.getTypeMemorySlots())   		
    	       
    	     }
    	     
    	     index = index + 1  
    	  }	  
      }   
 }