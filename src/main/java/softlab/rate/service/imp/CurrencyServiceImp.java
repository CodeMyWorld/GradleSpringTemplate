package softlab.rate.service.imp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import softlab.rate.entity.Currency;
import softlab.rate.dao.CurrencyDao;
import softlab.rate.dao.common.IOperations;
import softlab.rate.model.CurrencyModel;
import softlab.rate.service.CurrencyService;
import softlab.rate.service.RateService;
import softlab.rate.service.common.AbstractService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by alex on 17-1-17.
 */
@Service("currencyService")
public class CurrencyServiceImp extends AbstractService<Currency> implements CurrencyService {
    @Resource(name="currencyDao")
    private CurrencyDao currencyDao;

    @Resource(name = "rateService")
    private RateService rateService;

    @Value("#{currency_country}")
    private Properties codeCountry;

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
            String icon = codeCountry.get(currency.getCode()).toString().toLowerCase()+".svg";
            currencyModels.add(new CurrencyModel(currency, displayName, icon));
        }
        return currencyModels;
    }

    @Override
    public Currency addCurrencyByCode(String code) {
        code = code.toUpperCase();
        List<Currency> currency = queryList("code", code);
        if(currency.size() == 0 && codeCountry.keySet().contains(code)){
            Currency newCurrency = new Currency();
            newCurrency.setCode(code);
            create(newCurrency);
            if(!code.equals("USD")){
                rateService.updateRateForCurrency(newCurrency.getCode());
            }
            return newCurrency;
        }
        return null;
    }
}
