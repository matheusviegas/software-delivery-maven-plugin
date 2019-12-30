package br.com.mvsouza.plugins;

/**
 *
 * @author Matheus
 */
public class ReleaseConfig {

    private String title;
    private String apiKey;
    private String releaseNotesFile;
    private String buildDirectory;
    private String[] artifacts;

    public ReleaseConfig() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReleaseNotesFile() {
        return releaseNotesFile;
    }

    public void setReleaseNotesFile(String releaseNotesFile) {
        this.releaseNotesFile = releaseNotesFile;
    }

    public String getBuildDirectory() {
        return buildDirectory;
    }

    public void setBuildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    public String[] getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(String[] artifacts) {
        this.artifacts = artifacts;
    }

}
