package perturbation.rmi;

import org.junit.Test;
import perturbation.enactor.AlwaysEnactorImpl;
import perturbation.enactor.NeverEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.rmi.resources.BinOpRes;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by bdanglot on 11/08/16.
 */
public class TestPerturbationServer {

	@Test
	public void testPerturbationServer() throws Exception {
		PerturbationServerImpl.startServer("src/test/java/perturbation/rmi/resources/", "perturbation.rmi.resources");
		Thread.sleep(500);//Must wait the the server is effectively started
		//test rmi service
		Registry registry = LocateRegistry.getRegistry(PerturbationServerImpl.PORT);
		PerturbationServer server = (PerturbationServer) registry.lookup(PerturbationServerImpl.NAME_SERVER);

		List<PerturbationLocation> locations = server.getLocations();
		assertEquals(21, locations.size());
		for (PerturbationLocation location : locations) {
			location = server.enableLocation(location);
			assertTrue(location.getEnactor() instanceof AlwaysEnactorImpl);
			location = server.disableLocation(location);
			assertTrue(location.getEnactor() instanceof NeverEnactorImpl);
		}

		BinOpRes op = new BinOpRes();
		//and method
		//perturbation free
		assertEquals(true, op.and(true, true));
		assertEquals(false, op.and(true, false));

		//The location 2 is on the result of the computation
		PerturbationLocation location = server.enableLocation(locations.get(2));
		assertTrue(location.getEnactor() instanceof AlwaysEnactorImpl);

		assertEquals(location, server.logOn(location));

		//perturbation execution
		assertEquals(false, op.and(true, true));
		assertEquals(1, server.getCalls(location));
		assertEquals(1, server.getEnactions(location));
		assertEquals(true, op.and(true, false));
		assertEquals(2, server.getCalls(location));
		assertEquals(2, server.getEnactions(location));

		location = server.disableLocation(location);
		assertTrue(location.getEnactor() instanceof NeverEnactorImpl);

		assertEquals(location, server.stopLogOn(location));

		try {
			server.getCalls(location);
			fail();
		} catch (NullPointerException expected) {

		}

		try {
			server.getEnactions(location);
			fail();
		} catch (NullPointerException expected) {

		}

		//perturbation free
		assertEquals(true, op.and(true, true));
		assertEquals(false, op.and(true, false));

		//Stop rmi service
		server.stopService();
	}
}
