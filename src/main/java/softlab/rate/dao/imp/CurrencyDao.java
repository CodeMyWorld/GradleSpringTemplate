package softlab.rate.dao.imp;

import org.springframework.stereotype.Repository;
import softlab.rate.entity.Currency;
import softlab.rate.dao.ICurrencyDao;
import softlab.rate.dao.common.AbstracHibernateDao;

/**
 * Created by alex on 17-1-17.
 */
@Repository("currencyDao")
public class CurrencyDao extends AbstracHibernateDao<Currency> implements ICurrencyDao {
    public CurrencyDao(){
        super();
        setClazz(Currency.class);
    }
}
