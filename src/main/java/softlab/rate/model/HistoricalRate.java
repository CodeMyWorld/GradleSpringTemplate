package softlab.rate.model;

import java.util.List;

/**
 * Created by alex on 17-1-22.
 */
public class HistoricalRate {
    private List<Double> values;
    private String title;
    private String fromCid;
    private String toCid;
    private long start;

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getFromCid() {
        return fromCid;
    }

    public void setFromCid(String fromCid) {
        this.fromCid = fromCid;
    }

    public String getToCid() {
        return toCid;
    }

    public void setToCid(String toCid) {
        this.toCid = toCid;
    }
}
