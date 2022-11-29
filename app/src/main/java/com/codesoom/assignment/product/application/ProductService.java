package com.codesoom.assignment.product.application;

import com.codesoom.assignment.product.application.port.ProductUseCase;
import com.codesoom.assignment.product.application.port.command.ProductCreateRequest;
import com.codesoom.assignment.product.application.port.command.ProductUpdateRequest;
import com.codesoom.assignment.product.domain.Product;
import com.codesoom.assignment.product.domain.ProductRepository;
import com.codesoom.assignment.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService implements ProductUseCase {
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(final Long id) {
        return findProduct(id);
    }

    public Product createProduct(final ProductCreateRequest productCreateRequest) {
        return productRepository.save(productCreateRequest.toEntity());
    }

    public Product updateProduct(final Long id,
                                 final ProductUpdateRequest productUpdateRequest) {
        Product product = findProduct(id);

        product.update(productUpdateRequest.toEntity());

        return product;
    }

    public Product deleteProduct(final Long id) {
        Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    private Product findProduct(final Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }
}
