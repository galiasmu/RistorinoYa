package ar.edu.ubp.das.ristorino.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Pointcut("execution(* ar.edu.ubp.das.ristorino.components.RestauranteFacadeFactory.*(..))")
    public void restauranteFacadeMethods() {}

    @Before("restauranteFacadeMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Ejecutando método: {} con argumentos: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "restauranteFacadeMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Método ejecutado: {} con resultado: {}",
                joinPoint.getSignature().getName(),
                result);
    }

}
