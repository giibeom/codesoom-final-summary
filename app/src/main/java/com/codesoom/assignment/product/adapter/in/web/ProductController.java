package com.codesoom.assignment.product.adapter.in.web;

import com.codesoom.assignment.product.adapter.in.web.dto.request.ProductCreateRequestDto;
import com.codesoom.assignment.product.adapter.in.web.dto.request.ProductUpdateRequestDto;
import com.codesoom.assignment.product.application.port.ProductUseCase;
import com.codesoom.assignment.product.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public List<Product> list() {
        return productUseCase.getProducts();
    }

    @GetMapping("{id}")
    public Product detail(@PathVariable final Long id) {
        return productUseCase.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Product create(@RequestBody @Valid final ProductCreateRequestDto productCreateRequestDto) {
        return productUseCase.createProduct(productCreateRequestDto);
    }

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public Product update(@PathVariable final Long id,
                          @RequestBody @Valid final ProductUpdateRequestDto productUpdateRequestDto) {
        return productUseCase.updateProduct(id, productUpdateRequestDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void destroy(@PathVariable final Long id) {
        productUseCase.deleteProduct(id);
    }
}
