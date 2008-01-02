package br.org.archimedes.gui.rca.importer;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import br.org.archimedes.gui.actions.Messages;

public class ImportWizard extends Wizard implements IExportWizard {

	@Override
	public boolean performFinish() {
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.ImportDrawing_Title);
		this.setForcePreviousAndNextButtons(true);
	}
	
	/**
	 * Adds the pages we need for this ImportWizard.
	 * 
	 * Here we will only add a single page that lists all 
	 * plugins that are linked to the importWizard extension point.
	 */
	@Override
	public void addPages() {
		this.addPage(new ImportWizardPage());		
		super.addPages();
	}
}
