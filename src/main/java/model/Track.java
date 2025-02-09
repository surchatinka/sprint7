package model;

public class Track {
    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

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
