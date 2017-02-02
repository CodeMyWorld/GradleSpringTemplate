package softlab.rate.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by alex on 17-1-30.
 */
@Component("AOP")
@Aspect
public class AspectTest {

    @Pointcut("execution(* getHistoricalRates(..))")
    private void aspectForTransfer(){

    }

    @Before("aspectForTransfer() && args(fromCid, ..))")
    public void beforeTransfer(String fromCid){
        System.out.println("fromCid: "+fromCid);
    }
}
