package com.example.gkudva.flickviewer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gkudva on 14/09/17.
 */

public class VideoResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<FlickVideo> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<FlickVideo> getFlickVideos() {
        return results;
    }

    public void setFlickVideos(List<FlickVideo> results) {
        this.results = results;
    }


}
