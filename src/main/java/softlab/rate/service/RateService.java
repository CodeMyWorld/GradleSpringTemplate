package softlab.rate.service;

import softlab.rate.dao.common.IOperations;
import softlab.rate.entity.Rate;
import softlab.rate.model.HistoricalRate;
import softlab.rate.model.RateModel;

import java.util.List;

/**
 * Created by alex on 17-1-20.
 */
public interface RateService extends IOperations<Rate> {
    void updateRate();
    void updateRateForCurrency(String currencyCode);
    List<Rate> getLatestUpdateRates();
    List<RateModel> getCurrentRate(String fromCid);
    HistoricalRate getHistoricalRates(String fromCid, String toCid, long start, long end);
}
