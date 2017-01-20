package softlab.rate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import softlab.rate.model.RateModel;
import softlab.rate.service.RateService;
import softlab.rate.util.ResponseUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by alex on 17-1-20.
 */
@RestController
@RequestMapping("/rate")
public class RateController {
    @Resource(name = "rateService")
    private RateService rateService;

    @Resource(name = "responseUtil")
    private ResponseUtil responseUtil;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentRate(@RequestParam(value = "from") String from){
        List<RateModel> data = rateService.getCurrentRate(from);
        return responseUtil.getSuccessResponseEntity(data);
    }
}
