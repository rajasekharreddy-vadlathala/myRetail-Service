package com.myretail.rest;

import com.myretail.model.Product;
import com.myretail.repository.ProductRepository;
import com.myretail.service.ProductService;
import com.myretail.vo.ProductResponse;
import com.myretail.vo.ProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    ProductService productService;

    Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * Method is for insert the data
     * @param product
     * @return
     */
    @PostMapping(path="/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveProduct(@RequestBody Product product){

        logger.debug("Entering :: Class - ProductController :: Method - saveProduct()");
        ResponseEntity<?> response = null;
        try{
            response = ResponseEntity.status(HttpStatus.OK).body(productService.insertProduct(product));
            logger.debug("successfully load the product data : ");
        }catch(Exception ex){
            logger.error("ProductController - Error" + ex.getMessage() + "Error Cause" + ex.getCause());
            return getResponse(logger, ex);
        }
        logger.debug("Completed :: Class - ProductController :: Method - saveProduct()");
        return response;
    }

    @GetMapping(path="/products",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProdutDetails(){

        logger.info("Entering :: Class - ProductController :: Method - getProdutDetails()");
        ResponseEntity<?> response = null;
        Product product = null;
        try{
            response = ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
        }catch(Exception ex){
            logger.error("ProductController - Error" + ex.getMessage() + "Error Cause" + ex.getCause());
            return getResponse(logger, ex);
        }
        logger.debug("Completed :: Class - ProductController :: Method - getProdutDetails()");
        return response;
    }

    /**
     * Method is used to fetch specific product id data
     * @param prodId
     * @return
     */

    @GetMapping(path="/products/info/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProdutDetailsById(@PathVariable("id") int prodId){

        logger.debug("Entering :: Class - ProductController :: Method - getProdutDetailsById(), prodId : " + prodId);
        ResponseEntity<?> response = null;
        Product product = null;
        try{
            //response = ResponseEntity.status(HttpStatus.OK).body(repository.findById(prodId));
            response = ResponseEntity.status(HttpStatus.OK).body(productService.fetchProductDetailsById(prodId));
        }catch(Exception ex){
            logger.error("ProductController - Error" + ex.getMessage() + "Error Cause" + ex.getCause());
            return getResponse(logger, ex);
        }
        logger.debug("Completed :: Class - ProductController :: Method - getProdutDetailsById()");
        return response;
    }

    /**
     * Fetching data by Id with price details.
     * @param prodId
     * @return
     */

    @GetMapping(path="/products/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductByID(@PathVariable("id") int prodId){

        logger.debug("Entering :: Class - ProductController :: Method - getProductByID(), prodId : " + prodId);
        ResponseEntity<?> response = null;
        Product product = null;
        try{
            response = ResponseEntity.status(HttpStatus.OK).body(productService.fetchDataForID(prodId));
        }catch(Exception ex){
            logger.error("ProductController - Error" + ex.getMessage() + "Error Cause" + ex.getCause());
            return getResponse(logger, ex);
        }
        logger.debug("Completed :: Class - ProductController :: Method - getProductByID()");
        return response;
    }

    /**
     * Updating price data by Id.
     * @param product
     * @param prodId
     * @return
     */

    @PutMapping(value = "/products/{id}", produces =MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePriceDetails(@RequestBody ProductVo product, @PathVariable("id") int prodId) {
        logger.debug("Entering :: Class - ProductController :: Method - updatePriceDetails(), prodId : " + prodId);
        ResponseEntity<?> response = null;
        try{
            boolean result  = productService.updateProductById(product);
            if (result) {
                response = ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception exc) {
            logger.error("ProductController::updatePriceDetails::Exception::" + exc.getStackTrace());
            return getResponse(logger, exc);
        }
        logger.debug("Completed :: Class - ProductController :: Method - updatePriceDetails()");
        return response;
    }


    /**
     *
     * @param logger
     * @param e
     * @return
     */
    public ResponseEntity<?> getResponse(Logger logger, Exception e) {

        ProductResponse errormessage = new ProductResponse();
        errormessage.copy(logger, e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errormessage);
    }
}