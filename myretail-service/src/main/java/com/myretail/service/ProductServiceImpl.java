package com.myretail.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.myretail.model.CurrentPrice;
import com.myretail.model.Product;
import com.myretail.repository.ProductRepository;
import com.myretail.vo.CurrentPriceVo;
import com.myretail.vo.ProductVo;

@Service
public class ProductServiceImpl implements ProductService{

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.rest-api-url}")
    private String apiEndpointURL;

    @Override
    public List<Product> fetchAllProducts() {
        return null;
    }

    @Override
    public Product insertProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public ProductVo fetchProductDetailsById(int prodId) {
        Product product = productRepository.findById(prodId).get();
        logger.info("product id:{}",product.getProductId());
        ProductVo prodResponse = null;
        prodResponse = generateProductResponse(product, null);
        return prodResponse;
    }

    /**
     * Fetching the data by ID and consuming the data from eternal API
     * and preparting the response with product name
     * @param prodId
     * @return
     */
    @Override
    public ProductVo fetchDataForID(int prodId) throws JSONException {
        logger.debug("Entering :: Class - ProductServiceImpl :: Method - fetchDataForID()");
        String productName = null;
        ProductVo prodResponse = null;
        Product product = productRepository.findById(prodId).get();
        if(product != null) {
            //getting the product name from external API
            productName = getProductNameByExtApiCall(prodId);
            prodResponse = generateProductResponse(product, productName);
        }else{
            logger.debug("Product Not Found :: Having Exception while Fetching product data from DB ");
        }
        return prodResponse;
    }

    /**
     * Updating the data by Id
     * @param productVo
     * @return
     */
    @Override
    public boolean updateProductById(ProductVo productVo) {
        logger.debug("Entering :: Class - ProductServiceImpl :: Method - updateProductById()");
        boolean result = false;
        try {
            Product product = getProductInstance(productVo);
            productRepository.save(product);
            result = true;
        } catch (Exception ex) {
            logger.error("Product Not Found in database " + ex);
        }
        return result;
    }


    private Product getProductInstance(ProductVo productVo) {
        Product product = new Product();
        CurrentPrice currentPrice = new CurrentPrice();
        product.setProductId(productVo.getProductId());
        currentPrice.setCurrencyCode(productVo.getCurrentPrice().getCurrencyCode());
        currentPrice.setValue(productVo.getCurrentPrice().getValue());
        product.setCurrentPrice(currentPrice);
        return product;
    }

    /**
     * Preparing the response.
     * @param product
     * @param productName
     * @return
     */
    private ProductVo generateProductResponse(Product product, String productName) {
        logger.debug("Entering :: Class - ProductServiceImpl :: Method - generateProductResponse()");
        ProductVo prodResponse = new ProductVo();
        CurrentPriceVo currentPriceResponse= new CurrentPriceVo();
        try{
            currentPriceResponse.setCurrencyCode(product.getCurrentPrice().getCurrencyCode());
            currentPriceResponse.setValue(product.getCurrentPrice().getValue());

            prodResponse.setProductId(product.getProductId());
            prodResponse.setCurrentPrice(currentPriceResponse);
            prodResponse.setName(productName);
        }
        catch(Exception e) {
            logger.error("Exception for setting the current price values"+ e.getCause());
            logger.error("----"+ e.getLocalizedMessage());
        }
        return prodResponse;
    }

    /**
     * calling an external api to consume the product name
     * @param prodId
     * @return
     */
    private String getProductNameByExtApiCall(int prodId) throws JSONException {

        logger.debug("Entering :: Class - ProductServiceImpl :: Method - getProductNameByRemoteCall()");
        String prodUri="products/v3/";
        String productName= null;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiEndpointURL+prodUri+prodId).queryParam("excludes","fields=descriptions&id_type=TCI N&key=43cJWpLjH8Z8oR18KdrZDBKAgLLQKJjz");
            //TO-DO we can use "exchange" method if we have request headers are available.
            String jsonResponse = restTemplate.getForObject(builder.build().encode().toUri(),String.class);
            //TO-Do Assuming received the response
            if(jsonResponse != null) {
                JSONObject jsonObj=new JSONObject(jsonResponse);
                logger.debug("JSON Response from rest api :" + jsonResponse.toString());
                if(jsonObj.getJSONObject("name") != null)
                    productName = jsonObj.getString("name");
            }
            else{
                logger.debug("Product Name is not availbale in the response.");
            }
        }catch (RestClientException e) {
            logger.error("Rest end point is unavailable  :" + apiEndpointURL+ prodUri+ prodId);
            logger.error("Not having any response- ", e.getCause());
        }
        return productName;
    }


    /**
     * Preparing the payload
     * @param prodId
     * @return
     * @throws JSONException
     */

    private JSONObject prepareRequest(int prodId) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", prodId);
        logger.debug("request payload");
        return json;
    }
}
