package softlab.rate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import softlab.rate.entity.Currency;
import softlab.rate.model.CurrencyModel;
import softlab.rate.service.ICurrencyService;
import softlab.rate.util.ResponseUtil;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by alex on 17-1-17.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyController {
    @Resource(name = "currencyService")
    private ICurrencyService currencyService;

    @Resource(name = "responseUtil")
    private ResponseUtil responseUtil;

    @RequestMapping(value="/currencies", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrencies(@RequestParam(value="cid", required = false) String cid,
                                        @RequestParam(value="lan", required = false, defaultValue = "en") String lan){
        if(cid == null){
            List<CurrencyModel> data = currencyService.getCurrencyList(lan);
            return responseUtil.getSuccessResponseEntity(data);
        }else {
            //TODO return the queried one
            return null;
        }
    }

    @RequestMapping(value="/add", method = RequestMethod.POST)
    public ResponseEntity<?> addCurrency(@RequestParam(value="code") String code){
        Currency currency = currencyService.addCurrencyByCode(code);
        if(currency != null){
            return responseUtil.getSuccessResponseEntity(currency);
        }else {
            //TODO need a properties file instead of hard coded
            return responseUtil.getErrorResponseEntity("already existed", "600");
        }
    }
}
