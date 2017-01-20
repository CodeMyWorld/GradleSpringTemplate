package softlab.rate.dao;

import softlab.rate.dao.common.IOperations;
import softlab.rate.entity.Rate;

import java.util.List;

/**
 * Created by alex on 17-1-20.
 */
public interface RateDao extends IOperations<Rate> {
    List<Rate> getLatestUpdateDate();
}
