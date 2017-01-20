package softlab.rate.dao.imp;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;
import softlab.rate.dao.RateDao;
import softlab.rate.dao.common.AbstracHibernateDao;
import softlab.rate.entity.Rate;

import java.util.List;

/**
 * Created by alex on 17-1-20.
 */
@Repository("rateDao")
public class RateDaoImp extends AbstracHibernateDao<Rate> implements RateDao {
    public RateDaoImp(){
        super();
        setClazz(Rate.class);
    }

    @Override
    public List<Rate> getLatestUpdateDate() {
        //TODO deprecated API
        DetachedCriteria maxDate = DetachedCriteria.forClass(Rate.class)
                .setProjection(Projections.max("date"));
        Criteria criteria = getCurrentSesstion().createCriteria(Rate.class)
                .add(Property.forName("date").eq(maxDate));
        return criteria.list();
    }
}
