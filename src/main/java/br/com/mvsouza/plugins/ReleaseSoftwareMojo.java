package br.com.mvsouza.plugins;

import br.com.mvsouza.plugins.beansws.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "release", defaultPhase = LifecyclePhase.NONE)
public class ReleaseSoftwareMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            System.out.println("*********************************************************");
            System.out.println("************* SOFTWARE DELIVERY PLUGIN ******************");
            System.out.println("*********************************************************");

            ReleaseConfig config = parseReleaseDetailsFile();
            ServiceClient client = new ServiceClient(config);

            if (!validateVersionBeforeReleasing(config, client)) {
                System.out.println("Versão já publicada.");
                return;
            }

            File releaseNotesFile = getFileFromResources("softwaredelivery", config.getReleaseNotesFile());
            String releaseNotesStr = releaseNotesFile != null ? new String(Files.readAllBytes(releaseNotesFile.toPath()), Charset.forName("UTF-8")) : "";

            String p = project.getBasedir().toPath().resolve(config.getBuildDirectory()).toString();
            File artifact = new File(compress(config, p));

            client.releaseVersion(config, artifact, releaseNotesStr);
        } catch (Exception e) {
            getLog().error("Erro fatal ao gerar a release.", e);
        }

    }

    private File getFileFromResources(String... path) {
        if (resources.isEmpty()) {
            return null;
        }

        Path pathToResource = Paths.get(resources.get(0).getDirectory());

        for (String p : path) {
            pathToResource = pathToResource.resolve(p);
        }

        return pathToResource.toFile();
    }

    public static String compress(ReleaseConfig config, String dirPath) {
        final Path sourceDir = Paths.get(dirPath);
        String zipFileName = sourceDir.resolve(config.getTitle() + ".zip").toString();
        List<String> art = Arrays.asList(config.getArtifacts());

        try {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    if (!art.contains(file.toFile().getName())) {
                        return FileVisitResult.CONTINUE;
                    }

                    try {
                        Path targetFile = sourceDir.relativize(file);
                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                        byte[] bytes = Files.readAllBytes(file);
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zipFileName;
    }

    private void zipArtifact(ReleaseConfig config) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Test String");

        File artifact = project.getBasedir().toPath().resolve(config.getBuildDirectory()).resolve(config.getArtifacts()[0]).toFile();

        File zippedArtifact = new File(artifact.getParentFile().toPath().resolve(config.getTitle() + ".zip").toString());

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zippedArtifact))) {
            ZipEntry artifactEntry = new ZipEntry(artifact.getName());
            out.putNextEntry(artifactEntry);

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
        }
    }

    private ReleaseConfig parseReleaseDetailsFile() {
        ReleaseConfig releaseConfig = null;

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            File file = getFileFromResources("softwaredelivery", "release-config.yml");
            releaseConfig = mapper.readValue(file, ReleaseConfig.class);
            System.out.println(ReflectionToStringBuilder.toString(releaseConfig, ToStringStyle.MULTI_LINE_STYLE));
        } catch (Exception e) {
            getLog().error("Erro ao ler arquivo de informações de lançamento de versão.", e);
        }

        return releaseConfig;
    }

    private boolean validateVersionBeforeReleasing(ReleaseConfig config, ServiceClient client) {
        Project proj = client.findProjectById(config.getApiKey());

        if (proj == null) {
            return false;
        }

        return proj.getVersions().stream().filter(v -> v.getTitle().equals(config.getTitle())).count() == 0;
    }
}
