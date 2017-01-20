
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import softlab.rate.entity.Rate;
import softlab.rate.model.CurrencyModel;
import softlab.rate.service.CurrencyService;
import softlab.rate.service.RateService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * Created by alex on 17-1-20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/mvc-dispatcher-servlet.xml", "classpath:/spring-hibernate.xml"})
@WebAppConfiguration
@Transactional


public class BaseTest {

    @Resource(name = "rateService")
    private RateService rateService;

    @Value("#{currency_country}")
    private Properties currency;



    @Test
    @Rollback(false)
    public void test(){
//        rateService.updateRateForCurrency("CNY");
//        rateService.updateRate();
        System.out.println(currency.getProperty("CUP"));
    }
}
