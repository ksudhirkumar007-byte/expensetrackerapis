import feign.Feign;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public CategoryClient categoryClient() {
        return Feign.builder()
                .client(new OkHttpClient())        // Fast HTTP client
                .target(CategoryClient.class, "https://categoryapis.onrender.com");
    }
}