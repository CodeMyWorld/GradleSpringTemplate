package softlab.rate.dao.imp;

import org.springframework.stereotype.Repository;
import softlab.rate.entity.Currency;
import softlab.rate.dao.CurrencyDao;
import softlab.rate.dao.common.AbstracHibernateDao;

/**
 * Created by alex on 17-1-17.
 */
@Repository("currencyDao")
public class CurrencyDaoImp extends AbstracHibernateDao<Currency> implements CurrencyDao {
    public CurrencyDaoImp(){
        super();
        setClazz(Currency.class);
    }
}
