package com.loopers.domain.product;

import com.loopers.domain.PageRequest;
import com.loopers.domain.PageResponse;
import com.loopers.infrastructure.product.ProductParams;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<Product> getBy(Long id) {
        return productRepository.findBy(id);
    }

    public PageResponse<ProductView> search(PageRequest<ProductCommand.Search> command) {
        PageRequest<ProductParams.Search> productParams = command.map(ProductCommand.Search::toParams);

        return productRepository.search(productParams);
    }

    public Long searchCount(ProductCommand.SearchCount command) {

        return productRepository.searchCount(command.toParams());
    }

    public List<ProductInfo> findAllBy(List<Long> productIds) {
        return productRepository.findAllBy(productIds).stream().map(ProductInfo::of).collect(Collectors.toList());
    }

    public Optional<Product> findBy(Long id) {
        return productRepository.findBy(id);
    }
}
