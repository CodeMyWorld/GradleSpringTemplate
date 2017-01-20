package softlab.rate.service;

import softlab.rate.entity.Currency;
import softlab.rate.dao.common.IOperations;
import softlab.rate.model.CurrencyModel;

import java.util.List;

/**
 * Created by alex on 17-1-17.
 */
public interface CurrencyService extends IOperations<Currency>{
    List<CurrencyModel> getCurrencyList(String lan);
    Currency addCurrencyByCode(String code);
}
