package softlab.rate.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by alex on 17-1-20.
 */

@Entity(name = "Rate")
@Table(name = "rate")
public class Rate implements Serializable {
    private static final long serialVersionUID = 1L;
    public Rate(){super();}

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "rid", unique = true)
    private String rid;

    @Column(name="date")
    private Long date;

    @Column(name="value")
    private Double value;

    @ManyToOne
    @JoinColumn(name="cid")
    private Currency currency;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
