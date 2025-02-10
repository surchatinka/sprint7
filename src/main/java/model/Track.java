package model;

import lombok.Data;
@Data
public class Track {

    public Track(int track) {
        this.track = track;
    }
    public Track(){}

    @Override
    public String toString() {
        return String.format("{\"track\": %s}",track);
    }

    private int track;
}
