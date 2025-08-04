package com.loopers.domain.brand;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandInfo findBy(Long id) {
        Brand brand = brandRepository.findBy(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "[brandId = " + id + "] 존재하지 않는 브랜드입니다."));
        return BrandInfo.from(brand);
    }
}
