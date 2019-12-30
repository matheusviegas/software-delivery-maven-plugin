package br.com.mvsouza.plugins.beansws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author matheus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Upload {

    private Integer id;
    private String title;
    private String generatedFileName;
    private String url;

    public Upload() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
