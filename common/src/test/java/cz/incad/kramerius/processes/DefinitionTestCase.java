package cz.incad.kramerius.processes;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import cz.incad.kramerius.AbstractGuiceTestCase;
import cz.incad.kramerius.processes.DefinitionManager;
import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessManager;
import cz.incad.kramerius.processes.States;


public class DefinitionTestCase extends AbstractGuiceTestCase {

	@Before
	public void doBefore() {
		dropTables();
	}
	
	@After
	public void doAfter() {
		dropTables();
	}

	
	@Test
	public void testDefinition() throws InterruptedException {
		Injector inj = injector();
		DefinitionManager defMgr = inj.getInstance(DefinitionManager.class);
		
		LRProcessDefinition definition = defMgr.getLongRunningProcessDefinition("generovani_pdf");
		TestCase.assertNotNull(definition);
		
		LRProcess process = definition.createNewProcess();
		process.startMe(false, "","");

		Thread.sleep(2000);

		LRProcessManager instmgr = inj.getInstance(LRProcessManager.class);
		LRProcess lrProcess = instmgr.getLongRunningProcess(process.getUUID());
		Assert.assertTrue(instmgr.getLongRunningProcesses().size() == 1);
		Assert.assertNotNull(lrProcess);
		// kill process
		lrProcess.stopMe();
		
		// zjisti, jaky je stav po killovani
		LRProcess lrProcessAfterKill = instmgr.getLongRunningProcess(process.getUUID());
		Assert.assertTrue(instmgr.getLongRunningProcesses().size() == 1);
		Assert.assertNotNull(lrProcessAfterKill);
		Assert.assertTrue(lrProcessAfterKill.getProcessState().equals(States.KILLED));

	}
	
	protected Injector injector() {
		Injector injector = Guice.createInjector(new DefinitionModule());
		return injector;
	}


}
