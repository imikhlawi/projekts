package info.scce.cinco.fp.compgen.action

import java.io.ByteArrayInputStream
import org.eclipse.core.resources.IFile
import org.eclipse.jface.action.IAction
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.IActionDelegate

abstract class GeneratorAction implements IActionDelegate {
	
	IFile selectedFile
	
	override run(IAction action) {
		try {
			selectedFile?.write(generate)
		} catch(Exception e) {
			e.printStackTrace
			MessageDialog.openError(Display.current.activeShell, 
				"Error", "Generation failed. Check the console for details.")
		}
		MessageDialog.openInformation(Display.current.activeShell, 
			"Success", "Components successfully generated.")
	}
	
	abstract def String generate()
	
	def write(IFile file, String content) {
		val stream = new ByteArrayInputStream(content.getBytes(file.charset))
		if (file.exists) file.setContents(stream, true, true, null)
		else file.create(stream, true, null)
		stream.close
	}
	
	override selectionChanged(IAction action, ISelection selection) {
		selectedFile = switch selection {
			IStructuredSelection:
				selection.getFirstElement() as IFile
		}
	}
}