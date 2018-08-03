package net.globulus.simisync;

import net.globulus.simi.ActiveSimi;
import net.globulus.simi.api.SimiValue;
import net.globulus.simi.spring.ResourceLoaderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

@SpringBootApplication
public class SimiSyncApplication {

	@Autowired
	private static ResourceLoader resourceLoader;

	public static void main(String[] args) {
		ResourceLoaderWrapper resourceLoaderWrapper = new ResourceLoaderWrapper(resourceLoader);
		ActiveSimi.setImportResolver(new ActiveSimi.ImportResolver() {
			@Override
			public String readFile(String s) {
				try {
					return StreamUtils.copyToString(new ClassPathResource("static/" + s).getInputStream(), Charset.defaultCharset());
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public URL resolve(String s) {
				try {
					return new ClassPathResource("static/" + s).getURL();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		try {
			ActiveSimi.load("SimiSyncModels.simi", "SimiSyncControllers.simi");
			ActiveSimi.eval("SimiSyncControllers.Router", "parseControllers",
					new SimiValue.Object(resourceLoaderWrapper));
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpringApplication.run(SimiSyncApplication.class, args);
	}
}
