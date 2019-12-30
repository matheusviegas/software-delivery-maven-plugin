package br.com.mvsouza.plugins.beansws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 *
 * @author matheus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Version {

    private Integer id;
    private String title;
    private List<Upload> files;

    public Version() {
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

    public List<Upload> getFiles() {
        return files;
    }

    public void setFiles(List<Upload> files) {
        this.files = files;
    }

}
