package customer.aireport.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;

@Configuration
public class BeanInfoConfig implements ApplicationListener<ContextRefreshedEvent> {
    
    // @Autowired
    // private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // if (event.getApplicationContext().equals(this.applicationContext)) {
        //     System.out.println("\n=== Spring Beans Information ===");
        //     String[] beanNames = applicationContext.getBeanDefinitionNames();
        //     Arrays.sort(beanNames);
            
        //     for (String beanName : beanNames) {
        //         Object bean = applicationContext.getBean(beanName);
        //         System.out.printf("Bean Name: %-50s | Type: %s%n", 
        //             beanName, 
        //             bean.getClass().getName());
        //     }
        //     System.out.println("================================\n");
        // }
    }
}