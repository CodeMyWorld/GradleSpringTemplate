package softlab.rate.service.imp;

import org.springframework.stereotype.Service;
import softlab.rate.entity.Currency;
import softlab.rate.dao.ICurrencyDao;
import softlab.rate.dao.common.IOperations;
import softlab.rate.model.CurrencyModel;
import softlab.rate.service.ICurrencyService;
import softlab.rate.service.common.AbstractService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 17-1-17.
 */
@Service("currencyService")
public class CurrencyService extends AbstractService<Currency> implements ICurrencyService {
    @Resource(name="currencyDao")
    private ICurrencyDao currencyDao;

    @Override
    protected IOperations<Currency> getDao() {
        return currencyDao;
    }

    @Override
    public List<CurrencyModel> getCurrencyList(String lan) {
        List<Currency> currencies = currencyDao.findAll();
        List<CurrencyModel> currencyModels = new ArrayList<CurrencyModel>();
        for(Currency currency : currencies){
            String displayName = java.util.Currency.getInstance(currency.getCode()).getDisplayName(Locale.forLanguageTag(lan));
            //TODO icon path
            String icon = "path/" + currency.getCode();
            currencyModels.add(new CurrencyModel(currency, displayName, icon));
        }
        return currencyModels;
    }

    @Override
    public Currency addCurrencyByCode(String code) {
        List<Currency> currency = queryList("code", code);
        if(currency.size() == 0){
            Currency newCurrency = new Currency();
            newCurrency.setCode(code);
            create(newCurrency);
            return newCurrency;
        }
        return null;
    }
}
