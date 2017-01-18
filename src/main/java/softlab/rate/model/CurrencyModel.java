package softlab.rate.model;

import softlab.rate.entity.Currency;

/**
 * Created by alex on 17-1-17.
 */
public class CurrencyModel {
    private String cid;
    private String name;
    private String code;
    private String icon;

    public CurrencyModel(Currency currency, String name, String icon){
        this.cid = currency.getCid();
        this.code = currency.getCode();
        this.name = name;
        this.icon = icon;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
