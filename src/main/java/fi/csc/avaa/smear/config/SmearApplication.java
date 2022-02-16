package fi.csc.avaa.smear.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "SMEAR API",
                version = "1.1.0",
                contact = @Contact(
                        name = "SMEAR API Support",
                        email = "servicedesk@csc.fi"),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class SmearApplication extends Application {
}
