package com.mysite.jjw.service;

import com.mysite.jjw.DTO.ProductDTO;
import com.mysite.jjw.Handler.DataNotFoundException;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.nio.file.Paths;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final String uploadURL = "C:\\Project\\jjw\\src\\main\\resources\\static\\product-images"; // 이미지 업로드 경로


    // 상품 정렬 이랑 검색 둘다 하는 경우
    public Page<Review> getReviewInfo(Pageable pageable,String sort,Long product_idx){
        return reviewRepository.findAllWithSortAndReviewProductidx(sort,pageable,product_idx);
    }

    // 상품 정렬만 하는 경우
    public Page<Product> getAllProducts(Pageable pageable,String sort) {
        return productRepository.findAllWithSort(sort,pageable); // 모든 상품 목록 반환
    }

    //상품 검색 만 하는 경우 
    public Page<Product> searchProduct(String keyword, String sort , Pageable pageable) {
        return productRepository.findAllWithSearchAndSort(keyword, sort, pageable);
    }

    // 상품 상세 페이지 정보 가져오기
    public Product getProductInfo(Long product_idx) {
        // 상품 조회
        Optional<Product> productOpt = this.productRepository.findById(product_idx);  // ✅ 수정됨

        if (!productOpt.isPresent()) {
            throw new DataNotFoundException("Product not found"); // 상품이 없으면 예외 처리
        }

        Product product = productOpt.get(); // Optional에서 실제 Product 객체를 꺼냄
        product.setProduct_view(product.getProduct_view() + 1); // 조회수 증가

        this.productRepository.save(product); // 조회수 증가한 후 저장 (업데이트)

        return product;
    }


    // 상품 등록 메소드
    public Product registerProduct(ProductDTO productDTO, MultipartFile[] files) throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {

            if (file.isEmpty() || file.getSize() <= 0) {
                throw new IOException("파일이 비어 있거나 크기가 0입니다.");
            }

            String originalName = file.getOriginalFilename(); // 기존파일이름
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1); // 파일 명 이랑 확장자만 추출
            String uuid = UUID.randomUUID().toString(); // 중복없이 랜덤한 이름으로 만들기
            String newFileName = uuid + "_" + fileName; // ✅ 절대경로 없이 파일명만 저장
            Path savePath = Paths.get(uploadURL, newFileName); // 파일 을 저장할 전체 경로 정하기
            
            file.transferTo(savePath.toFile()); // 경로에 파일을 저장 

            // 파일명 리스트에 추가
            fileNames.add(newFileName);
        }


        // Product 객체 생성
        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setProduct_price(productDTO.getProduct_price());
        product.setProduct_pcs(productDTO.getProduct_pcs());
        product.setProductContent(productDTO.getProductContent());
        product.setProduct_user(productDTO.getProduct_user());
        product.setProduct_at(LocalDate.now());
        product.setProduct_image(String.join(",", fileNames)); // ✅ 파일명만 저장 여러 개 일경우 , 로 구분

        // 상품 저장
        return productRepository.save(product);
    }
}










