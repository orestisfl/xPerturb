package perturbation.rmi;

import org.junit.Test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.Assert.assertEquals;

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
		assertEquals(21,server.getLocations().size());
		//Stop rmi service
		server.stopService();
	}
}
