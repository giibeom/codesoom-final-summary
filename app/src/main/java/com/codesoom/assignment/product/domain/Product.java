package com.codesoom.assignment.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String maker;

    private Integer price;

    private String imageUrl;

    @Builder
    public Product(final Long id, final String name,
                   final String maker, final Integer price, final String imageUrl) {
        this.id = id;
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    /**
     * 상품 정보를 수정합니다. <br>
     * 필드가 null일 경우 수정하지 않습니다.
     *
     * @param updateProduct 수정할 상품 정보
     */
    public final void update(final Product updateProduct) {
        updateName(updateProduct.getName());
        updateMaker(updateProduct.getMaker());
        updatePrice(updateProduct.getPrice());
        updateImageUrl(updateProduct.getImageUrl());
    }


    private void updateName(final String name) {
        if (name != null) {
            this.name = name;
        }
    }

    private void updateMaker(final String maker) {
        if (maker != null) {
            this.maker = maker;
        }
    }

    private void updatePrice(final Integer price) {
        if (price != null) {
            this.price = price;
        }
    }

    private void updateImageUrl(final String imageUrl) {
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}
