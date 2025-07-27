package com.loopers.domain.brand;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public Optional<Brand> findBy(Long id) {
        return brandRepository.findBy(id);
    }
}
