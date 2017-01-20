package softlab.rate.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by alex on 17-1-17.
 */

@Entity(name = "Currency")
@Table(name = "currency")
public class Currency implements Serializable{
    private static final long serialVersionUID = 1L;
    public Currency(){super();}

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "cid", unique = true)
    private String cid;

    @Column(name = "code", length = 5)
    private String code;

    @OneToMany (mappedBy = "currency", cascade = CascadeType.ALL)
    private Set<Rate> rates;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Rate> getRates() {
        return rates;
    }

    public void setRates(Set<Rate> rates) {
        this.rates = rates;
    }
}
