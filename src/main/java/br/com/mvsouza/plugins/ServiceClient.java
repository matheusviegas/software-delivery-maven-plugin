package br.com.mvsouza.plugins;

import br.com.mvsouza.plugins.beansws.ProjectContainer;
import br.com.mvsouza.plugins.beansws.Project;
import java.io.File;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

/**
 *
 * @author matheus
 */
public class ServiceClient {

    private final Client client;
    private final String BASE_URL = "https://ci.mvsouza.com.br/api";

    public ServiceClient(ReleaseConfig releaseConfig) {
        Configuration clientConfig = new ClientConfig(MultiPartWriter.class).register(new AuthenticationFilter(releaseConfig.getApiKey()));
        this.client = ClientBuilder.newClient(clientConfig);
    }

    public Project findProjectById(String apiKey) {
        WebTarget target = this.client.target(BASE_URL).path("project").path("apikey").path(apiKey);
        Invocation.Builder invocationBuilder = target.request("application/json;charset=UTF-8");
        Response response = invocationBuilder.get();

        return response.readEntity(ProjectContainer.class).getData();
    }

    public void releaseVersion(ReleaseConfig config, List<File> files, String changelog) {
        WebTarget target = client.target(BASE_URL).path("release");

        FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart()
                .field("title", config.getTitle())
                .field("changelog", changelog);

        files.forEach((file) -> {
            multipart.getBodyParts().add(new FileDataBodyPart("file", file));
        });

        Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            ProjectContainer bean = response.readEntity(ProjectContainer.class);

            System.out.println(String.format("Versão [%s] publicada com sucesso no projeto [%s]", config.getTitle(), bean.getData().getTitle()));
            return;
        }

        System.out.println("Erro ao publicar versão!");
    }

}
