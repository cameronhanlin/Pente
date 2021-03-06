package Pente.Service;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GrayService {

    public ColorScale fetchGrayColorData(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://www.254shadesofgray.com/api/GrayOfTheDay", ColorScale.class);
    }

    public ColorScale fetchOffsetColor(int currentNumber){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://www.254shadesofgray.com/api/gray/";

        if(currentNumber >127){
            currentNumber = currentNumber - 120;
        } else {
            currentNumber = currentNumber + 120;
        }

        url = url+currentNumber;
        return restTemplate.getForObject(url, ColorScale.class);
    }


}